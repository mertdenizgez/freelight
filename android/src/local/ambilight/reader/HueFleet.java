package local.ambilight.reader;

import android.content.*;
import java.util.*;

/** Main-thread owner of independent bulb sessions; discovery never owns a bulb link. */
public final class HueFleet {
    private final Context context;
    private final SharedPreferences registry;
    private final LinkedHashMap<String,HueBle> bulbs=new LinkedHashMap<>();
    public final HueBle discovery;
    public static String preferences(String address){return "hue_bulb_"+address;}
    public HueFleet(Context context){
        this.context=context.getApplicationContext();registry=context.getSharedPreferences("hue_fleet",0);
        migrate();
        for(String id:new TreeSet<>(registry.getStringSet("addresses",Collections.emptySet())))
            bulbs.put(id,new HueBle(this.context,preferences(id),false));
        discovery=new HueBle(this.context,"hue_discovery",true);
    }
    private void migrate(){
        if(registry.getBoolean("migrated",false))return;
        SharedPreferences old=context.getSharedPreferences("bluetooth",0);
        String id=old.getString("address","");
        Set<String> ids=new HashSet<>(registry.getStringSet("addresses",Collections.emptySet()));
        if(!id.isEmpty()){
            SharedPreferences.Editor e=context.getSharedPreferences(preferences(id),0).edit();
            e.putString("address",id).putString("name",old.getString("name","Hue ampul"))
                .putString("position_"+id,BulbPosition.normalize(old.getString("position_"+id,"left")))
                .putBoolean("automatic",old.getBoolean("automatic",true))
                .putInt("brightness_multiplier",old.getInt("brightness_multiplier",2));
            if(old.contains("baseline"))e.putString("baseline",old.getString("baseline","{}"));
            if(!e.commit())throw new IllegalStateException("Ampul ayarı kaydedilemedi");
            ids.add(id);
        }
        if(!registry.edit().putStringSet("addresses",ids).putBoolean("migrated",true).commit())throw new IllegalStateException("Ampul listesi kaydedilemedi");
    }
    public synchronized ArrayList<HueBle> bulbs(){return new ArrayList<>(bulbs.values());}
    public synchronized HueBle get(String id){return bulbs.get(id);}
    public String position(HueBle.Found found){HueBle bulb=get(found.device.getAddress());return bulb==null?"":bulb.source;}
    public synchronized void add(HueBle.Found found,String position){
        if(BulbPosition.index(position)<0)throw new IllegalArgumentException("Konum seçin");
        String id=found.device.getAddress();HueBle bulb=bulbs.get(id);
        if(bulb==null){
            SharedPreferences prefs=context.getSharedPreferences(preferences(id),0);
            if(!prefs.edit().putString("address",id).putString("name",found.name)
                .putString("position_"+id,position).putBoolean("automatic",true).commit())throw new IllegalStateException("Ampul kaydedilemedi");
            Set<String> ids=new HashSet<>(registry.getStringSet("addresses",Collections.emptySet()));ids.add(id);
            if(!registry.edit().putStringSet("addresses",ids).commit())throw new IllegalStateException("Ampul listesi kaydedilemedi");
            bulb=new HueBle(context,preferences(id),false);bulbs.put(id,bulb);
        }else bulb.setPosition(position);
        discovery.endScan();bulb.startSync();
    }
    public synchronized void startAll(){for(HueBle b:bulbs.values())b.startSync();}
    public synchronized void stopAll(){for(HueBle b:bulbs.values())b.restore();}
    public synchronized void close(){discovery.close();for(HueBle b:bulbs.values())b.close();bulbs.clear();}
}
