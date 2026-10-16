package local.ambilight.reader;
import android.app.Instrumentation;import android.os.Bundle;import org.json.*;
public class TestHarness extends Instrumentation {
 public void onCreate(Bundle args){super.onCreate(args);start();}
 public void onStart(){Bundle result=new Bundle();try{
  getTargetContext().getSharedPreferences("hue_test",0).edit().clear().commit();
  class Fake extends HueClient {String response;boolean authenticated;String path;Fake(){super(getTargetContext(),"hue_test");}protected String request(String method,String endpoint,JSONObject body,boolean auth)throws Exception{authenticated=auth;path=endpoint;if(endpoint.equals("/api")&&(!body.getBoolean("generateclientkey")||!method.equals("POST")))throw new AssertionError("registration request");return response;}}
  Fake hue=new Fake();hue.saveHost("192.168.1.20");
  for(String address:new String[]{"8.8.8.8","192.168.1.999","https://192.168.1.20",""}){boolean failed=false;try{HueClient.validateHost(address);}catch(Exception e){failed=true;}if(!failed)throw new AssertionError("invalid host accepted");}
  hue.response="[{\"error\":{\"type\":101}}]";boolean failed=false;try{hue.pair();}catch(Exception e){failed=e.getMessage().contains("düğmeye");}if(!failed||hue.paired())throw new AssertionError("link button error");
  hue.response="[{\"success\":{\"username\":\"fake-key\",\"clientkey\":\"0123456789abcdef0123456789abcdef\"}}]";hue.pair();if(!hue.paired()||hue.authenticated||!hue.path.equals("/api"))throw new AssertionError("pairing");
  hue.response="{\"errors\":[],\"data\":[{\"id\":\"area-1\",\"metadata\":{\"name\":\"Salon\"},\"channels\":[{\"channel_id\":0}]}]}";JSONArray areas=hue.testAndLoadAreas();if(areas.length()!=1||!hue.authenticated||!hue.path.equals("/clip/v2/resource/entertainment_configuration"))throw new AssertionError("areas");
  hue.selectArea("area-1","Salon");hue.source("left");if(!hue.areaId().equals("area-1")||hue.cachedAreas().length()!=1)throw new AssertionError("selection persistence");
  hue.response="{\"errors\":[],\"data\":[]}";hue.testAndLoadAreas();if(!hue.areaId().isEmpty())throw new AssertionError("removed selection");
  hue.saveHost("192.168.1.21");if(hue.paired())throw new AssertionError("key reused across hosts");
  hue.response="[{\"success\":{\"username\":\"fake-key\",\"clientkey\":\"bad\"}}]";failed=false;try{hue.pair();}catch(Exception e){failed=true;}if(!failed||hue.paired())throw new AssertionError("invalid client key");
  result.putString("stream","PASS: local IP validation; button error; client key registration; authorized area read; persistence; removed area; changed host invalidates keys; malformed key rejected.");finish(-1,result);
 }catch(Throwable e){result.putString("stream","FAIL: "+e.toString());finish(0,result);}finally{getTargetContext().getSharedPreferences("hue_test",0).edit().clear().commit();}}
}
