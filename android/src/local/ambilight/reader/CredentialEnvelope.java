package local.ambilight.reader;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import java.io.IOException;

/** Versioned authenticated envelope; the Android caller supplies a device-bound Keystore key. */
public final class CredentialEnvelope {
    public static byte[] encrypt(Key key,byte[] plaintext)throws Exception{
        Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");cipher.init(Cipher.ENCRYPT_MODE,key);byte[] iv=cipher.getIV(),encrypted=cipher.doFinal(plaintext);
        if(iv.length!=12)throw new IOException("Unexpected IV length");byte[] output=new byte[2+iv.length+encrypted.length];output[0]=1;output[1]=(byte)iv.length;System.arraycopy(iv,0,output,2,iv.length);System.arraycopy(encrypted,0,output,2+iv.length,encrypted.length);return output;
    }
    public static byte[] decrypt(Key key,byte[] bytes)throws Exception{
        if(bytes.length<30||bytes[0]!=1||bytes[1]!=12)throw new IOException("Invalid credential envelope");int length=bytes[1]&255;
        Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");cipher.init(Cipher.DECRYPT_MODE,key,new GCMParameterSpec(128,bytes,2,length));return cipher.doFinal(bytes,2+length,bytes.length-2-length);
    }
}
