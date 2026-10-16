package local.ambilight.reader;

import android.content.Context;
import android.graphics.*;
import android.os.SystemClock;
import android.view.*;
import java.util.*;

/** Static local room asset, with live light overlays and remote-friendly bulb selection. */
public final class RoomScene extends View {
    public interface Listener {void select(String id);void addBulb();}
    private final Bitmap room,photo;
    private final Listener listener;
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private final ArrayList<RectF> hits=new ArrayList<>();
    private final ArrayList<String> ids=new ArrayList<>();
    private int selected=0;
    public RoomScene(Context context,Bitmap image,Bitmap bulbPhoto,Listener listener){
        super(context);room=image;photo=bulbPhoto;this.listener=listener;setFocusable(true);setFocusableInTouchMode(true);setClickable(true);
        setContentDescription(I18n.text(context,"room_accessibility"));
    }
    private float dp(float value){return value*getResources().getDisplayMetrics().density;}
    private int color(String source){
        try{if(SystemClock.elapsedRealtime()-ReaderService.last>2000)return Color.rgb(147,125,103);int[] rgb=HueBle.sampleFrame(ReaderService.frame,source);return Color.rgb(rgb[0],rgb[1],rgb[2]);}catch(Exception ignored){return Color.rgb(147,125,103);}
    }
    private void glow(Canvas canvas,float x,float y,float radius,int color,int alpha){
        paint.setShader(new RadialGradient(x,y,radius,new int[]{Color.argb(alpha,Color.red(color),Color.green(color),Color.blue(color)),Color.TRANSPARENT},null,Shader.TileMode.CLAMP));canvas.drawCircle(x,y,radius,paint);paint.setShader(null);
    }
    private final ColorMatrixColorFilter offFilter=new ColorMatrixColorFilter(new float[]{.55f,0,0,0,0, 0,.55f,0,0,0, 0,0,.55f,0,0, 0,0,0,1,0});
    private final ColorMatrixColorFilter onFilter=new ColorMatrixColorFilter(new float[]{1.03f,0,0,0,8, 0,1.03f,0,0,8, 0,0,1.03f,0,8, 0,0,0,1,0});
    private void bulb(Canvas canvas,float x,float y,int shade,boolean on,int brightness,boolean focus){
        if(on){float[] hsv=new float[3];Color.colorToHSV(shade,hsv);hsv[2]=Math.max(.65f,hsv[2]);int glowColor=Color.HSVToColor(hsv);int alpha=85+Math.round(110*(float)Math.sqrt(Math.min(254,brightness)/254f));glow(canvas,x,y-dp(9),dp(48),glowColor,alpha);glow(canvas,x,y-dp(9),dp(25),glowColor,Math.min(210,alpha+30));}
        RectF target=new RectF(x-dp(28),y-dp(28),x+dp(28),y+dp(28));int photoLayer=canvas.saveLayer(target,null);paint.setColor(Color.WHITE);paint.setAlpha(on?255:195);paint.setColorFilter(on?onFilter:offFilter);canvas.drawBitmap(photo,null,target,paint);paint.setColorFilter(null);paint.setAlpha(255);
        if(on){float[] tint=new float[3];Color.colorToHSV(shade,tint);tint[2]=.95f;int visible=Color.HSVToColor(tint);paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));paint.setShader(new LinearGradient(0,y-dp(24),0,y+dp(17),new int[]{Color.argb(150,Color.red(visible),Color.green(visible),Color.blue(visible)),Color.TRANSPARENT},null,Shader.TileMode.CLAMP));canvas.drawRect(target,paint);paint.setShader(null);paint.setXfermode(null);}
        canvas.restoreToCount(photoLayer);
        if(focus){paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(dp(1.6f));paint.setColor(0xFFE6CDA9);canvas.drawRoundRect(new RectF(x-dp(25),y-dp(30),x+dp(25),y+dp(30)),dp(10),dp(10),paint);paint.setStyle(Paint.Style.FILL);}
    }
    @Override protected void onFocusChanged(boolean gain,int direction,Rect previous){super.onFocusChanged(gain,direction,previous);invalidate();}
    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);float w=getWidth(),h=getHeight();
        Path clip=new Path();clip.addRoundRect(new RectF(0,0,w,h),dp(20),dp(20),Path.Direction.CW);canvas.save();canvas.clipPath(clip);
        RoomGeometry geometry=new RoomGeometry(w,h,getResources().getDisplayMetrics().density,room.getWidth(),room.getHeight());float scale=geometry.scale,offset=geometry.imageOffset;canvas.drawColor(Color.rgb(61,51,40));
        // The generated room has a front-on TV at x=.321..679, y=.217..562.
        canvas.drawBitmap(room,null,new RectF(0,offset,w,offset+room.getHeight()*scale),paint);
        RectF tv=new RectF(geometry.left,geometry.top,geometry.right,geometry.bottom);
        canvas.save();canvas.clipOutRect(tv);
        if(ReaderService.running&&SystemClock.elapsedRealtime()-ReaderService.last<2000){
            glow(canvas,tv.left,tv.centerY(),tv.height()*.9f,color("left"),155);
            glow(canvas,tv.centerX(),tv.top,tv.width()*.58f,color("top"),155);
            glow(canvas,tv.right,tv.centerY(),tv.height()*.9f,color("right"),155);
        }
        canvas.restore();
        paint.setShader(new LinearGradient(0,h*.78f,0,h,new int[]{Color.TRANSPARENT,Color.argb(85,8,14,23)},null,Shader.TileMode.CLAMP));canvas.drawRect(0,h*.78f,w,h,paint);paint.setShader(null);
        ArrayList<HueBle> bulbs=ReaderService.fleet==null?new ArrayList<>():ReaderService.fleet.bulbs();
        hits.clear();ids.clear();int[] counts=new int[5],seen=new int[5];for(HueBle b:bulbs)counts[BulbPosition.index(b.source)]++;
        if(!bulbs.isEmpty())selected=Math.min(selected,bulbs.size()-1);
        for(int n=0;n<bulbs.size();n++){
            HueBle b=bulbs.get(n);int region=BulbPosition.index(b.source),slot=seen[region]++;float[] anchor=geometry.anchor(b.source,slot,counts[region]);float x=anchor[0],y=anchor[1];bulb(canvas,x,y,b.syncing?color(b.source):Color.rgb(255,194,115),b.isLightOn(),b.lastBrightness,hasFocus()&&n==selected);
            paint.setTextAlign(Paint.Align.CENTER);paint.setTextSize(dp(11));String label=(n+1)+" · "+b.savedName();if(label.length()>23)label=label.substring(0,22)+"…";
            float textWidth=paint.measureText(label);RectF pill=new RectF(x-textWidth/2-dp(7),y+dp(32),x+textWidth/2+dp(7),y+dp(52));paint.setColor(Color.argb(235,247,240,229));canvas.drawRoundRect(pill,dp(7),dp(7),paint);paint.setColor(b.ready?Palette.INK:Palette.MUTED);canvas.drawText(label,x,y+dp(46),paint);
            hits.add(new RectF(Math.min(pill.left,x-dp(25)),y-dp(30),Math.max(pill.right,x+dp(25)),pill.bottom));ids.add(b.id());
        }
        canvas.restore();
    }
    @Override public boolean onTouchEvent(android.view.MotionEvent event){
        if(event.getAction()==MotionEvent.ACTION_DOWN)return true;
        if(event.getAction()==MotionEvent.ACTION_UP){for(int i=0;i<hits.size();i++)if(hits.get(i).contains(event.getX(),event.getY())){selected=i;requestFocus();performClick();return true;}return true;}return true;
    }
    @Override public boolean performClick(){super.performClick();if(ids.isEmpty())listener.addBulb();else listener.select(ids.get(Math.min(selected,ids.size()-1)));return true;}
    @Override public boolean onKeyDown(int key,KeyEvent event){
        if(key==KeyEvent.KEYCODE_DPAD_LEFT||key==KeyEvent.KEYCODE_DPAD_RIGHT){if(!ids.isEmpty()){selected=(selected+(key==KeyEvent.KEYCODE_DPAD_RIGHT?1:ids.size()-1))%ids.size();invalidate();}return true;}
        if(key==KeyEvent.KEYCODE_DPAD_CENTER||key==KeyEvent.KEYCODE_ENTER){if(event.getRepeatCount()==0)performClick();return true;}
        return super.onKeyDown(key,event);
    }
}
