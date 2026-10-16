package local.ambilight.reader;
import android.content.*;
public class FleetTest {
 static void check(boolean v,String s){if(!v)throw new AssertionError(s);}
 public static void main(String[] args){
  Context c=new Context();String a="AA:00:00:00:00:01",b="AA:00:00:00:00:02";
  c.getSharedPreferences("bluetooth",0).edit().putString("address",a).putString("name","Old Hue").putString("position_"+a,"left").putString("baseline","warm-original").putBoolean("automatic",true).commit();
  HueFleet fleet=new HueFleet(c);HueBle first=fleet.get(a);
  check(fleet.bulbs().size()==1&&first.source.equals("left"),"legacy bulb migration");
  check(c.getSharedPreferences(HueFleet.preferences(a),0).getString("baseline","").equals("warm-original"),"original light migrated");
  fleet.add(new HueBle.Found(b,"Second Hue"),"right");HueBle second=fleet.get(b);
  check(fleet.bulbs().size()==2&&fleet.get(a)==first&&!first.closed,"adding second must retain first session");
  check(first.source.equals("left")&&second.source.equals("right"),"independent positions");
  check(!c.getSharedPreferences(HueFleet.preferences(b),0).contains("baseline"),"baseline must not leak");
  first.restore();check(first.paused&&!second.paused,"pause isolation");
  first.disconnect();check(!first.automatic&&second.automatic,"disconnect isolation");
  second.setPosition("top");check(first.source.equals("left"),"position isolation");
  fleet.add(new HueBle.Found(b,"Second Hue"),"right");check(fleet.bulbs().size()==2&&fleet.get(b)==second,"duplicate deduplicated");
  fleet.close();check(first.closed&&second.closed&&fleet.discovery.closed,"all sessions released");
  HueFleet reboot=new HueFleet(c);check(reboot.bulbs().size()==2,"registry survives restart");
  check(!reboot.get(a).automatic&&reboot.get(b).automatic,"disconnect survives restart");
  check(reboot.get(b).source.equals("right"),"position survives restart");
  reboot.startAll();check(reboot.get(a).automatic&&reboot.get(b).automatic,"start all");
  reboot.stopAll();check(reboot.get(a).paused&&reboot.get(b).paused,"stop all");
  int before=reboot.bulbs().size();boolean rejected=false;try{reboot.add(new HueBle.Found("unknown","bad"),"bottom");}catch(IllegalArgumentException e){rejected=true;}
  check(rejected&&reboot.bulbs().size()==before,"invalid position is atomic");
  check(reboot.discovery.discoveryOnly,"discovery independent from bulb sessions");
  System.out.println("PASS: two-session isolation; migration and original light; add without replacing; duplicate prevention; persistent disconnect/position; start/stop all; invalid position; resource cleanup.");
 }
}
