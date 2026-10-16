package local.ambilight.reader;
import java.io.*;import java.net.*;import java.nio.charset.StandardCharsets;import java.security.*;import java.util.*;import java.util.concurrent.atomic.AtomicReference;import javax.net.ssl.*;import org.json.JSONObject;
/** Real TLS/HTTP tests. The JSON test double carries raw bodies; JSON parsing is not tested here. */
public final class TransportTest {
 static SSLServerSocket listener;static String fingerprint;static AtomicReference<Throwable> failure=new AtomicReference<>();
 static class Request {String first;Map<String,String> headers=new HashMap<>();InputStream in;}
 static String line(InputStream in)throws Exception {ByteArrayOutputStream b=new ByteArrayOutputStream();int c;while((c=in.read())!=-1){if(c==10)return b.toString("UTF-8");if(c!=13)b.write(c);}return b.size()==0?null:b.toString("UTF-8");}
 static SSLSocket accept()throws Exception {SSLSocket socket=(SSLSocket)listener.accept();socket.setSoTimeout(4000);return socket;}
 static Request headers(SSLSocket socket)throws Exception {Request r=new Request();r.in=socket.getInputStream();r.first=line(r.in);String row;while((row=line(r.in))!=null&&!row.isEmpty()){int colon=row.indexOf(':');r.headers.put(row.substring(0,colon).toLowerCase(Locale.ROOT),row.substring(colon+1).trim());}return r;}
 static byte[] body(Request r)throws Exception {int length=Integer.parseInt(r.headers.getOrDefault("content-length","0"));byte[] out=new byte[length];new DataInputStream(r.in).readFully(out);return out;}
 static void reply(SSLSocket socket,String status,String extra,String body)throws Exception {byte[] bytes=body.getBytes(StandardCharsets.UTF_8);OutputStream out=socket.getOutputStream();out.write(("HTTP/1.1 "+status+"\r\nContent-Length: "+bytes.length+"\r\n"+extra+"\r\n").getBytes(StandardCharsets.UTF_8));out.write(bytes);out.flush();}
 interface Work {void run()throws Exception;}
 static Thread serve(Work work){failure.set(null);Thread t=new Thread(()->{try{work.run();}catch(Throwable e){failure.set(e);}});t.setDaemon(true);t.start();return t;}
 static void done(Thread t)throws Exception {t.join(5000);if(t.isAlive())throw new AssertionError("Server hung");if(failure.get()!=null)throw new AssertionError("Server failure",failure.get());}
 static TvCredentials credentials=new TvCredentials("test-user","test-key","");
 public static void main(String[] args)throws Exception{
  KeyStore keys=KeyStore.getInstance("PKCS12");try(InputStream in=new FileInputStream(args[0])){keys.load(in,"test-only-password".toCharArray());}KeyManagerFactory km=KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());km.init(keys,"test-only-password".toCharArray());SSLContext context=SSLContext.getInstance("TLS");context.init(km.getKeyManagers(),null,new SecureRandom());fingerprint=TvProtocol.hex(MessageDigest.getInstance("SHA-256").digest(keys.getCertificate("test").getEncoded()));listener=(SSLServerSocket)context.getServerSocketFactory().createServerSocket();listener.setReuseAddress(true);listener.bind(new InetSocketAddress("127.0.0.1",1926));listener.setSoTimeout(5000);
  String challenge="WWW-Authenticate: Digest realm=\"test\", nonce=\"nonce\", qop=\"auth\"\r\nConnection: keep-alive\r\n";
  try{
   Thread get=serve(()->{try(SSLSocket socket=accept()){Request first=headers(socket);if(first.headers.containsKey("authorization"))throw new AssertionError();reply(socket,"401 Unauthorized",challenge,"");Request retry=headers(socket);if(!retry.first.startsWith("GET ")||!retry.headers.containsKey("authorization"))throw new AssertionError("GET retry");reply(socket,"200 OK","Connection: keep-alive\r\n","{\"ok\":true}");}});
   try(LocalTvApi api=new LocalTvApi(fingerprint)){if(!api.request("GET","/6/ambilight/measured",null,credentials).toString().equals("{\"ok\":true}"))throw new AssertionError();}done(get);
   Thread post=serve(()->{try(SSLSocket first=accept()){Request request=headers(first);if(!request.first.startsWith("POST "))throw new AssertionError();body(request);reply(first,"401 Unauthorized",challenge,"");if(line(request.in)!=null)throw new AssertionError("POST challenge connection was reused");}try(SSLSocket second=accept()){Request retry=headers(second);if(!retry.headers.containsKey("authorization"))throw new AssertionError("No POST authorization");if(!new String(body(retry),StandardCharsets.UTF_8).equals("{\"pin\":\"0000\"}"))throw new AssertionError("POST body changed");reply(second,"200 OK","Connection: close\r\n","{\"ok\":true}");}});
   try(LocalTvApi api=new LocalTvApi(fingerprint)){api.request("POST","/6/pair/grant",new JSONObject("{\"pin\":\"0000\"}"),credentials);}done(post);
   Thread huge=serve(()->{try(SSLSocket socket=accept()){headers(socket);socket.getOutputStream().write("HTTP/1.1 200 OK\r\nContent-Length: 70000\r\n\r\n".getBytes(StandardCharsets.US_ASCII));socket.getOutputStream().flush();}});
   boolean rejected=false;try(LocalTvApi api=new LocalTvApi(fingerprint)){api.request("GET","/6/ambilight/measured",null,null);}catch(IOException e){rejected=true;}if(!rejected)throw new AssertionError("Oversize body accepted");done(huge);
   Thread wrong=serve(()->{try(SSLSocket socket=accept()){try{socket.startHandshake();}catch(IOException expected){}}});
   rejected=false;try(LocalTvApi api=new LocalTvApi("wrong-certificate")){api.request("GET","/6/ambilight/measured",null,null);}catch(SSLException e){rejected=true;}if(!rejected)throw new AssertionError("Certificate mismatch accepted");done(wrong);
   rejected=false;try(LocalTvApi api=new LocalTvApi(fingerprint)){api.request("GET","/6/settings",null,null);}catch(IllegalArgumentException e){rejected=true;}if(!rejected)throw new AssertionError("Unsupported endpoint accepted");
  }finally{listener.close();}
  System.out.println("PASS: real local TLS/HTTP; GET Digest keep-alive; POST Digest fresh-connection retry/body preservation; oversized reply rejection; certificate-pin mismatch; endpoint allowlist.");
 }
}
