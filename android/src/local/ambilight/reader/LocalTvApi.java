package local.ambilight.reader;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.*;
import javax.net.ssl.*;
import org.json.JSONObject;

/** HTTPS restricted to this Android device's loopback/local interfaces; no remote host input. */
public class LocalTvApi implements AutoCloseable {
    private volatile SSLSocket socket;private InputStream input;private String host,certificate="";private int attempt;private volatile boolean closed;
    private final String expectedCertificate;private String preferred="";
    public static final class Failure extends IOException {public final int code;Failure(int code){super("Local TV HTTP "+code);this.code=code;}}
    public LocalTvApi(String fingerprint){expectedCertificate=fingerprint==null?"":fingerprint;}
    public LocalTvApi(String fingerprint,String preferred){this(fingerprint);this.preferred=preferred;}
    public static List<String> localAddresses()throws Exception{
        LinkedHashSet<String> addresses=new LinkedHashSet<>();addresses.add("127.0.0.1");Enumeration<NetworkInterface> networks=NetworkInterface.getNetworkInterfaces();
        while(networks!=null&&networks.hasMoreElements()){NetworkInterface n=networks.nextElement();if(!n.isUp()||n.isLoopback())continue;Enumeration<InetAddress> values=n.getInetAddresses();while(values.hasMoreElements()){InetAddress a=values.nextElement();if(a instanceof Inet4Address&&a.isSiteLocalAddress())addresses.add(a.getHostAddress());}}
        return new ArrayList<>(addresses);
    }
    public String certificate(){return certificate;}
    public String host(){return host==null?"":host;}
    private void connect()throws Exception{
        if(closed)throw new InterruptedIOException();if(socket!=null)return;List<String> addresses=localAddresses();if(addresses.remove(preferred))addresses.add(0,preferred);host=addresses.get(attempt%addresses.size());
        SSLContext tls=SSLContext.getInstance("TLS");tls.init(null,new TrustManager[]{new X509TrustManager(){public X509Certificate[] getAcceptedIssuers(){return new X509Certificate[0];}public void checkClientTrusted(X509Certificate[] chain,String a)throws java.security.cert.CertificateException{throw new java.security.cert.CertificateException();}public void checkServerTrusted(X509Certificate[] chain,String a)throws java.security.cert.CertificateException{try{String actual=TvProtocol.hex(MessageDigest.getInstance("SHA-256").digest(chain[0].getEncoded()));if(!expectedCertificate.isEmpty()&&!expectedCertificate.equals(actual))throw new java.security.cert.CertificateException("TV certificate changed");certificate=actual;}catch(GeneralSecurityException e){throw new java.security.cert.CertificateException(e);}}}},new SecureRandom());
        SSLSocket s=(SSLSocket)tls.getSocketFactory().createSocket();socket=s;s.connect(new InetSocketAddress(host,1926),2000);s.setSoTimeout(3000);s.startHandshake();if(closed){s.close();throw new InterruptedIOException();}input=s.getInputStream();
    }
    private void reset(){SSLSocket old=socket;socket=null;try{if(old!=null)old.close();}catch(Exception ignored){}input=null;}
    public void close(){closed=true;reset();}
    private String line()throws Exception{ByteArrayOutputStream out=new ByteArrayOutputStream();int b;while((b=input.read())!=-1){if(b==10)return out.toString("UTF-8");if(b!=13)out.write(b);if(out.size()>8192)throw new IOException("HTTP line too long");}throw new EOFException();}
    private void copy(ByteArrayOutputStream out,int size)throws Exception{if(size<0||size>65536-out.size())throw new IOException("Response too large");byte[] buffer=new byte[4096];while(size>0){int n=input.read(buffer,0,Math.min(size,buffer.length));if(n<0)throw new EOFException();out.write(buffer,0,n);size-=n;}}
    private static final class Response{int code;Map<String,String> headers=new HashMap<>();byte[] body;}
    private Response exchange(String method,String path,byte[] body,String authorization)throws Exception{
        connect();Response response=new Response();StringBuilder headers=new StringBuilder(method+" "+path+" HTTP/1.1\r\nHost: "+host+":1926\r\nConnection: keep-alive\r\n");
        if(authorization!=null)headers.append("Authorization: ").append(authorization).append("\r\n");if(body!=null)headers.append("Content-Type: application/json\r\nContent-Length: ").append(body.length).append("\r\n");headers.append("\r\n");OutputStream out=socket.getOutputStream();out.write(headers.toString().getBytes(StandardCharsets.UTF_8));if(body!=null)out.write(body);out.flush();
        String[] status=line().split(" ");if(status.length<2)throw new IOException("Invalid HTTP response");response.code=Integer.parseInt(status[1]);String l;int headerBytes=0;while(!(l=line()).isEmpty()){headerBytes+=l.length();if(headerBytes>16384)throw new IOException("Headers too large");int colon=l.indexOf(':');if(colon>0)response.headers.put(l.substring(0,colon).toLowerCase(Locale.ROOT),l.substring(colon+1).trim());}
        ByteArrayOutputStream data=new ByteArrayOutputStream();if("chunked".equalsIgnoreCase(response.headers.get("transfer-encoding"))){while(true){int size=Integer.parseInt(line().split(";",2)[0].trim(),16);if(size==0){while(!line().isEmpty()){}break;}copy(data,size);if(!line().isEmpty())throw new IOException("Invalid chunk");}}
        else if(response.headers.containsKey("content-length"))copy(data,Integer.parseInt(response.headers.get("content-length")));else throw new IOException("Missing HTTP length");
        response.body=data.toByteArray();if("close".equalsIgnoreCase(response.headers.get("connection")))reset();return response;
    }
    public JSONObject request(String method,String path,JSONObject json,TvCredentials credentials)throws Exception{
        if(!path.equals("/6/pair/request")&&!path.equals("/6/pair/grant")&&!path.equals("/6/ambilight/measured"))throw new IllegalArgumentException("Unsupported TV endpoint");
        byte[] body=json==null?null:json.toString().getBytes(StandardCharsets.UTF_8);
        try{Response r=exchange(method,path,body,null);if(r.code==401&&credentials!=null){String auth=TvProtocol.digest(r.headers.get("www-authenticate"),method,path,credentials.user,credentials.key,UUID.randomUUID().toString().replace("-",""));r=exchange(method,path,body,auth);}if(r.code!=200)throw new Failure(r.code);return new JSONObject(new String(r.body,StandardCharsets.UTF_8));}
        catch(Exception e){reset();attempt++;throw e;}
    }
}
