package local.ambilight.reader;

import android.content.Context;
import android.security.keystore.*;
import android.util.AtomicFile;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import org.json.JSONObject;

/** Device-bound encrypted credentials. Atomic replacement never destroys a working pair on failure. */
public final class TvCredentials {
    public final String user,key,fingerprint;
    private static TvCredentials cached;private static boolean loaded;
    public TvCredentials(String user,String key,String pin){if(user==null||user.isEmpty()||key==null||key.isEmpty())throw new IllegalArgumentException("Missing credentials");this.user=user;this.key=key;fingerprint=pin==null?"":pin;}
    private static AtomicFile file(Context c){return new AtomicFile(new File(c.getNoBackupFilesDir(),"tv-credentials.enc"));}
    private static java.security.Key key()throws Exception{
        KeyStore store=KeyStore.getInstance("AndroidKeyStore");store.load(null);if(store.containsAlias("freelight.tv"))return store.getKey("freelight.tv",null);
        KeyGenerator generator=KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");generator.init(new KeyGenParameterSpec.Builder("freelight.tv",KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build());return generator.generateKey();
    }
    public static synchronized TvCredentials load(Context c)throws Exception{
        if(loaded)return cached;
        AtomicFile f=file(c);
        if(f.getBaseFile().exists()||new File(f.getBaseFile().getPath()+".bak").exists()){
            JSONObject stored=new JSONObject(new String(CredentialEnvelope.decrypt(key(),f.readFully()),StandardCharsets.UTF_8));cached=new TvCredentials(stored.getString("user"),stored.getString("key"),stored.optString("certificate"));loaded=true;return cached;
        }
        File legacy=new File(c.getFilesDir(),"pairing.json");if(legacy.exists()){
            try(FileInputStream in=new FileInputStream(legacy)){ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[1024];int n;while((n=in.read(b))!=-1){out.write(b,0,n);if(out.size()>65536)throw new IOException("Invalid legacy storage");}JSONObject s=new JSONObject(new String(out.toByteArray(),StandardCharsets.UTF_8));save(c,new TvCredentials(s.getJSONObject("device").getString("id"),s.getJSONObject("response").getString("auth_key"),""));}
            legacy.delete();return cached;
        }
        loaded=true;return null;
    }
    public static synchronized void save(Context c,TvCredentials credentials)throws Exception{
        byte[] json=new JSONObject().put("user",credentials.user).put("key",credentials.key).put("certificate",credentials.fingerprint).toString().getBytes(StandardCharsets.UTF_8);
        byte[] encrypted=CredentialEnvelope.encrypt(key(),json);AtomicFile f=file(c);FileOutputStream out=null;
        try{out=f.startWrite();out.write(encrypted);f.finishWrite(out);}catch(Exception e){if(out!=null)f.failWrite(out);throw e;}cached=credentials;loaded=true;
    }
    public static synchronized void forget(Context c){file(c).delete();new File(c.getFilesDir(),"pairing.json").delete();cached=null;loaded=true;}
}
