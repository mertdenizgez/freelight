package local.ambilight.reader;
import android.content.*;
import android.content.res.*;
import java.util.*;
public final class I18n {
    public static Context wrap(Context context){
        String selected=context.getSharedPreferences("ui",0).getString("language","system");
        String system=Resources.getSystem().getConfiguration().getLocales().get(0).toLanguageTag();
        Locale locale=Locale.forLanguageTag(LanguagePolicy.resolve(selected,system));Configuration config=new Configuration(context.getResources().getConfiguration());config.setLocale(locale);config.setLayoutDirection(locale);return context.createConfigurationContext(config);
    }
    public static String text(Context context,String key,Object... values){int id=context.getResources().getIdentifier(key,"string",context.getPackageName());if(id==0)throw new IllegalArgumentException("Missing translation: "+key);return context.getString(id,values);}
    public static String position(Context context,String source){return text(context,"position_"+BulbPosition.normalize(source));}
    public static String factor(Context context,int percent){return text(context,"factor",percent/100.0);}
}
