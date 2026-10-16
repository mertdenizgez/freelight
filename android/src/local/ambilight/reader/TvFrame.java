package local.ambilight.reader;
import org.json.JSONObject;
import java.util.Iterator;
public final class TvFrame {
    public static JSONObject validate(JSONObject frame)throws Exception{
        JSONObject layer=frame.getJSONObject("layer1");for(String side:new String[]{"left","top","right"}){JSONObject zones=layer.getJSONObject(side);if(zones.length()==0)throw new IllegalArgumentException("Missing zones");Iterator<String> keys=zones.keys();while(keys.hasNext()){JSONObject rgb=zones.getJSONObject(keys.next());for(String channel:new String[]{"r","g","b"}){Object value=rgb.get(channel);if(!(value instanceof Integer)||(Integer)value<0||(Integer)value>255)throw new IllegalArgumentException("Invalid RGB");}}}return frame;
    }
}
