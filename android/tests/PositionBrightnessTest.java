package local.ambilight.reader;
import java.util.*;
public class PositionBrightnessTest {
 static void same(int[] a,int... b){if(!Arrays.equals(a,b))throw new AssertionError(Arrays.toString(a)+" != "+Arrays.toString(b));}
 public static void main(String[] args){
  // API pixels are clockwise: left is bottom-to-top, top is left-to-right, right top-to-bottom.
  int[][] left={{255,0,0},{0,255,0},{0,0,255}};
  int[][] right={{255,255,0},{255,0,255},{0,255,255}};
  int[][] top={{0,255,255},{0,255,255},{255,0,0},{255,0,0},{255,255,255},{255,255,255}};
  same(BulbPosition.sample("left",left,top,right),85,85,85);
  same(BulbPosition.sample("right",left,top,right),170,170,170);
  same(BulbPosition.sample("upper_left",left,top,right),0,127,255);
  same(BulbPosition.sample("upper_right",left,top,right),255,255,127);
  same(BulbPosition.sample("top",left,top,right),170,170,170);
  for(int v=0;v<=255;v++)for(int p=0;p<=400;p+=25){int got=HueBleColor.scaledBrightness(v,v,v,p);if(got!=Math.min(254,v*p/100))throw new AssertionError("brightness");}
  if(HueBleColor.scaledBrightness(50,30,20,200)!=100||HueBleColor.scaledBrightness(50,30,20,50)!=25||HueBleColor.scaledBrightness(50,30,20,0)!=0)throw new AssertionError();
  boolean failed=false;try{BulbPosition.sample("bottom",left,top,right);}catch(IllegalArgumentException e){failed=true;}if(!failed)throw new AssertionError("invalid region accepted");
  same(BulbPosition.sample("upper_left",new int[][]{{100,10,20}},new int[][]{{0,20,40}},right),50,15,30);
  System.out.println("PASS: 5 regions; clockwise corner mapping; equal edge weighting; sparse topology; 4352 brightness cases at 0–4x; invalid position rejection.");
 }
}
