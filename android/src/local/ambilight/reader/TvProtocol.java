package local.ambilight.reader;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** JointSpace v6 wire protocol. The shared HMAC seed is public protocol data, not a user's TV key. */
public final class TvProtocol {
    private static final String SEED="JCqdN5AcnAHgJYseUn7ER5k3qgtemfUvMRghQpTfTZq7Cvv8EPQPqfz6dDxPQPSu4gKFPWkJGw32zyASgJkHwCjU";
    public static String hex(byte[] bytes){StringBuilder s=new StringBuilder();for(byte b:bytes)s.append(String.format(Locale.ROOT,"%02x",b&255));return s.toString();}
    public static String signature(long timestamp,String pin)throws Exception{
        if(timestamp<0||pin==null||!pin.matches("[0-9]{4}"))throw new IllegalArgumentException("Invalid PIN");
        Mac mac=Mac.getInstance("HmacSHA1");mac.init(new SecretKeySpec(Base64.getDecoder().decode(SEED),"HmacSHA1"));
        return Base64.getEncoder().encodeToString(hex(mac.doFinal((timestamp+pin).getBytes(StandardCharsets.UTF_8))).getBytes(StandardCharsets.US_ASCII));
    }
    private static String md5(String text)throws Exception{return hex(MessageDigest.getInstance("MD5").digest(text.getBytes(StandardCharsets.UTF_8)));}
    private static String quoted(String text){return "\""+text.replace("\\","\\\\").replace("\"","\\\"")+"\"";}
    public static String digest(String challenge,String method,String path,String user,String key,String cnonce)throws Exception{
        if(challenge==null||!challenge.regionMatches(true,0,"Digest ",0,7))throw new IllegalArgumentException("Digest challenge missing");
        Map<String,String> fields=new HashMap<>();Matcher m=Pattern.compile("(\\w+)=(?:\"([^\"]*)\"|([^, ]+))").matcher(challenge);while(m.find())fields.put(m.group(1),m.group(2)!=null?m.group(2):m.group(3));
        String realm=fields.get("realm"),nonce=fields.get("nonce");if(realm==null||nonce==null||!fields.getOrDefault("algorithm","MD5").equalsIgnoreCase("MD5"))throw new IllegalArgumentException("Unsupported digest");
        String a1=md5(user+":"+realm+":"+key),a2=md5(method+":"+path),response;
        String result="Digest username="+quoted(user)+", realm="+quoted(realm)+", nonce="+quoted(nonce)+", uri="+quoted(path);
        if(fields.containsKey("qop")){
            boolean auth=false;for(String q:fields.get("qop").split(","))if(q.trim().equals("auth"))auth=true;if(!auth)throw new IllegalArgumentException("Unsupported qop");
            response=md5(a1+":"+nonce+":00000001:"+cnonce+":auth:"+a2);result+=", qop=auth, nc=00000001, cnonce="+quoted(cnonce);
        }else response=md5(a1+":"+nonce+":"+a2);
        result+=", response="+quoted(response);if(fields.containsKey("opaque"))result+=", opaque="+quoted(fields.get("opaque"));return result;
    }
    public static long deadline(long now,int seconds){if(seconds<=0||seconds>600)throw new IllegalArgumentException("Invalid timeout");return now+seconds*1000L;}
    public static void requireActive(long now,long deadline){if(now>=deadline)throw new IllegalStateException("Pairing expired");}
}
