package local.ambilight.reader;
import java.util.*;
public final class LocaleGeometryTest {
 public static void main(String[] args){
  String[][] samples={{"system","tr-TR","tr"},{"system","de-DE","de"},{"system","es-MX","es"},{"system","fr-CA","fr"},{"system","pt-BR","pt"},{"system","zh-Hant-TW","zh"},{"system","in-ID","id"},{"system","ar-SA","ar"},{"system","fi-FI","en"},{"system","fa-IR","en"},{"en","tr-TR","en"},{"ar","en-US","ar"},{"de","ar-SA","de"},{"bad","tr-TR","en"}};
  for(String[] sample:samples)if(!LanguagePolicy.resolve(sample[0],sample[1]).equals(sample[2]))throw new AssertionError(Arrays.toString(sample));
  for(String lang:LanguagePolicy.CODES)if(!LanguagePolicy.resolve(lang,"zz").equals(lang))throw new AssertionError(lang);
  for(float density:new float[]{1,1.5f,2,3})for(int width:new int[]{640,904,1280}){
   RoomGeometry g=new RoomGeometry(width*density,400*density,density,1672,941);
   for(String source:new String[]{"upper_left","top","upper_right"}){
    float[] p=g.anchor(source,0,1);if(p[1]-28*density<0||p[1]+52*density>g.top-12*density)throw new AssertionError("Upper lamp or label overlaps TV: "+source);
   }
   if(!(g.anchor("left",0,1)[0]<g.left&&g.anchor("right",0,1)[0]>g.right))throw new AssertionError("Physical sides changed");
  }
  System.out.println("PASS: 18 languages; regional locales; Indonesian alias; explicit selection; English fallback; upper bulb and label clearance on 12 viewport/density combinations; physical L/R.");
 }
}
