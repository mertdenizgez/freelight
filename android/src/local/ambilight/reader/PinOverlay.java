package local.ambilight.reader;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.LongSupplier;

/** User-authorized, temporary remote keypad. Never reads another app's window or PIN. */
final class PinOverlay implements AutoCloseable {
    private final Context context;
    private final WindowManager windows;
    private final Handler handler=new Handler(Looper.getMainLooper());
    private final LinearLayout root;
    private final TextView status;
    private final Button confirm;
    private final ArrayList<Button> controls=new ArrayList<>();
    private final StringBuilder digits=new StringBuilder();
    private final LongSupplier remaining;
    private final Consumer<String> submit;
    private final Runnable cancel,expired;
    private final long visibleUntil=SystemClock.elapsedRealtime()+30000;
    private boolean attached,closed,busy;
    private String message="";
    private final Runnable tick=new Runnable(){public void run(){
        if(closed)return;
        if(!busy&&(remaining.getAsLong()<=0||SystemClock.elapsedRealtime()>=visibleUntil)){expired.run();return;}
        render();handler.postDelayed(this,250);
    }};
    PinOverlay(Context context,LongSupplier remaining,Consumer<String> submit,Runnable cancel,Runnable expired){
        this.context=context;this.remaining=remaining;this.submit=submit;this.cancel=cancel;this.expired=expired;
        windows=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        root=new LinearLayout(context){@Override public boolean dispatchKeyEvent(KeyEvent event){
            int code=event.getKeyCode();int digit=code>=KeyEvent.KEYCODE_0&&code<=KeyEvent.KEYCODE_9?code-KeyEvent.KEYCODE_0:code>=KeyEvent.KEYCODE_NUMPAD_0&&code<=KeyEvent.KEYCODE_NUMPAD_9?code-KeyEvent.KEYCODE_NUMPAD_0:-1;
            if(digit>=0){if(event.getAction()==KeyEvent.ACTION_DOWN&&event.getRepeatCount()==0)append(digit);return true;}
            if(code==KeyEvent.KEYCODE_BACK){if(event.getAction()==KeyEvent.ACTION_UP)cancel.run();return true;}
            if(code==KeyEvent.KEYCODE_DEL){if(event.getAction()==KeyEvent.ACTION_DOWN)erase();return true;}
            return super.dispatchKeyEvent(event);
        }};
        root.setOrientation(LinearLayout.VERTICAL);root.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);root.setPadding(dp(18),dp(8),dp(18),dp(8));root.setBackground(shape(Palette.SURFACE,Palette.FOCUS_BORDER));
        status=new TextView(context);status.setTextColor(Palette.INK);status.setTextSize(16);status.setGravity(Gravity.CENTER);root.addView(status,new LinearLayout.LayoutParams(-1,dp(30)));
        LinearLayout numbers=new LinearLayout(context);root.addView(numbers,new LinearLayout.LayoutParams(-1,dp(48)));
        for(int i=0;i<10;i++){final int digit=i;add(numbers,button(Integer.toString(i),()->append(digit)));}
        LinearLayout actions=new LinearLayout(context);LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(-1,dp(48));ap.topMargin=dp(6);root.addView(actions,ap);
        add(actions,button("⌫",this::erase));add(actions,button(text("cancel"),cancel));confirm=button(text("tv_confirm"),()->{if(!busy&&digits.length()==4){setBusy(true);submit.accept(digits.toString());}});add(actions,confirm);
        render();
    }
    private int dp(int value){return Math.round(value*context.getResources().getDisplayMetrics().density);}
    private String text(String key){return I18n.text(context,key);}
    private GradientDrawable shape(int color,int border){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(10));if(border!=0)d.setStroke(dp(2),border);return d;}
    private Button button(String label,Runnable action){Button b=new Button(context);b.setId(View.generateViewId());b.setText(label);b.setAllCaps(false);b.setTextColor(Palette.INK);b.setTextSize(14);b.setMinWidth(0);b.setMinimumWidth(0);b.setPadding(dp(6),0,dp(6),0);b.setStateListAnimator(null);android.graphics.drawable.StateListDrawable bg=new android.graphics.drawable.StateListDrawable();bg.addState(new int[]{android.R.attr.state_focused},shape(Palette.FOCUS,Palette.FOCUS_BORDER));bg.addState(new int[]{},shape(Palette.BUTTON,0));b.setBackground(bg);b.setOnClickListener(v->action.run());controls.add(b);return b;}
    private void add(LinearLayout row,Button b){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-1,1);p.setMargins(dp(3),0,dp(3),0);row.addView(b,p);}
    private void append(int digit){if(closed||busy||digits.length()>=4)return;digits.append(digit);message="";render();if(digits.length()==4)confirm.requestFocus();}
    private void erase(){if(closed||busy)return;if(digits.length()>0)digits.deleteCharAt(digits.length()-1);message="";render();}
    private void render(){if(closed)return;String value=digits.toString();while(value.length()<4)value+="·";status.setText("Freelight · "+(message.isEmpty()?text("tv_pin_hint")+"  "+value:message));confirm.setEnabled(!busy&&digits.length()==4);}
    void setBusy(boolean value){busy=value;for(Button b:controls)b.setEnabled(!value);message=value?text("tv_verifying"):"";render();}
    void error(String value){setBusy(false);message=value;render();}
    void show(){if(closed)throw new IllegalStateException();WindowManager.LayoutParams p=new WindowManager.LayoutParams(-1,-2,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,PixelFormat.TRANSLUCENT);p.gravity=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;p.y=dp(10);p.setTitle("Freelight PIN keypad");windows.addView(root,p);attached=true;controls.get(0).requestFocus();handler.post(tick);}
    public void close(){if(closed)return;closed=true;handler.removeCallbacks(tick);digits.setLength(0);if(attached){attached=false;try{windows.removeViewImmediate(root);}catch(IllegalArgumentException ignored){}}}
}
