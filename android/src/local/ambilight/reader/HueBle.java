package local.ambilight.reader;

import android.bluetooth.*;
import android.bluetooth.le.*;
import android.content.*;
import android.os.*;
import org.json.*;
import java.util.*;

/** Local Hue BLE controller. All connection and GATT state belongs to the main looper. */
public final class HueBle {
    static final UUID MODEL=UUID.fromString("00002a24-0000-1000-8000-00805f9b34fb");
    static final UUID POWER=UUID.fromString("932c32bd-0002-47a2-835a-a8d455b859dd");
    static final UUID BRIGHTNESS=UUID.fromString("932c32bd-0003-47a2-835a-a8d455b859dd");
    static final UUID TEMPERATURE=UUID.fromString("932c32bd-0004-47a2-835a-a8d455b859dd");
    static final UUID COLOR=UUID.fromString("932c32bd-0005-47a2-835a-a8d455b859dd");
    public static final class Found {
        public final BluetoothDevice device; public final String name; public final int rssi;
        Found(BluetoothDevice d,String n,int r){device=d;name=n;rssi=r;}
        public String label(){return name+" · "+rssi+" dBm";}
    }
    public volatile String status="Ampul bekleniyor",model="",operation="",snapshot="",trace="",source="left";
    public volatile boolean scanning=false,ready=false,syncing=false,automatic=true,screenOn=true,lightOn=false;
    public volatile long writes=0,failures=0,lastWrite=0;
    public volatile int brightnessPercent=200,lastBrightness=0,recoveries=0;
    private final Context context; private final SharedPreferences prefs; private final boolean discoveryOnly;
    private final Handler h=new Handler(Looper.getMainLooper()); private final BluetoothAdapter adapter;
    private final LinkedHashMap<String,Found> found=new LinkedHashMap<>();
    private final Map<UUID,BluetoothGattCharacteristic> chars=new HashMap<>();
    private final Map<UUID,byte[]> confirmed=new HashMap<>();
    private final ArrayDeque<Op> queue=new ArrayDeque<>();
    // Keep failed registrations only until a fresh session has completed initialization.
    // Never alter radio state or request privileged system permissions.
    private final ArrayList<BluetoothGatt> retiring=new ArrayList<>();
    private BluetoothGatt gatt; private BluetoothDevice selected; private BluetoothLeScanner scanner;
    private Op current; private int busyRetries=0,registrationRetries=0;
    private boolean closed=false,paused=false,initializing=false,testing=false;
    private long nextRetry=0; private String finishMessage;
    private byte[] previousPower,previousBrightness,previousColor,previousTemperature;
    private static final class Op {final UUID id;final byte[] value;Op(UUID i,byte[] v){id=i;value=v;}}

    public HueBle(Context context){this(context,"bluetooth",false);}
    public HueBle(Context context,String preferenceName,boolean discoveryOnly){
        this.discoveryOnly=discoveryOnly;
        this.context=context.getApplicationContext();prefs=this.context.getSharedPreferences(preferenceName,0);
        BluetoothManager manager=this.context.getSystemService(BluetoothManager.class);adapter=manager==null?null:manager.getAdapter();
        automatic=prefs.getBoolean("automatic",true);brightnessPercent=Math.max(0,Math.min(400,prefs.getInt("brightness_percent",prefs.getInt("brightness_multiplier",2)*100)));
        source=BulbPosition.normalize(prefs.getString("position_"+prefs.getString("address",""),"left"));
        PowerManager power=this.context.getSystemService(PowerManager.class);screenOn=power==null||power.isInteractive();
        IntentFilter filter=new IntentFilter();filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);filter.addAction(Intent.ACTION_SCREEN_ON);filter.addAction(Intent.ACTION_SCREEN_OFF);
        if(Build.VERSION.SDK_INT>=33)this.context.registerReceiver(events,filter,Context.RECEIVER_EXPORTED);else this.context.registerReceiver(events,filter);
        if(!discoveryOnly){loadSavedDevice();h.postDelayed(pulse,1000);}
    }
    private boolean allowed(){return Build.VERSION.SDK_INT<31||(context.checkSelfPermission("android.permission.BLUETOOTH_SCAN")==0&&context.checkSelfPermission("android.permission.BLUETOOTH_CONNECT")==0);}
    private void event(String value){trace+=(SystemClock.elapsedRealtime()%100000)+":"+value+"; ";if(trace.length()>2400)trace=trace.substring(trace.length()-2400);}
    public String savedName(){String custom=prefs.getString("custom_name","");return custom.isEmpty()?prefs.getString("name","Hue LCA012"):custom;}
    public void configure(String name,String position,int percent){
        if(name==null||name.trim().length()>40||BulbPosition.index(position)<0||percent<0||percent>400)throw new IllegalArgumentException("Geçersiz ampul ayarı");
        h.post(()->{source=position;brightnessPercent=percent;prefs.edit().putString("custom_name",name.trim()).putString("position_"+id(),position).putInt("brightness_percent",percent).apply();});
    }
    public String customName(){return prefs.getString("custom_name","");}
    public String brightnessLabel(){return String.format(new Locale("tr"),"%.2f×",brightnessPercent/100.0);}
    public static int[] sampleFrame(JSONObject frame,String source)throws JSONException{
        JSONObject layer=frame.getJSONObject("layer1");String[] sides={"left","top","right"};int[][][] colors=new int[3][][];
        for(int s=0;s<3;s++){JSONObject zones=layer.getJSONObject(sides[s]);colors[s]=new int[zones.length()][3];for(int i=0;i<zones.length();i++){JSONObject c=zones.getJSONObject(""+i);colors[s][i]=new int[]{c.getInt("r"),c.getInt("g"),c.getInt("b")};}}
        return BulbPosition.sample(source,colors[0],colors[1],colors[2]);
    }
    public String id(){return prefs.getString("address","");}
    public boolean isLightOn(){return ready&&lightOn&&lastBrightness>0;}
    public boolean isPaused(){return paused||!automatic;}
    public boolean isConnecting(){return initializing||(!ready&&automatic&&!paused);}
    public boolean hasSavedDevice(){return !prefs.getString("address","").isEmpty();}
    public String savedPosition(Found bulb){return prefs.getString("position_"+bulb.device.getAddress(),bulb.device.getAddress().equals(prefs.getString("address",""))?source:"");}
    public void setPosition(String value){
        if(BulbPosition.index(value)<0)throw new IllegalArgumentException("Geçersiz ampul konumu");
        h.post(()->{if(!hasSavedDevice())return;source=value;prefs.edit().putString("position_"+prefs.getString("address",""),value).apply();});
    }
    private void loadSavedDevice(){
        if(discoveryOnly||adapter==null||!allowed())return;
        try{
            String address=prefs.getString("address","");
            if(!address.isEmpty()){selected=adapter.getRemoteDevice(address);loadBaseline();return;}
        }catch(Exception e){status="Kaydedilen ampul bekleniyor.";}
    }
    private final BroadcastReceiver events=new BroadcastReceiver(){public void onReceive(Context c,Intent i){
        String action=i.getAction();
        if(discoveryOnly){if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)&&i.getIntExtra(BluetoothAdapter.EXTRA_STATE,-1)==BluetoothAdapter.STATE_OFF)stopScan();return;}
        if(Intent.ACTION_SCREEN_OFF.equals(action)){screenOn=false;syncing=false;cancelTest();if(ready)restoreInternal();status="TV beklemede; ampul başlangıç ışığında.";}
        else if(Intent.ACTION_SCREEN_ON.equals(action)){screenOn=true;paused=false;nextRetry=0;status="TV açıldı; otomatik Hue bağlantısı hazırlanıyor.";}
        else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
            int state=i.getIntExtra(BluetoothAdapter.EXTRA_STATE,-1);
            if(state==BluetoothAdapter.STATE_OFF||state==BluetoothAdapter.STATE_TURNING_OFF){stopScan();closeConnection();status="Bluetooth bekleniyor.";}
            else if(state==BluetoothAdapter.STATE_ON){nextRetry=0;loadSavedDevice();}
        }
    }};
    public ArrayList<Found> devices(){synchronized(found){return new ArrayList<>(found.values());}}
    public void scan(){h.post(()->{
        if(!allowed()){status="Yakındaki cihazlar izni gerekli.";return;}
        if(adapter==null||!adapter.isEnabled()){status="TV Bluetooth’u kapalı.";return;}
        stopScan();synchronized(found){found.clear();}scanner=adapter.getBluetoothLeScanner();
        if(scanner==null){status="Ampul araması kullanılamıyor.";return;}
        try{scanning=true;status="Hue ampuller aranıyor…";scanner.startScan(null,new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(),scanCallback);h.postDelayed(scanEnd,20000);}catch(Exception e){scanning=false;status="Ampul araması başlatılamadı.";}
    });}
    private final Runnable scanEnd=()->{stopScan();if(!ready)status=devices().isEmpty()?"Ampul bulunamadı; elektriği ve görünürlüğünü kontrol edin.":"Ampulü seçip bağlanın.";};
    private final ScanCallback scanCallback=new ScanCallback(){
        public void onScanResult(int type,ScanResult result){
            if(!allowed())return;ScanRecord record=result.getScanRecord();String name=record==null?null:record.getDeviceName();if(name==null)name=result.getDevice().getName();
            boolean hue=name!=null&&name.toLowerCase(Locale.ROOT).contains("hue");
            if(record!=null&&record.getManufacturerSpecificData(0x100B)!=null)hue=true;
            if(record!=null&&record.getServiceUuids()!=null)for(ParcelUuid u:record.getServiceUuids())if(u.toString().startsWith("932c32bd"))hue=true;
            if(!hue)return;String label=name==null?"Hue ampul":name;
            synchronized(found){if(found.size()<32||found.containsKey(result.getDevice().getAddress()))found.put(result.getDevice().getAddress(),new Found(result.getDevice(),label,result.getRssi()));}
        }
        public void onScanFailed(int code){h.post(()->{scanning=false;status="Ampul arama hatası: "+code;});}
    };
    private void stopScan(){h.removeCallbacks(scanEnd);if(scanning&&scanner!=null&&allowed())try{scanner.stopScan(scanCallback);}catch(Exception ignored){}scanning=false;}
    public void endScan(){stopScan();}
    private void connectGatt(){
        if(closed||selected==null||!allowed()||adapter==null||!adapter.isEnabled()||gatt!=null)return;
        try{status="Kaydedilen ampule bağlanılıyor…";initializing=true;gatt=selected.connectGatt(context,true,callback,BluetoothDevice.TRANSPORT_LE);nextRetry=SystemClock.elapsedRealtime()+10000;}
        catch(Exception e){gatt=null;initializing=false;nextRetry=SystemClock.elapsedRealtime()+10000;status="Ampul bağlantısı tekrar denenecek.";}
    }
    private final BluetoothGattCallback callback=new BluetoothGattCallback(){
        public void onConnectionStateChange(BluetoothGatt connection,int code,int state){h.post(()->{
            if(connection!=gatt)return;
            if(code==0&&state==BluetoothProfile.STATE_CONNECTED){clearOperations();initializing=true;status="Hue bağlantısı doğrulanıyor…";if(!connection.discoverServices())fail("Ampul özellikleri bekleniyor.");}
            else if(state==BluetoothProfile.STATE_DISCONNECTED||code!=0){clearOperations();initializing=false;syncing=false;status="Ampul bağlantısı bekleniyor; otomatik yeniden bağlanılacak.";nextRetry=SystemClock.elapsedRealtime()+5000;}
        });}
        public void onServicesDiscovered(BluetoothGatt connection,int code){h.post(()->{
            if(connection!=gatt||closed)return;if(code!=0){fail("Ampul özellikleri okunamadı.");return;}
            clearOperations();initializing=true;
            for(BluetoothGattService service:connection.getServices())for(BluetoothGattCharacteristic c:service.getCharacteristics())chars.put(c.getUuid(),c);
            if(!chars.containsKey(MODEL)||!chars.containsKey(POWER)||!chars.containsKey(BRIGHTNESS)||!chars.containsKey(COLOR)){paused=true;fail("Bu cihaz Hue renk protokolünü sunmuyor.");return;}
            enqueue(MODEL,null);enqueue(POWER,null);enqueue(BRIGHTNESS,null);if(chars.containsKey(TEMPERATURE))enqueue(TEMPERATURE,null);enqueue(COLOR,null);
        });}
        public void onCharacteristicRead(BluetoothGatt connection,BluetoothGattCharacteristic c,int code){byte[] value=c.getValue();h.post(()->complete(connection,c.getUuid(),value==null?new byte[0]:value.clone(),code));}
        public void onCharacteristicRead(BluetoothGatt connection,BluetoothGattCharacteristic c,byte[] value,int code){byte[] copy=value.clone();h.post(()->complete(connection,c.getUuid(),copy,code));}
        public void onCharacteristicWrite(BluetoothGatt connection,BluetoothGattCharacteristic c,int code){h.post(()->complete(connection,c.getUuid(),null,code));}
    };
    private void enqueue(UUID id,byte[] value){if(value!=null&&Arrays.equals(value,confirmed.get(id)))return;queue.addLast(new Op(id,value));pump();}
    private void pump(){
        if(closed||current!=null||gatt==null||queue.isEmpty())return;
        current=queue.removeFirst();BluetoothGattCharacteristic characteristic=chars.get(current.id);operation=current.id+" / "+(current.value==null?"read":hex(current.value));boolean accepted=false;
        try{
            if(characteristic!=null){
                if(current.value==null)accepted=gatt.readCharacteristic(characteristic);
                else if(Build.VERSION.SDK_INT>=33){int result=gatt.writeCharacteristic(characteristic,current.value,BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);accepted=result==BluetoothStatusCodes.SUCCESS;if(!accepted)operation+=" code="+result;}
                else{characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);characteristic.setValue(current.value);accepted=gatt.writeCharacteristic(characteristic);}
            }
        }catch(SecurityException e){event("system permission "+current.id);renewRegistration();return;}catch(Exception e){event("start exception "+e.getClass().getSimpleName());}
        if(!accepted){Op retry=current;current=null;if(++busyRetries<=3){queue.addFirst(retry);h.postDelayed(pumpTask,150);}else fail("Bluetooth işlemi tekrar denenecek.");return;}
        busyRetries=0;h.postDelayed(operationTimeout,5000);
    }
    private final Runnable pumpTask=()->pump();
    private final Runnable operationTimeout=()->fail("Ampul yanıtı bekleniyor; bağlantı yenilenecek.");
    private void renewRegistration(){
        recoveries++;clearOperations();initializing=true;
        if(registrationRetries++>=8){fail("TV Bluetooth bağlantısı reddedildi; yeniden denenecek.");return;}
        // Android 14 can retain restricted handle IDs on reused client registrations.
        // A fresh normal GATT client is used; no privileged access is requested.
        if(gatt!=null){retiring.add(gatt);gatt=null;}status="Bluetooth bağlantı oturumu otomatik yenileniyor…";h.postDelayed(()->connectGatt(),300);
    }
    private void complete(BluetoothGatt connection,UUID id,byte[] value,int code){
        if(connection!=gatt||current==null||!current.id.equals(id))return;h.removeCallbacks(operationTimeout);Op op=current;current=null;
        if(code!=0){event("reply "+code+" "+id);fail("Ampul bağlantısı tekrar denenecek ("+code+").");return;}
        if(op.value==null){
            confirmed.put(id,value.clone());if(id.equals(POWER)&&value.length==1)lightOn=value[0]!=0;if(id.equals(BRIGHTNESS)&&value.length==1)lastBrightness=value[0]&255;
            if(id.equals(MODEL)){model=new String(value,java.nio.charset.StandardCharsets.UTF_8).trim();if(!"LCA012".equals(model)){paused=true;fail("Beklenen LCA012 yerine "+model+" bulundu.");return;}}
            else if(id.equals(POWER)&&previousPower==null)previousPower=value;
            else if(id.equals(BRIGHTNESS)&&previousBrightness==null)previousBrightness=value;
            else if(id.equals(TEMPERATURE)&&previousTemperature==null)previousTemperature=value;
            else if(id.equals(COLOR)){
                if(previousColor==null)previousColor=value;saveBaseline();snapshot="power="+hex(previousPower)+" brightness="+hex(previousBrightness)+" color="+hex(previousColor)+" temperature="+hex(previousTemperature);
                initializing=false;ready=true;registrationRetries=0;closeRetiring();status=automatic?"Hue hazır; TV renkleri bekleniyor.":"LCA012 bağlı.";
            }
        }else{
            if(id.equals(POWER)&&op.value.length==1)lightOn=op.value[0]!=0;confirmed.put(id,op.value.clone());if(id.equals(COLOR))confirmed.remove(TEMPERATURE);if(id.equals(TEMPERATURE))confirmed.remove(COLOR);
            if(id.equals(BRIGHTNESS)&&op.value.length==1)lastBrightness=op.value[0]&255;
            writes++;lastWrite=SystemClock.elapsedRealtime();
            if(queue.isEmpty()&&finishMessage!=null){status=finishMessage;finishMessage=null;}
        }
        h.postDelayed(pumpTask,35);
    }
    private void fail(String message){failures++;closeConnection();nextRetry=SystemClock.elapsedRealtime()+10000;status=message;}
    private void clearOperations(){ready=false;queue.clear();current=null;chars.clear();confirmed.clear();busyRetries=0;finishMessage=null;h.removeCallbacks(operationTimeout);h.removeCallbacks(pumpTask);}
    private void closeRetiring(){for(BluetoothGatt old:retiring)try{old.disconnect();old.close();}catch(Exception ignored){}retiring.clear();}
    private void closeConnection(){cancelTest();clearOperations();syncing=false;initializing=false;BluetoothGatt old=gatt;gatt=null;if(old!=null)try{old.disconnect();old.close();}catch(Exception ignored){}closeRetiring();}
    public void disconnect(){h.post(()->{automatic=false;prefs.edit().putBoolean("automatic",false).apply();paused=true;stopScan();closeConnection();status="Bağlantı kesildi. Canlı izleme’den yeniden başlatabilirsiniz.";});}
    public void setAutomatic(boolean enabled){h.post(()->{
        automatic=enabled;prefs.edit().putBoolean("automatic",enabled).apply();paused=!enabled;nextRetry=0;
        if(!enabled){syncing=false;cancelTest();if(ready)restoreInternal();}else if(selected==null)loadSavedDevice();
        status=enabled?"TV açıkken otomatik aktarım etkin.":"Otomatik aktarım kapalı.";
    });}
    public void startSync(){h.post(()->{automatic=true;paused=false;prefs.edit().putBoolean("automatic",true).apply();context.getSharedPreferences("settings",0).edit().putBoolean("enabled",true).apply();cancelTest();nextRetry=0;status="Otomatik aktarım etkin; ampul bağlantısı bekleniyor.";});}
    public void restore(){h.post(()->{paused=true;syncing=false;cancelTest();restoreInternal();});}
    private void cancelTest(){testing=false;h.removeCallbacks(testGreen);h.removeCallbacks(testBlue);h.removeCallbacks(testRestore);}
    public void test(){h.post(()->{
        if(!ready||current!=null||!queue.isEmpty()){status="Ampul hazır olunca tekrar deneyin.";return;}
        paused=true;syncing=false;cancelTest();testing=true;status="Renk testi: kırmızı → yeşil → mavi → önceki ışık";
        sendRgb(new int[]{255,0,0},100);h.postDelayed(testGreen,2000);h.postDelayed(testBlue,4000);h.postDelayed(testRestore,6000);
    });}
    private final Runnable testGreen=()->{if(ready&&testing)sendRgb(new int[]{0,255,0},100);};
    private final Runnable testBlue=()->{if(ready&&testing)sendRgb(new int[]{0,0,255},100);};
    private final Runnable testRestore=()->{testing=false;if(ready)restoreInternal();};
    private void restoreInternal(){
        if(!ready){status="Aktarım durdu; ampul bağlantısı yok.";return;}queue.clear();finishMessage="Aktarım durdu; önceki ışık geri yüklendi.";
        if(validXY(previousColor))enqueue(COLOR,previousColor);else if(validTemperature(previousTemperature)&&chars.containsKey(TEMPERATURE))enqueue(TEMPERATURE,previousTemperature);
        if(previousBrightness!=null&&previousBrightness.length==1)enqueue(BRIGHTNESS,previousBrightness);if(previousPower!=null&&previousPower.length==1)enqueue(POWER,previousPower);
        if(current==null&&queue.isEmpty()){status=finishMessage;finishMessage=null;}
    }
    public void warmWhite(){h.post(()->{
        if(!ready||!chars.containsKey(TEMPERATURE)){status="Sıcak beyaz için ampul bağlantısı bekleniyor.";return;}
        paused=true;syncing=false;cancelTest();queue.clear();previousTemperature=new byte[]{0x72,1};previousColor=new byte[]{-1,-1,-1,-1};previousPower=new byte[]{1};saveBaseline(true);
        finishMessage="Sıcak beyaz (2700 K) aktif.";enqueue(POWER,previousPower);enqueue(TEMPERATURE,previousTemperature);
    });}
    private final Runnable pulse=new Runnable(){public void run(){
        if(closed)return;
        boolean wanted=automatic&&!paused&&screenOn&&!testing;
        if(wanted&&allowed()&&adapter!=null&&adapter.isEnabled()){
            if(selected==null)loadSavedDevice();
            if(selected!=null&&!scanning){
                if(gatt==null&&SystemClock.elapsedRealtime()>=nextRetry){registrationRetries=0;connectGatt();}
                else if(!ready&&!initializing&&gatt!=null&&SystemClock.elapsedRealtime()>=nextRetry){nextRetry=SystemClock.elapsedRealtime()+10000;try{gatt.connect();}catch(Exception e){fail("Ampul bağlantısı tekrar denenecek.");}}
            }
            if(ready&&ReaderService.last>0&&SystemClock.elapsedRealtime()-ReaderService.last<2000){
                syncing=true;
                if(current==null&&queue.isEmpty())try{sendRgb(sampleFrame(ReaderService.frame,source),brightnessPercent);status=BulbPosition.label(source)+" Ambilight aktarılıyor · parlaklık "+brightnessLabel();}catch(Exception ignored){}
            }else if(ready){syncing=false;status="Yeni TV renkleri bekleniyor.";}
        }
        h.postDelayed(this,200);
    }};
    private void sendRgb(int[] rgb,int multiplier){
        if(!ready)return;int brightness=HueBleColor.scaledBrightness(rgb[0],rgb[1],rgb[2],multiplier);
        if(brightness==0){enqueue(POWER,new byte[]{0});return;}
        enqueue(POWER,new byte[]{1});enqueue(BRIGHTNESS,new byte[]{(byte)brightness});enqueue(COLOR,HueBleColor.xy(rgb[0],rgb[1],rgb[2]));
    }
    private static boolean validXY(byte[] p){if(p==null||p.length!=4)return false;int x=(p[0]&255)|((p[1]&255)<<8),y=(p[2]&255)|((p[3]&255)<<8);return x>0&&y>0&&x+y<=65535;}
    private static boolean validTemperature(byte[] p){if(p==null||p.length!=2)return false;int value=(p[0]&255)|((p[1]&255)<<8);return value>=50&&value<=1000;}
    private static String hex(byte[] value){if(value==null)return "";StringBuilder out=new StringBuilder();for(byte b:value)out.append(String.format(Locale.ROOT,"%02x",b&255));return out.toString();}
    private static byte[] unhex(String value){if(value==null||value.isEmpty()||value.length()>8||value.length()%2!=0)return null;byte[] out=new byte[value.length()/2];for(int i=0;i<out.length;i++)out[i]=(byte)Integer.parseInt(value.substring(i*2,i*2+2),16);return out;}
    private void loadBaseline(){try{JSONObject baseline=new JSONObject(prefs.getString("baseline","{}"));previousPower=unhex(baseline.optString("power"));previousBrightness=unhex(baseline.optString("brightness"));previousColor=unhex(baseline.optString("color"));previousTemperature=unhex(baseline.optString("temperature"));}catch(Exception ignored){}}
    private void saveBaseline(){saveBaseline(false);}
    private void saveBaseline(boolean replace){if(!replace&&prefs.contains("baseline"))return;try{String value=new JSONObject().put("power",hex(previousPower)).put("brightness",hex(previousBrightness)).put("color",hex(previousColor)).put("temperature",hex(previousTemperature)).toString();prefs.edit().putString("baseline",value).apply();}catch(Exception ignored){}}
    public void close(){closed=true;h.removeCallbacksAndMessages(null);stopScan();closeConnection();try{context.unregisterReceiver(events);}catch(Exception ignored){}}
}
