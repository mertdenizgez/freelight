package android.content;
import java.util.*;
public class Context {
 final Map<String,SharedPreferences> prefs=new HashMap<>();
 public Context getApplicationContext(){return this;}
 public SharedPreferences getSharedPreferences(String name,int mode){return prefs.computeIfAbsent(name,k->new SharedPreferences());}
}
