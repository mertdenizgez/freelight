package local.ambilight.reader;
import android.app.*;import android.content.*;import android.content.pm.ServiceInfo;import android.os.*;import org.json.*;import java.io.*;
public class ReaderService extends Service {
 public static volatile JSONObject frame;public static volatile String status="Stopped",connectionState="stopped",endpoint="";public static volatile long count=0,errors=0,latency=0,last=0;public static volatile boolean running=false;
 public static volatile HueFleet fleet;private static volatile ReaderService owner;
 private Thread worker;private volatile boolean stop;private volatile LocalTvApi api;
 public static boolean canStart(Context c){return Build.VERSION.SDK_INT<31||(c.checkSelfPermission("android.permission.BLUETOOTH_CONNECT")==0&&c.checkSelfPermission("android.permission.BLUETOOTH_SCAN")==0);}
 public void onCreate(){super.onCreate();owner=this;frame=null;last=0;fleet=new HueFleet(this);}
 public int onStartCommand(Intent intent,int flags,int id){
  if(intent!=null&&"STOP".equals(intent.getAction())){getSharedPreferences("settings",0).edit().putBoolean("enabled",false).apply();stopSelf();return START_NOT_STICKY;}
  if(!canStart(this)){connectionState="permission";stopSelf();return START_NOT_STICKY;}
  getSharedPreferences("settings",0).edit().putBoolean("enabled",true).apply();
  NotificationManager nm=getSystemService(NotificationManager.class);nm.createNotificationChannel(new NotificationChannel("reader","Freelight",NotificationManager.IMPORTANCE_LOW));
  PendingIntent open=PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),PendingIntent.FLAG_IMMUTABLE);
  PendingIntent end=PendingIntent.getService(this,1,new Intent(this,ReaderService.class).setAction("STOP"),PendingIntent.FLAG_IMMUTABLE);
  Context localized=I18n.wrap(this);Notification notification=new Notification.Builder(this,"reader").setSmallIcon(android.R.drawable.ic_menu_view).setContentTitle(I18n.text(localized,"notification_title")).setContentText(I18n.text(localized,"notification_text")).setContentIntent(open).setOngoing(true).addAction(new Notification.Action.Builder(null,I18n.text(localized,"stop"),end).build()).build();
  if(Build.VERSION.SDK_INT>=29)startForeground(1,notification,ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE);else startForeground(1,notification);
  if(worker==null){stop=false;worker=new Thread(this::loop,"FreelightReader");worker.start();}return START_STICKY;
 }
 protected void dump(FileDescriptor fd,PrintWriter writer,String[] args){try{JSONArray bulbs=new JSONArray();HueBle ble=null;if(fleet!=null)for(HueBle b:fleet.bulbs()){if(ble==null)ble=b;bulbs.put(new JSONObject().put("id",b.id()).put("name",b.savedName()).put("source",b.source).put("status",b.status).put("ready",b.ready).put("syncing",b.syncing).put("automatic",b.automatic).put("paused",b.isPaused()).put("writes",b.writes).put("failures",b.failures).put("light_on",b.isLightOn()).put("brightness_percent",b.brightnessPercent).put("brightness",b.lastBrightness));}JSONObject snapshot=new JSONObject().put("pairing_state",TvPairing.diagnostic).put("running",running).put("status",status==null?"":status).put("samples",count).put("errors",errors).put("latency_ms",latency).put("endpoint",endpoint).put("last_elapsed_ms",last).put("age_ms",last==0?-1:SystemClock.elapsedRealtime()-last).put("ble_status",ble==null?"":ble.status).put("ble_ready",ble!=null&&ble.ready).put("ble_sync",ble!=null&&ble.syncing).put("ble_writes",ble==null?0:ble.writes).put("ble_failures",ble==null?0:ble.failures).put("ble_model",ble==null?"":ble.model).put("ble_automatic",ble!=null&&ble.automatic).put("ble_screen_on",ble!=null&&ble.screenOn).put("ble_source",ble==null?"":ble.source).put("ble_multiplier",ble==null?0:ble.brightnessPercent/100.0).put("ble_brightness",ble==null?0:ble.lastBrightness).put("ble_recoveries",ble==null?0:ble.recoveries).put("ble_devices",ble==null?0:ble.devices().size()).put("ble_trace",ble==null?"":ble.trace).put("ble_snapshot",ble==null?"":ble.snapshot).put("ble_operation",ble==null?"":ble.operation).put("bulbs",bulbs).put("frame",frame==null?JSONObject.NULL:frame);writer.println(snapshot.toString());}catch(Exception e){writer.println("State unavailable");}}

 private void loop(){
  running=true;int consecutive=0;TvCredentials active=null;
  while(!stop){long started=SystemClock.elapsedRealtime();try{
   TvCredentials credentials=TvCredentials.load(this);
   if(credentials==null){frame=null;last=0;connectionState="setup";status="TV setup required";Thread.sleep(1000);continue;}
   if(credentials!=active||api==null){if(api!=null)api.close();api=new LocalTvApi(credentials.fingerprint);active=credentials;}
   JSONObject data=TvFrame.validate(api.request("GET","/6/ambilight/measured",null,credentials));
   if(stop)break;
   synchronized(TvCredentials.class){if(TvCredentials.load(this)!=credentials)continue;
   if(credentials.fingerprint.isEmpty()){TvCredentials.save(this,new TvCredentials(credentials.user,credentials.key,api.certificate()));}
   endpoint=api.host();frame=data;count++;last=SystemClock.elapsedRealtime();latency=last-started;connectionState="connected";status="Connected";consecutive=0;}Thread.sleep(Math.max(1,200-latency));
  }catch(InterruptedException e){break;}catch(Exception e){
   if(stop)break;errors++;consecutive++;connectionState=e instanceof LocalTvApi.Failure&&((LocalTvApi.Failure)e).code==401?"auth":"unavailable";status="Local TV connection unavailable";
   try{Thread.sleep(Math.min(15000,500L*(1L<<Math.min(consecutive,5))));}catch(InterruptedException end){break;}
  }}if(owner==this)running=false;
 }
 public void onDestroy(){stop=true;if(api!=null)api.close();if(worker!=null)worker.interrupt();if(fleet!=null){fleet.close();fleet=null;}frame=null;last=0;running=false;status="Stopped";connectionState="stopped";super.onDestroy();}
 public IBinder onBind(Intent intent){return null;}
}
