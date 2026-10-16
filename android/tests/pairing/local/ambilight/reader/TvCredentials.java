package local.ambilight.reader;
import android.content.Context;
public final class TvCredentials {public final String user,key,fingerprint;public static TvCredentials stored;public static int saves;public TvCredentials(String u,String k,String f){user=u;key=k;fingerprint=f;}public static void save(Context c,TvCredentials v){stored=v;saves++;}}
