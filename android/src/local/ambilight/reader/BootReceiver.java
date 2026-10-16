package local.ambilight.reader;
import android.content.*;
public class BootReceiver extends BroadcastReceiver{public void onReceive(Context c,Intent i){if((Intent.ACTION_BOOT_COMPLETED.equals(i.getAction())||Intent.ACTION_MY_PACKAGE_REPLACED.equals(i.getAction()))&&ReaderService.canStart(c)&&c.getSharedPreferences("settings",0).getBoolean("enabled",false))try{c.startForegroundService(new Intent(c,ReaderService.class));}catch(Exception e){android.util.Log.w("AmbilightReader","Boot start failed",e);}}}
