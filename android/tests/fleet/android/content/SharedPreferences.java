package android.content;
import java.util.*;
public class SharedPreferences {
 final Map<String,Object> data=new HashMap<>();
 public String getString(String k,String d){return (String)data.getOrDefault(k,d);}
 public boolean getBoolean(String k,boolean d){return (Boolean)data.getOrDefault(k,d);}
 public int getInt(String k,int d){return (Integer)data.getOrDefault(k,d);}
 @SuppressWarnings("unchecked") public Set<String> getStringSet(String k,Set<String> d){return (Set<String>)data.getOrDefault(k,d);}
 public boolean contains(String k){return data.containsKey(k);}
 public Editor edit(){return new Editor();}
 public class Editor {
 final Map<String,Object> changes=new HashMap<>();
 public Editor putString(String k,String v){changes.put(k,v);return this;}
 public Editor putBoolean(String k,boolean v){changes.put(k,v);return this;}
 public Editor putInt(String k,int v){changes.put(k,v);return this;}
 public Editor putStringSet(String k,Set<String> v){changes.put(k,new HashSet<>(v));return this;}
 public boolean commit(){data.putAll(changes);return true;}
 public void apply(){commit();}
 }
}
