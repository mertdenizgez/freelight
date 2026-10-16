package local.ambilight.reader;
import android.content.*;
/** Test double: no Bluetooth radio or Android process is used. */
public class HueBle {
 public String source;public boolean automatic,paused,closed,scanStopped; public final boolean discoveryOnly;
 final SharedPreferences prefs;
 public static class Device{final String id;Device(String id){this.id=id;}public String getAddress(){return id;}}
 public static class Found{public final Device device;public final String name;Found(String id,String name){device=new Device(id);this.name=name;}}
 public HueBle(Context c,String key,boolean discovery){discoveryOnly=discovery;prefs=c.getSharedPreferences(key,0);source=prefs.getString("position_"+prefs.getString("address",""),"left");automatic=prefs.getBoolean("automatic",true);}
 public String id(){return prefs.getString("address","");}
 public void setPosition(String s){source=s;prefs.edit().putString("position_"+id(),s).apply();}
 public void startSync(){automatic=true;paused=false;prefs.edit().putBoolean("automatic",true).apply();}
 public void restore(){paused=true;}
 public void disconnect(){automatic=false;paused=true;prefs.edit().putBoolean("automatic",false).apply();}
 public void endScan(){scanStopped=true;}
 public void close(){closed=true;}
}
