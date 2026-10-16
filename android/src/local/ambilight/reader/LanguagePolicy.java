package local.ambilight.reader;
import java.util.*;
/** Locale selection independent of Android, including a deterministic English fallback. */
public final class LanguagePolicy {
    public static final String[] CODES={"en","tr","de","fr","es","ru","ar","it","pt","nl","pl","uk","zh","ja","ko","hi","id","sv"};
    public static final String[] NAMES={"English","Türkçe","Deutsch","Français","Español","Русский","العربية","Italiano","Português","Nederlands","Polski","Українська","中文","日本語","한국어","हिन्दी","Bahasa Indonesia","Svenska"};
    public static String normalize(String tag){if(tag==null)return "en";String code=tag.replace('_','-').toLowerCase(Locale.ROOT).split("-")[0];if(code.equals("in"))code="id";for(String supported:CODES)if(supported.equals(code))return code;return "en";}
    public static String resolve(String preference,String system){return normalize(preference==null||preference.equals("system")?system:preference);}
}
