package local.ambilight.reader;
import java.util.*;import org.json.*;
public class LocalTvApi {public static boolean reject,unavailable,badFrame;public static int grants;public static Runnable duringGrant;public static class Failure extends Exception {Failure(){super("Unavailable");}}
public LocalTvApi(String fingerprint){}public LocalTvApi(String fingerprint,String preferred){if(!fingerprint.equals("test-certificate"))throw new AssertionError("Session certificate not pinned");}public static List<String> localAddresses(){return Arrays.asList("127.0.0.1");}public String host(){return "127.0.0.1";}public String certificate(){return "test-certificate";}public void close(){}
public JSONObject request(String method,String path,JSONObject json,TvCredentials credentials)throws Exception{
 if(unavailable)throw new Failure();
 if(path.endsWith("/request"))return new JSONObject().put("error_id","SUCCESS").put("auth_key","test-session-key").put("timestamp",140049L).put("timeout",60);
 if(path.endsWith("/grant")){grants++;if(!json.getJSONObject("auth").getString("pin").equals("3467"))throw new AssertionError("Unexpected test PIN");if(duringGrant!=null)duringGrant.run();return new JSONObject().put("error_id",reject?"DENIED":"SUCCESS");}
 JSONObject layer=new JSONObject();for(String edge:new String[]{"left","top","right"})layer.put(edge,new JSONObject().put("0",new JSONObject().put("r",badFrame?999:10).put("g",20).put("b",30)));return new JSONObject().put("layer1",layer);
}}
