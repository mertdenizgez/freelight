package local.ambilight.reader;
import java.util.*;import javax.crypto.*;import java.nio.charset.StandardCharsets;
public final class CredentialEnvelopeTest {
    interface Task{void run()throws Exception;}
    static void rejects(Task task)throws Exception{try{task.run();}catch(Exception e){return;}throw new AssertionError("Invalid envelope accepted");}
    public static void main(String[] args)throws Exception{
        KeyGenerator generator=KeyGenerator.getInstance("AES");generator.init(128);javax.crypto.SecretKey key=generator.generateKey(),wrong=generator.generateKey();byte[] plain="synthetic credential payload".getBytes(StandardCharsets.UTF_8);
        byte[] a=CredentialEnvelope.encrypt(key,plain),b=CredentialEnvelope.encrypt(key,plain);if(Arrays.equals(a,b))throw new AssertionError("IV reused");if(!Arrays.equals(plain,CredentialEnvelope.decrypt(key,a)))throw new AssertionError("Round trip");
        byte[] tampered=a.clone();tampered[tampered.length-1]^=1;rejects(()->CredentialEnvelope.decrypt(key,tampered));rejects(()->CredentialEnvelope.decrypt(wrong,a));rejects(()->CredentialEnvelope.decrypt(key,Arrays.copyOf(a,15)));
        byte[] version=a.clone();version[0]=2;rejects(()->CredentialEnvelope.decrypt(key,version));byte[] iv=a.clone();iv[1]=99;rejects(()->CredentialEnvelope.decrypt(key,iv));
        System.out.println("PASS: credential AES-GCM round trip; independent IVs; tamper, wrong-key, truncation, version and IV-length rejection.");
    }
}
