package local.ambilight.reader;
public final class TvProtocolTest {
    public static void main(String[] args)throws Exception{
        String auth=TvProtocol.digest("Digest realm=\"testrealm@host.com\", qop=\"auth,auth-int\", nonce=\"dcd98b7102dd2f0e8b11d0f600bfb0c093\", opaque=\"5ccc069c403ebaf9f0171e9517f40e41\"","GET","/dir/index.html","Mufasa","Circle Of Life","0a4f113b");
        if(!auth.contains("response=\"6629fae49393a05397450978507c4ef1\""))throw new AssertionError("RFC digest vector");
        if(!auth.contains("qop=auth")||!auth.contains("opaque="))throw new AssertionError("Digest fields");
        String signature=TvProtocol.signature(140049,"3467");if(!signature.equals("Y2NiNDQ4ZDcyYjg5ZmJlNzg1MWNiYjIyYzEwOGNhMTJmNWZmODYwNA=="))throw new AssertionError("JointSpace HMAC vector: "+signature);
        for(String pin:new String[]{"","123","12345","12a4","12\n4"}){boolean fail=false;try{TvProtocol.signature(10,pin);}catch(IllegalArgumentException e){fail=true;}if(!fail)throw new AssertionError("Invalid PIN accepted");}
        long end=TvProtocol.deadline(1000,60);if(end!=61000)throw new AssertionError("Deadline");TvProtocol.requireActive(60999,end);boolean expired=false;try{TvProtocol.requireActive(61000,end);}catch(IllegalStateException e){expired=true;}if(!expired)throw new AssertionError("Expiry boundary");
        boolean unsupported=false;try{TvProtocol.digest("Digest realm=\"x\", nonce=\"x\", algorithm=SHA-256","POST","/6/pair/grant","u","k","c");}catch(IllegalArgumentException e){unsupported=true;}if(!unsupported)throw new AssertionError("Algorithm downgrade");
        System.out.println("PASS: RFC Digest vector; JointSpace HMAC vector; malformed PIN rejection; expiry boundary; unsupported auth algorithm rejection.");
    }
}
