package local.ambilight.reader;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.nsd.*;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.util.*;
import java.util.concurrent.*;

public class MainActivity extends Activity {
    private final Handler handler = new Handler();
    private final ExecutorService jobs = Executors.newSingleThreadExecutor();
    private HueClient hue;
    private TextView tvStatus;private TvPairing tvPairing;private AlertDialog pinDialog;private Button pairButton;private boolean tvBusy;private int tvGeneration;private String tvMessage;
    private LinearLayout root, content;
    private TextView hueStatus, readiness, areaHint, pageTitle;
    private FrameLayout shell,drawerOverlay; private LinearLayout drawer; private Button menuButton,homeNavigation; private View beforeMenu; private boolean drawerOpen=false; private Bitmap roomBitmap,bulbBitmap;
    private LinearLayout bulbList; private boolean livePage; private HueFleet displayedFleet;
    private final LinkedHashMap<String,BulbRow> bulbRows=new LinkedHashMap<>();
    private EditText address;
    private Spinner areas;
    private JSONArray areaData = new JSONArray();
    private RoomScene preview;
    private TextView bleStatus; private Spinner bleDevices; private int bleDeviceCount=-1; private ArrayList<HueBle.Found> bleData=new ArrayList<>(); private Runnable permissionAction;
    private boolean busy = false, destroyed = false;
    private String hueMessage;
    private NsdManager nsd;
    private NsdManager.DiscoveryListener discovery;
    private final ArrayList<Button> networkButtons = new ArrayList<>();
    private final Runnable tick = new Runnable() { public void run() { render(); handler.postDelayed(this, 400); } };
    private final Runnable discoveryTimeout = () -> { stopDiscovery(); setHueStatus(s("bridge_not_found")); };
    @Override protected void attachBaseContext(Context base){super.attachBaseContext(I18n.wrap(base));}
    private String s(String key,Object... args){return I18n.text(this,key,args);}
    private String positionLabel(String source){return I18n.position(this,source);}
    private int dp(float value) { return Math.round(value * getResources().getDisplayMetrics().density); }
    private TextView text(String value, int size, int color) { TextView view = new TextView(this); view.setText(value); view.setTextSize(size); view.setTextColor(color); return view; }
    private GradientDrawable shape(int color, int border) { GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(14)); if (border != 0) d.setStroke(dp(2), border); return d; }
    private Button button(String label, Runnable action) {
        Button b = new Button(this); b.setText(label); b.setTextSize(14);b.setMinWidth(0);b.setMinimumWidth(0);b.setMaxLines(2);b.setAutoSizeTextTypeUniformWithConfiguration(11,14,1,android.util.TypedValue.COMPLEX_UNIT_SP);b.setStateListAnimator(null);b.setElevation(0); b.setAllCaps(false); b.setTextColor(Palette.INK); b.setPadding(dp(18), dp(8), dp(18), dp(8));
        android.graphics.drawable.StateListDrawable states = new android.graphics.drawable.StateListDrawable();
        states.addState(new int[]{android.R.attr.state_focused}, shape(Palette.FOCUS, Palette.FOCUS_BORDER));
        states.addState(new int[]{android.R.attr.state_pressed}, shape(Palette.FOCUS, Palette.FOCUS_BORDER));
        states.addState(new int[]{}, shape(Palette.BUTTON, 0)); b.setBackground(states);
        b.setOnClickListener(v -> action.run()); return b;
    }
    private LinearLayout card() { LinearLayout c = new LinearLayout(this); c.setOrientation(1); c.setPadding(dp(24), dp(20), dp(24), dp(20)); c.setBackground(shape(Palette.SURFACE, 0)); return c; }
    private void addButton(LinearLayout row, Button button) { LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(52), 1); p.setMargins(0, dp(12), dp(12), dp(12)); row.addView(button, p); }
    public void onCreate(Bundle state) {
        super.onCreate(state); hue = new HueClient(this); nsd = (NsdManager) getSystemService(NSD_SERVICE);
        shell=new FrameLayout(this);
        root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(28),dp(12),dp(28),dp(12));root.setBackgroundColor(Palette.BACKGROUND);shell.addView(root,new FrameLayout.LayoutParams(-1,-1));
        LinearLayout header=new LinearLayout(this);header.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);header.setGravity(Gravity.CENTER_VERTICAL);
        menuButton=button("☰",this::openMenu);menuButton.setContentDescription(s("menu_open"));LinearLayout.LayoutParams menuLayout=new LinearLayout.LayoutParams(dp(52),dp(48));menuLayout.rightMargin=dp(16);header.addView(menuButton,menuLayout);
        ImageView logo=new ImageView(this);logo.setImageResource(getResources().getIdentifier("freelight_icon","drawable",getPackageName()));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(32),dp(32));lp.rightMargin=dp(8);header.addView(logo,lp);
        header.addView(text("Freelight",23,Palette.INK),new LinearLayout.LayoutParams(0,dp(40),1));pageTitle=text(s("home"),14,Palette.SECONDARY);header.addView(pageTitle);root.addView(header);
        content=new LinearLayout(this);content.setOrientation(1);LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,0,1);cp.topMargin=dp(10);root.addView(content,cp);
        buildDrawer();setContentView(shell);if(getIntent().getBooleanExtra("show_app_settings",false)){getIntent().removeExtra("show_app_settings");showSettings();}else if(hasTvPair())showLive();else showTvSetup();
        if (ReaderService.canStart(this)&&getSharedPreferences("settings", 0).getBoolean("enabled", false)) startForegroundService(new Intent(this, ReaderService.class));
    }
    private void buildDrawer(){
        drawerOverlay=new FrameLayout(this);drawerOverlay.setVisibility(View.GONE);shell.addView(drawerOverlay,new FrameLayout.LayoutParams(-1,-1));
        View scrim=new View(this);scrim.setBackgroundColor(Color.argb(175,0,0,0));scrim.setOnClickListener(v->closeMenu());drawerOverlay.addView(scrim,new FrameLayout.LayoutParams(-1,-1));
        drawer=new LinearLayout(this);drawer.setOrientation(1);drawer.setPadding(dp(22),dp(28),dp(22),dp(20));drawer.setBackgroundColor(Palette.DRAWER);FrameLayout.LayoutParams panel=new FrameLayout.LayoutParams(dp(300),-1,Gravity.LEFT);drawerOverlay.addView(drawer,panel);
        TextView brand=text("Freelight",28,Palette.INK);drawer.addView(brand);TextView hint=text(s("tagline"),14,Palette.SECONDARY);hint.setPadding(0,dp(5),0,dp(30));drawer.addView(hint);
        homeNavigation=button(s("home"),()->{showLive();closeMenu();});homeNavigation.setFocusableInTouchMode(true);drawer.addView(homeNavigation,new LinearLayout.LayoutParams(-1,dp(54)));
        Button bluetooth=button(s("bluetooth"),()->{showBluetooth();closeMenu();});LinearLayout.LayoutParams nav=new LinearLayout.LayoutParams(-1,dp(54));nav.topMargin=dp(10);drawer.addView(bluetooth,nav);
        Button bridge=button(s("tv_setup"),()->{showTvSetup();closeMenu();});LinearLayout.LayoutParams bridgeLayout=new LinearLayout.LayoutParams(-1,dp(54));bridgeLayout.topMargin=dp(10);drawer.addView(bridge,bridgeLayout);
        Button appSettings=button(s("app_settings"),()->{showSettings();closeMenu();});LinearLayout.LayoutParams settingsLayout=new LinearLayout.LayoutParams(-1,dp(54));settingsLayout.topMargin=dp(10);drawer.addView(appSettings,settingsLayout);
        drawer.addView(new View(this),new LinearLayout.LayoutParams(-1,0,1));drawer.addView(button(s("close_menu"),this::closeMenu),new LinearLayout.LayoutParams(-1,dp(52)));
    }
    private void openMenu(){
        if(drawerOpen)return;drawerOpen=true;beforeMenu=getCurrentFocus();root.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);drawerOverlay.setVisibility(View.VISIBLE);drawer.setTranslationX(-dp(300));drawer.animate().translationX(0).setDuration(180).start();homeNavigation.requestFocus();homeNavigation.post(()->{if(drawerOpen)homeNavigation.requestFocus();});
    }
    private void closeMenu(){
        if(!drawerOpen)return;drawerOpen=false;drawer.animate().cancel();drawerOverlay.setVisibility(View.GONE);root.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);if(beforeMenu!=null&&beforeMenu.isAttachedToWindow()&&beforeMenu.isShown())beforeMenu.requestFocus();else menuButton.requestFocus();
    }
    @Override public void onBackPressed(){if(drawerOpen){closeMenu();return;}if(!livePage){showLive();menuButton.requestFocus();return;}super.onBackPressed();}
    @Override public boolean dispatchKeyEvent(KeyEvent event){if(event.getAction()==KeyEvent.ACTION_DOWN&&event.getRepeatCount()==0&&event.getKeyCode()==KeyEvent.KEYCODE_MENU){if(drawerOpen)closeMenu();else openMenu();return true;}return super.dispatchKeyEvent(event);}
    private void resetBulbPage(){tvStatus=null;pairButton=null;stopDiscovery();bulbList=null;displayedFleet=null;bulbRows.clear();bleStatus=null;bleDevices=null;preview=null;readiness=null;content.removeAllViews();livePage=false;}
    private Bitmap bulbPhoto(){if(bulbBitmap==null){BitmapFactory.Options options=new BitmapFactory.Options();options.inSampleSize=4;bulbBitmap=BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("freelight_bulb_photo","drawable",getPackageName()),options);}return bulbBitmap;}
    private void showLive(){
        resetBulbPage();livePage=true;pageTitle.setText(s("home"));
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);LinearLayout body=new LinearLayout(this);body.setOrientation(1);scroll.addView(body);content.addView(scroll,new LinearLayout.LayoutParams(-1,-1));
        if(roomBitmap==null)roomBitmap=BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("freelight_room","drawable",getPackageName()));
        preview=new RoomScene(this,roomBitmap,bulbPhoto(),new RoomScene.Listener(){public void select(String id){withBluetooth(()->choosePosition(null,ReaderService.fleet.get(id)));}public void addBulb(){showBluetooth();}});body.addView(preview,new LinearLayout.LayoutParams(-1,dp(400)));
        LinearLayout toolbar=new LinearLayout(this);toolbar.setGravity(Gravity.CENTER_VERTICAL);toolbar.setPadding(0,dp(18),0,dp(8));readiness=text("Ampuller",19,Palette.INK);toolbar.addView(readiness,new LinearLayout.LayoutParams(0,dp(40),1));
        Button start=button(s("start_all"),()->withBluetooth(()->ReaderService.fleet.startAll()));LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(dp(160),dp(52));sp.rightMargin=dp(8);toolbar.addView(start,sp);
        toolbar.addView(button(s("stop_all"),()->{if(ReaderService.fleet!=null)ReaderService.fleet.stopAll();}),new LinearLayout.LayoutParams(dp(160),dp(52)));body.addView(toolbar);
        bulbList=new LinearLayout(this);bulbList.setOrientation(1);body.addView(bulbList,new LinearLayout.LayoutParams(-1,-2));render();
    }
    private void showSettings(){
        resetBulbPage();pageTitle.setText(s("app_settings"));ScrollView scroll=new ScrollView(this);LinearLayout body=card();scroll.addView(body);content.addView(scroll,new LinearLayout.LayoutParams(-1,-1));
        body.addView(text(s("app_settings"),24,Palette.INK));TextView label=text(s("language"),17,Palette.INK);label.setPadding(0,dp(24),0,dp(8));body.addView(label);
        Spinner languages=new Spinner(this);String[] names=new String[LanguagePolicy.NAMES.length+1];names[0]=s("system_default");System.arraycopy(LanguagePolicy.NAMES,0,names,1,LanguagePolicy.NAMES.length);languages.setAdapter(spinnerAdapter(names));
        String current=getSharedPreferences("ui",0).getString("language","system");int selection=0;for(int i=0;i<LanguagePolicy.CODES.length;i++)if(LanguagePolicy.CODES[i].equals(current))selection=i+1;languages.setSelection(selection);body.addView(languages,new LinearLayout.LayoutParams(-1,dp(56)));
        TextView hint=text(s("language_hint"),15,Palette.SECONDARY);hint.setPadding(0,dp(16),0,dp(24));body.addView(hint);
        Button apply=button(s("apply"),()->{int choice=languages.getSelectedItemPosition();String code=choice==0?"system":LanguagePolicy.CODES[choice-1];getSharedPreferences("ui",0).edit().putString("language",code).apply();getIntent().putExtra("show_app_settings",true);recreate();});body.addView(apply,new LinearLayout.LayoutParams(-1,dp(54)));
        LinearLayout extras=new LinearLayout(this);addButton(extras,button(s("privacy"),this::showPrivacy));addButton(extras,button(s("about"),this::showAbout));body.addView(extras);

    }

    private boolean hasTvPair(){try{return TvCredentials.load(this)!=null;}catch(Exception e){return false;}}
    private void showTvSetup(){
        resetBulbPage();pageTitle.setText(s("tv_setup"));ScrollView scroll=new ScrollView(this);LinearLayout body=card();scroll.addView(body);content.addView(scroll,new LinearLayout.LayoutParams(-1,-1));
        body.addView(text(s("tv_setup"),24,Palette.INK));TextView intro=text(s("tv_intro"),16,Palette.SECONDARY);intro.setPadding(0,dp(14),0,dp(16));body.addView(intro);
        tvStatus=text("",17,Palette.ACCENT);body.addView(tvStatus);pairButton=button(hasTvPair()?s("tv_repair"):s("tv_pair"),this::beginTvPairing);LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(-1,dp(54));pp.topMargin=dp(18);body.addView(pairButton,pp);pairButton.setEnabled(!tvBusy);
        TextView help=text(s("tv_begin_help"),14,Palette.SECONDARY);help.setPadding(0,dp(16),0,dp(8));body.addView(help);
        if(hasTvPair()){
            LinearLayout actions=new LinearLayout(this);addButton(actions,button(s("setup_connect_bulb"),this::showBluetooth));addButton(actions,button(s("reader_start"),()->withBluetooth(()->{})));addButton(actions,button(s("reader_stop"),()->startService(new Intent(this,ReaderService.class).setAction("STOP"))));body.addView(actions);
            Button forget=button(s("tv_forget"),()->new AlertDialog.Builder(this).setTitle(s("tv_forget_title")).setMessage(s("tv_forget_help")).setNegativeButton(s("cancel"),null).setPositiveButton(s("tv_forget"),(d,w)->{cancelTvPairing();stopService(new Intent(this,ReaderService.class));getSharedPreferences("settings",0).edit().putBoolean("enabled",false).apply();TvCredentials.forget(this);tvMessage=null;showTvSetup();}).show());body.addView(forget,new LinearLayout.LayoutParams(-1,dp(52)));
        }
        renderTvStatus();
    }
    private void renderTvStatus(){if(tvStatus==null)return;String message=tvMessage;if(message==null){
        if(!hasTvPair())message=s("tv_status_missing");
        else if("connected".equals(ReaderService.connectionState)&&android.os.SystemClock.elapsedRealtime()-ReaderService.last<2000)message=s("tv_status_fresh");
        else if("auth".equals(ReaderService.connectionState))message=s("tv_status_auth");
        else message=s("tv_status_wait");
    }if(!message.contentEquals(tvStatus.getText()))tvStatus.setText(message);}
    private void cancelTvPairing(){tvGeneration++;tvBusy=false;if(tvPairing!=null){tvPairing.close();tvPairing=null;}if(pinDialog!=null){pinDialog.dismiss();pinDialog=null;}}
    private void beginTvPairing(){
        if(tvBusy)return;cancelTvPairing();final int generation=tvGeneration;final TvPairing session=new TvPairing();tvPairing=session;tvBusy=true;tvMessage=s("tv_pairing");if(pairButton!=null)pairButton.setEnabled(false);renderTvStatus();
        jobs.execute(()->{try{session.begin();handler.post(()->{if(destroyed||generation!=tvGeneration){session.close();return;}showTvPin(session,generation);});}
        catch(Exception e){session.close();handler.post(()->{if(destroyed||generation!=tvGeneration)return;tvBusy=false;tvMessage=s("tv_failed");if(pairButton!=null)pairButton.setEnabled(true);renderTvStatus();});}});
    }
    private void showTvPin(TvPairing session,int generation){
        LinearLayout box=new LinearLayout(this);box.setOrientation(1);box.setPadding(dp(24),dp(8),dp(24),dp(12));box.addView(text(s("tv_pin_help"),16,Palette.INK));EditText pin=new EditText(this);pin.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);pin.setSingleLine(true);pin.setHint(s("tv_pin_hint"));pin.setTextColor(Palette.INK);pin.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(4)});box.addView(pin,new LinearLayout.LayoutParams(-1,dp(54)));TextView error=text("",14,Palette.ACCENT);box.addView(error);
        pinDialog=new AlertDialog.Builder(this).setTitle(s("tv_setup")).setView(box).setNegativeButton(s("cancel"),(d,w)->{cancelTvPairing();tvMessage=null;showTvSetup();}).setPositiveButton(s("tv_confirm"),null).create();pinDialog.setCanceledOnTouchOutside(false);
        pinDialog.setOnCancelListener(d->{cancelTvPairing();tvMessage=null;showTvSetup();});pinDialog.setOnShowListener(d->{pin.requestFocus();Button confirm=pinDialog.getButton(AlertDialog.BUTTON_POSITIVE);confirm.setOnClickListener(v->{
            String value=pin.getText().toString();if(!value.matches("[0-9]{4}")){error.setText(s("tv_pin_invalid"));return;}
            if(session.remainingMillis()==0){error.setText(s("tv_expired"));confirm.setEnabled(false);return;}
            confirm.setEnabled(false);pin.setEnabled(false);error.setText(s("tv_verifying"));
            jobs.execute(()->{try{session.finish(getApplicationContext(),value);handler.post(()->{if(destroyed||generation!=tvGeneration)return;tvBusy=false;tvPairing=null;if(pinDialog!=null){pinDialog.dismiss();pinDialog=null;}tvMessage=s("tv_success");showTvSetup();handler.postDelayed(()->{if(!destroyed){tvMessage=null;renderTvStatus();}},5000);});}
            catch(Exception e){handler.post(()->{if(destroyed||generation!=tvGeneration)return;error.setText(session.remainingMillis()==0?s("tv_expired"):s("tv_failed"));pin.setEnabled(true);confirm.setEnabled(session.remainingMillis()>0);});}});
        });});pinDialog.show();
    }
    private void showPrivacy(){
        resetBulbPage();pageTitle.setText(s("privacy"));ScrollView scroll=new ScrollView(this);LinearLayout body=card();scroll.addView(body);content.addView(scroll,new LinearLayout.LayoutParams(-1,-1));body.addView(text(s("privacy"),24,Palette.INK));TextView policy=text(s("privacy_body"),16,Palette.INK);policy.setPadding(0,dp(20),0,dp(20));body.addView(policy);try(java.io.InputStream input=getAssets().open("privacy-policy.txt")){java.io.ByteArrayOutputStream bytes=new java.io.ByteArrayOutputStream();byte[] buffer=new byte[1024];int n;while((n=input.read(buffer))!=-1){bytes.write(buffer,0,n);if(bytes.size()>32768)throw new java.io.IOException();}String full=new String(bytes.toByteArray(),java.nio.charset.StandardCharsets.UTF_8).replaceAll("(?m)^#{1,6} +","");body.addView(text(full,14,Palette.SECONDARY));}catch(Exception ignored){}body.addView(text(Privacy.CONTACT,14,Palette.SECONDARY));
    }
    private void showAbout(){new AlertDialog.Builder(this).setTitle("Freelight 0.12").setMessage(Privacy.LICENSE+"\n\n"+s("tv_intro")+"\n\n"+Privacy.CONTACT).setPositiveButton(s("close_menu"),null).show();}

    private void showHue() {
        resetBulbPage();pageTitle.setText("Hue Bridge"); networkButtons.clear();
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true); LinearLayout body = card(); scroll.addView(body);
        body.addView(text(s("bridge_title"), 23, Palette.INK));
        hueStatus = text(hueMessage == null ? (hue.paired() ? s("bridge_paired") : s("bridge_wait")) : hueMessage, 14, Palette.SECONDARY); body.addView(hueStatus);
        TextView label = text(s("bridge_ip"), 14, Palette.INK); label.setPadding(0, dp(14), 0, dp(4)); body.addView(label);
        address = new EditText(this); address.setSingleLine(true); address.setTextSize(18); address.setTextColor(Palette.INK); address.setHintTextColor(Palette.SECONDARY); address.setHint(s("ip_hint")); address.setText(hue.host()); address.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI); address.setPadding(dp(14), dp(8), dp(14), dp(8)); address.setBackground(shape(Palette.INPUT, Palette.BORDER)); body.addView(address, new LinearLayout.LayoutParams(-1, dp(48)));
        LinearLayout actions = new LinearLayout(this);
        Button find = button(s("find"), this::discover); Button pair = button(s("pair"), () -> runNetwork(true)); Button test = button(s("test"), () -> runNetwork(false));
        networkButtons.add(find); networkButtons.add(pair); networkButtons.add(test); addButton(actions, find); addButton(actions, pair); addButton(actions, test); body.addView(actions);
        body.addView(text(s("bridge_pair_help"), 13, Palette.SECONDARY));
        TextView areaLabel = text(s("entertainment_area"), 14, Palette.INK); areaLabel.setPadding(0, dp(14), 0, 0); body.addView(areaLabel);
        areas = new Spinner(this); body.addView(areas, new LinearLayout.LayoutParams(-1, dp(45))); areaHint = text("", 12, Palette.SECONDARY); body.addView(areaHint); fillAreas(hue.cachedAreas());
        body.addView(text(s("position_title"), 14, Palette.INK)); Spinner source = new Spinner(this); String[] labels = {positionLabel("left"),positionLabel("top"),positionLabel("right")}; String[] values = {"left", "top", "right"}; source.setAdapter(spinnerAdapter(labels)); int chosen = Arrays.asList(values).indexOf(hue.source()); source.setSelection(Math.max(0, chosen)); source.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { public void onNothingSelected(AdapterView<?> p) {} public void onItemSelected(AdapterView<?> p, View v, int position, long id) { hue.source(values[position]); } }); body.addView(source, new LinearLayout.LayoutParams(-1, dp(45)));
        Button forget = button(s("forget"), () -> { if (busy) return; hue.forget(); fillAreas(new JSONArray()); setHueStatus(s("forgotten")); }); body.addView(forget, new LinearLayout.LayoutParams(-1, dp(42)));
        TextView note = text(s("bridge_note"), 12, Palette.SECONDARY); note.setPadding(0, dp(12), 0, 0); body.addView(note);
        content.addView(scroll, new LinearLayout.LayoutParams(-1, -1)); setBusy(busy);
    }
    private void withBluetooth(Runnable action) {
        if(!hasTvPair()){showTvSetup();return;}
        String[] permissions=Build.VERSION.SDK_INT>=31?new String[]{"android.permission.BLUETOOTH_SCAN","android.permission.BLUETOOTH_CONNECT"}:new String[]{"android.permission.ACCESS_FINE_LOCATION"};
        for(String p:permissions)if(checkSelfPermission(p)!=0){permissionAction=action;requestPermissions(permissions,42);return;}
        if(ReaderService.fleet==null){startForegroundService(new Intent(this,ReaderService.class));handler.postDelayed(()->{if(!destroyed&&ReaderService.fleet!=null)action.run();},600);}else action.run();
    }
    public void onRequestPermissionsResult(int request,String[] names,int[] results){super.onRequestPermissionsResult(request,names,results);if(request==42){boolean granted=results.length>0;for(int r:results)granted &= r==0;Runnable action=permissionAction;permissionAction=null;if(granted&&action!=null)withBluetooth(action);else if(bleStatus!=null)bleStatus.setText(s("permission"));}}
    private void showBluetooth(){
        resetBulbPage();pageTitle.setText(s("bluetooth"));bleDeviceCount=-1;
        ScrollView scroll=new ScrollView(this);LinearLayout body=card();scroll.addView(body);
        body.addView(text(s("scan_title"),23,Palette.INK));
        body.addView(text(s("scan_steps"),14,Palette.ACCENT));
        body.addView(text(s("scan_help"),14,Palette.SECONDARY));
        bleStatus=text(s("scan_hint"),14,Palette.ACCENT);body.addView(bleStatus);
        bleDevices=new Spinner(this);body.addView(bleDevices,new LinearLayout.LayoutParams(-1,dp(54)));
        LinearLayout pairing=new LinearLayout(this);
        addButton(pairing,button(s("scan"),()->withBluetooth(()->ReaderService.fleet.discovery.scan())));
        addButton(pairing,button(s("scan_select"),()->withBluetooth(()->{int index=bleDevices.getSelectedItemPosition();if(index>=0&&index<bleData.size())choosePosition(bleData.get(index),null);else bleStatus.setText(s("scan_first"));})));
        body.addView(pairing);
        TextView title=text(s("registered_bulbs"),20,Palette.INK);title.setPadding(0,dp(16),0,dp(8));body.addView(title);
        body.addView(text(s("manage_hint"),13,Palette.SECONDARY));
        bulbList=new LinearLayout(this);bulbList.setOrientation(1);body.addView(bulbList);
        content.addView(scroll,new LinearLayout.LayoutParams(-1,-1));renderBle();renderBulbs();
    }
    private void choosePosition(HueBle.Found bulb,HueBle existing){
        HueFleet fleet=ReaderService.fleet;if(fleet==null)return;
        if(bulb==null&&existing==null)return;
        HueBle saved=existing!=null?existing:fleet.get(bulb.device.getAddress());
        LinearLayout body=new LinearLayout(this);body.setOrientation(1);body.setPadding(dp(20),dp(6),dp(20),dp(12));
        body.addView(text(s("bulb_name"),15,Palette.INK));
        EditText name=new EditText(this);name.setSingleLine(true);name.setTextSize(17);name.setTextColor(Palette.INK);name.setHintTextColor(Palette.SECONDARY);name.setHint(s("name_hint"));name.setText(saved==null?"":saved.customName());name.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(40)});body.addView(name,new LinearLayout.LayoutParams(-1,dp(46)));
        body.addView(text(s("position_title"),15,Palette.INK));
        String initial=saved==null?"":saved.source;
        PositionPreview diagram=new PositionPreview(initial);body.addView(diagram,new LinearLayout.LayoutParams(-1,dp(110)));
        Spinner choices=new Spinner(this);String[] options=new String[BulbPosition.LABELS.length+1];options[0]=s("choose_position");for(int i=0;i<BulbPosition.VALUES.length;i++)options[i+1]=positionLabel(BulbPosition.VALUES[i]);choices.setAdapter(spinnerAdapter(options));choices.setSelection(BulbPosition.index(initial)+1);body.addView(choices,new LinearLayout.LayoutParams(-1,dp(52)));
        int initialBrightness=saved==null?200:saved.brightnessPercent;
        TextView brightness=text("",16,Palette.INK);body.addView(brightness);
        SeekBar slider=new SeekBar(this);slider.setMax(16);slider.setProgress(initialBrightness/25);body.addView(slider,new LinearLayout.LayoutParams(-1,dp(52)));
        Runnable updateBrightness=()->brightness.setText(s("brightness",slider.getProgress()/4.0));updateBrightness.run();
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onStartTrackingTouch(SeekBar s){}public void onStopTrackingTouch(SeekBar s){}public void onProgressChanged(SeekBar s,int progress,boolean user){updateBrightness.run();}});
        body.addView(text(s("brightness_hint"),12,Palette.SECONDARY));
        ScrollView scroll=new ScrollView(this);scroll.addView(body);LinearLayout frame=new LinearLayout(this);frame.addView(scroll,new LinearLayout.LayoutParams(-1,dp(310)));
        AlertDialog dialog=new AlertDialog.Builder(this).setTitle(bulb==null?s("bulb_settings"):s("new_bulb")).setView(frame).setNegativeButton(s("cancel"),null).setPositiveButton(bulb==null?s("save"):s("save_connect"),null).create();
        dialog.setOnShowListener(unused->{
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            Button save=dialog.getButton(AlertDialog.BUTTON_POSITIVE);save.setEnabled(BulbPosition.index(initial)>=0);
            choices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){public void onNothingSelected(AdapterView<?> p){}public void onItemSelected(AdapterView<?> p,View v,int index,long id){diagram.source=index==0?"":BulbPosition.VALUES[index-1];diagram.invalidate();save.setEnabled(index>0);}});
            save.setOnClickListener(v->{int selected=choices.getSelectedItemPosition();if(selected<=0)return;String position=BulbPosition.VALUES[selected-1];if(bulb!=null)fleet.add(bulb,position);HueBle target=bulb==null?existing:fleet.get(bulb.device.getAddress());target.configure(name.getText().toString(),position,slider.getProgress()*25);dialog.dismiss();});
            choices.requestFocus();
        });dialog.show();
    }
    private class PositionPreview extends View {
        String source;final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        PositionPreview(String value){super(MainActivity.this);source=value;setContentDescription(s("position_title"));}
        protected void onDraw(Canvas canvas){
            super.onDraw(canvas);float w=getWidth(),h=getHeight();paint.setColor(Palette.DIAGRAM);canvas.drawRoundRect(w*.29f,h*.38f,w*.71f,h*.92f,dp(9),dp(9),paint);
            paint.setTextAlign(Paint.Align.CENTER);paint.setTextSize(dp(16));paint.setColor(Palette.INK);canvas.drawText("TV",w/2,h*.73f,paint);
            float[] xs={w*.10f,w*.25f,w*.50f,w*.75f,w*.90f};float[] ys={h*.62f,h*.18f,h*.10f,h*.18f,h*.62f};String[] labels={positionLabel("left"),positionLabel("upper_left"),positionLabel("top"),positionLabel("upper_right"),positionLabel("right")};
            for(int i=0;i<xs.length;i++){boolean active=BulbPosition.VALUES[i].equals(source);paint.setColor(active?Palette.ACCENT:Palette.MUTED);canvas.drawCircle(xs[i],ys[i],dp(active?8:5),paint);paint.setTextSize(dp(11));canvas.drawText(labels[i],xs[i],ys[i]+dp(20),paint);}
        }
    }
    private void renderBle(){
        if(bleStatus==null||ReaderService.fleet==null)return;HueBle scan=ReaderService.fleet.discovery;
        String scanMessage=scan.scanning?s("scanning"):scan.status.contains("izin")?s("permission"):scan.status.contains("kapalı")?s("bluetooth_off"):scan.status.contains("hata")||scan.status.contains("başlatılamadı")||scan.status.contains("kullanılamıyor")?s("scan_error"):scan.devices().isEmpty()?s("scan_hint"):s("scan_first");if(!scanMessage.contentEquals(bleStatus.getText()))bleStatus.setText(scanMessage);
        ArrayList<HueBle.Found> data=scan.devices();if(data.size()!=bleDeviceCount){bleDeviceCount=data.size();bleData=data;String[] labels=new String[Math.max(1,data.size())];if(data.isEmpty())labels[0]=scan.scanning?s("scanning"):s("scan_empty");else for(int i=0;i<data.size();i++)labels[i]=data.get(i).label()+(ReaderService.fleet.get(data.get(i).device.getAddress())!=null?" · "+s("registered"):"");bleDevices.setAdapter(spinnerAdapter(labels));}
    }
    private class BulbRow {
        final LinearLayout view;final TextView title,status;final Button toggle;
        BulbRow(String id,int number){
            view=new LinearLayout(MainActivity.this);view.setOrientation(1);view.setPadding(dp(20),dp(16),dp(20),dp(16));view.setBackground(shape(Palette.CARD,0));
            LinearLayout nameRow=new LinearLayout(MainActivity.this);nameRow.setGravity(Gravity.CENTER_VERTICAL);ImageView icon=new ImageView(MainActivity.this);icon.setImageBitmap(bulbPhoto());icon.setScaleType(ImageView.ScaleType.FIT_CENTER);LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(dp(28),dp(36));ip.rightMargin=dp(9);nameRow.addView(icon,ip);title=text("",15,Palette.INK);nameRow.addView(title,new LinearLayout.LayoutParams(0,-2,1));view.addView(nameRow);status=text("",12,Palette.SECONDARY);status.setPadding(0,dp(5),0,0);view.addView(status);
            LinearLayout controls=new LinearLayout(MainActivity.this);
            if(livePage){
                toggle=button("",()->withBluetooth(()->{HueBle b=ReaderService.fleet.get(id);if(b!=null){if(b.isPaused())b.startSync();else b.restore();}}));addButton(controls,toggle);
                addButton(controls,button(s("settings"),()->withBluetooth(()->choosePosition(null,ReaderService.fleet.get(id)))));
            }else{toggle=button(s("disconnect"),()->{if(ReaderService.fleet!=null){HueBle b=ReaderService.fleet.get(id);if(b!=null)b.disconnect();}});addButton(controls,toggle);}
            view.addView(controls);
        }
        void update(HueBle bulb,int number){
            String label=number+" · "+bulb.savedName()+" · "+positionLabel(bulb.source)+" · "+I18n.factor(MainActivity.this,bulb.brightnessPercent);
            if(!label.contentEquals(title.getText()))title.setText(label);
            String connection=bulb.ready?s("connected"):bulb.isConnecting()?s("connecting"):s("disconnected");
            String message=connection+" · "+(bulb.syncing?s("streaming"):bulb.isPaused()?s("paused"):s("waiting"));
            if(!message.contentEquals(status.getText()))status.setText(message);
            if(livePage){String action=bulb.isPaused()?s("start"):s("stop");if(!action.contentEquals(toggle.getText()))toggle.setText(action);}else toggle.setEnabled(bulb.automatic||bulb.ready||bulb.isConnecting());
        }
    }
    private void renderBulbs(){
        if(bulbList==null)return;HueFleet fleet=ReaderService.fleet;
        ArrayList<HueBle> bulbs=fleet==null?new ArrayList<>():fleet.bulbs();ArrayList<String> ids=new ArrayList<>();for(HueBle b:bulbs)ids.add(b.id());
        if(displayedFleet!=fleet||!ids.equals(new ArrayList<>(bulbRows.keySet()))||(bulbList.getChildCount()==0)){
            displayedFleet=fleet;bulbRows.clear();bulbList.removeAllViews();int number=0;
            LinearLayout line=null;
            for(HueBle b:bulbs){BulbRow row=new BulbRow(b.id(),++number);bulbRows.put(b.id(),row);
                if(livePage){if(number%2==1){line=new LinearLayout(this);bulbList.addView(line,new LinearLayout.LayoutParams(-1,-2));}LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1);p.topMargin=dp(10);if(number%2==1)p.rightMargin=dp(10);line.addView(row.view,p);}
                else{LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.topMargin=dp(8);bulbList.addView(row.view,p);}
            }
            if(livePage&&bulbs.size()%2==1)line.addView(new View(this),new LinearLayout.LayoutParams(0,0,1));
            if(bulbs.isEmpty()){TextView empty=text(s("empty_bulbs"),15,Palette.SECONDARY);empty.setPadding(0,dp(20),0,dp(20));bulbList.addView(empty);}
        }
        int number=0,connected=0;for(HueBle b:bulbs){bulbRows.get(b.id()).update(b,++number);if(b.ready)connected++;}
        if(livePage&&readiness!=null){String summary=s("bulbs_count",connected,bulbs.size());if(!summary.contentEquals(readiness.getText()))readiness.setText(summary);}
    }
    private ArrayAdapter<String> spinnerAdapter(String[] values) { ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, values); return a; }
    private void fillAreas(JSONArray data) {
        areas.setOnItemSelectedListener(null); areaData = data; ArrayList<String> names = new ArrayList<>(); int selected = 0;
        try { for (int i = 0; i < data.length(); i++) { JSONObject area = data.getJSONObject(i); names.add(area.getString("name") + " · " + s("channels",area.getInt("channels"))); if (area.getString("id").equals(hue.areaId())) selected = i; } } catch (Exception ignored) {}
        if (names.isEmpty()) names.add(s("no_areas")); areas.setAdapter(spinnerAdapter(names.toArray(new String[0]))); areas.setEnabled(data.length() > 0); areas.setSelection(selected);
        areaHint.setText(data.length() == 0 ? s("area_create") : s("area_saved"));
        areas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { public void onNothingSelected(AdapterView<?> p) {} public void onItemSelected(AdapterView<?> p, View v, int position, long id) { try { if (position < areaData.length()) { JSONObject a = areaData.getJSONObject(position); hue.selectArea(a.getString("id"), a.getString("name")); } } catch (Exception ignored) {} } });
    }
    private void setBusy(boolean value) { busy = value; for (Button b : networkButtons) b.setEnabled(!value); if (address != null) address.setEnabled(!value); }
    private void setHueStatus(String value) { hueMessage = value; if (hueStatus != null) hueStatus.setText(value); }
    private void runNetwork(boolean pairing) {
        if (busy) return;
        try { hue.saveHost(address.getText().toString()); } catch (Exception e) { setHueStatus(s("invalid_ip")); return; }
        stopDiscovery(); setBusy(true); setHueStatus(pairing ? s("pairing") : s("testing"));
        jobs.execute(() -> { try { if (pairing) hue.pair(); JSONArray data = hue.testAndLoadAreas(); handler.post(() -> { if (destroyed) return; setBusy(false); fillAreas(data); setHueStatus(data.length() == 0 ? s("area_create") : s("bridge_connected")); }); } catch (Exception e) { String message = e.getMessage(); handler.post(() -> { if (destroyed) return; setBusy(false); setHueStatus(message!=null&&message.contains("düğme")?s("pair_button"):s("network_error")); }); } });
    }
    private void discover() {
        if (busy || discovery != null) return; setHueStatus(s("discovering"));
        discovery = new NsdManager.DiscoveryListener() {
            public void onDiscoveryStarted(String t) {}
            public void onServiceFound(NsdServiceInfo service) { if (destroyed || discovery == null) return; try { nsd.resolveService(service, new NsdManager.ResolveListener() { public void onResolveFailed(NsdServiceInfo s, int e) {} public void onServiceResolved(NsdServiceInfo s) { handler.post(() -> { if (destroyed || discovery == null || busy) return; if (s.getHost() instanceof java.net.Inet4Address) { address.setText(s.getHost().getHostAddress()); setHueStatus(s("bridge_found")); stopDiscovery(); } }); } }); } catch (Exception ignored) {} }
            public void onServiceLost(NsdServiceInfo s) {}
            public void onDiscoveryStopped(String t) {}
            public void onStartDiscoveryFailed(String t, int e) { handler.post(() -> { stopDiscovery(); setHueStatus(s("discovery_error")); }); }
            public void onStopDiscoveryFailed(String t, int e) {}
        };
        try { nsd.discoverServices("_hue._tcp.", NsdManager.PROTOCOL_DNS_SD, discovery); handler.postDelayed(discoveryTimeout, 8000); }
        catch (Exception e) { stopDiscovery(); setHueStatus(s("discovery_error")); }
    }
    private void stopDiscovery() { handler.removeCallbacks(discoveryTimeout); if (discovery != null) { try { nsd.stopServiceDiscovery(discovery); } catch (Exception ignored) {} discovery = null; } }
    protected void onResume() { super.onResume(); handler.post(tick); }
    protected void onPause() { handler.removeCallbacks(tick); stopDiscovery(); super.onPause(); }
    protected void onDestroy() { destroyed = true;cancelTvPairing(); stopDiscovery(); jobs.shutdownNow(); super.onDestroy(); }
    private void render(){renderTvStatus();renderBle();renderBulbs();if(preview!=null)preview.invalidate();}
}
