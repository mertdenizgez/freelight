package local.ambilight.reader;

import android.content.Context;
import android.os.SystemClock;
import org.json.JSONObject;
import java.util.UUID;
import java.io.InterruptedIOException;

/** A PIN session lives only in RAM; a failed/cancelled attempt never replaces stored credentials. */
public final class TvPairing implements AutoCloseable {
    public static volatile String diagnostic="idle";
    public static void noteFailure(Exception error){String stage=diagnostic.split("/",2)[0];diagnostic=stage+"/"+(error instanceof LocalTvApi.Failure?"HTTP_"+((LocalTvApi.Failure)error).code:error.getClass().getSimpleName());}

    private LocalTvApi api;
    private JSONObject device;
    private TvCredentials pending;
    private long deadline,timestamp;
    private volatile boolean cancelled;

    public void begin()throws Exception{
        diagnostic="request";
        final LocalTvApi connection;
        final JSONObject identity=new JSONObject().put("id",UUID.randomUUID().toString().replace("-","")).put("device_name","Freelight TV").put("device_os","Android").put("app_id","local.ambilight.reader").put("app_name","Freelight").put("type","native");
        synchronized(this){if(cancelled)throw new InterruptedIOException();connection=new LocalTvApi("");api=connection;}
        JSONObject request=new JSONObject().put("scope",new org.json.JSONArray().put("read").put("write").put("control")).put("device",identity);
        JSONObject response=null;Exception last=null;int count=LocalTvApi.localAddresses().size();
        for(int i=0;i<count;i++){
            if(cancelled)throw new InterruptedIOException();
            try{response=connection.request("POST","/6/pair/request",request,null);break;}
            catch(LocalTvApi.Failure e){throw e;}catch(Exception e){last=e;}
        }
        if(response==null)throw last==null?new java.io.IOException("No local TV API"):last;
        if(!"SUCCESS".equals(response.optString("error_id")))throw new java.io.IOException("Pairing unavailable");
        TvCredentials candidate=new TvCredentials(identity.getString("id"),response.getString("auth_key"),connection.certificate());
        long end=TvProtocol.deadline(SystemClock.elapsedRealtime(),response.getInt("timeout"));
        synchronized(this){if(cancelled)throw new InterruptedIOException();pending=candidate;device=identity;timestamp=response.getLong("timestamp");deadline=end;diagnostic="PIN_ready";}
    }

    public void finish(Context context,String pin)throws Exception{
        final LocalTvApi connection;
        final TvCredentials candidate;
        final JSONObject identity;
        final long issued;
        synchronized(this){
            if(cancelled)throw new InterruptedIOException();
            if(pending==null)throw new IllegalStateException("No session");
            TvProtocol.requireActive(SystemClock.elapsedRealtime(),deadline);
            candidate=pending;identity=device;issued=timestamp;
            String host=api.host();api.close();connection=new LocalTvApi(candidate.fingerprint,host);api=connection;
        }
        JSONObject auth=new JSONObject().put("auth_AppId","1").put("pin",pin).put("auth_timestamp",issued).put("auth_signature",TvProtocol.signature(issued,pin));
        diagnostic="grant";
        JSONObject response=connection.request("POST","/6/pair/grant",new JSONObject().put("auth",auth).put("device",identity),candidate);
        if(cancelled)throw new InterruptedIOException();
        if(!"SUCCESS".equals(response.optString("error_id"))){String code=response.optString("error_id");diagnostic="grant_"+(code.matches("[A-Z_]{1,40}")?code:"rejected");if(code.equals("TIMEOUT")){synchronized(this){deadline=SystemClock.elapsedRealtime();}}throw new IllegalArgumentException("PIN rejected");}
        diagnostic="frame_check";
        TvFrame.validate(connection.request("GET","/6/ambilight/measured",null,candidate));
        synchronized(this){if(cancelled)throw new InterruptedIOException();TvCredentials.save(context,candidate);diagnostic="paired";}
        close();
    }

    public synchronized long remainingMillis(){return Math.max(0,deadline-SystemClock.elapsedRealtime());}
    public synchronized void close(){cancelled=true;if(api!=null)api.close();api=null;pending=null;device=null;deadline=0;}
}
