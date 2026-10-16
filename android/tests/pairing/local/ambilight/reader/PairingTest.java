package local.ambilight.reader;
import android.content.Context;import android.os.SystemClock;
public final class PairingTest {
 static final Context context=new Context();static TvCredentials old;
 static void reset(){SystemClock.now=1000;LocalTvApi.reject=false;LocalTvApi.unavailable=false;LocalTvApi.badFrame=false;LocalTvApi.duringGrant=null;LocalTvApi.grants=0;TvCredentials.saves=0;old=new TvCredentials("old-id","old-key","old-pin");TvCredentials.stored=old;}
 interface Task {void run()throws Exception;}
 static void fails(Task task)throws Exception{boolean failed=false;try{task.run();}catch(Exception e){failed=true;}if(!failed||TvCredentials.stored!=old||TvCredentials.saves!=0)throw new AssertionError("Failed pairing replaced credentials");}
 public static void main(String[] args)throws Exception{
  reset();TvPairing good=new TvPairing();good.begin();if(TvCredentials.stored!=old)throw new AssertionError();good.finish(context,"3467");if(TvCredentials.saves!=1||TvCredentials.stored==old||!TvCredentials.stored.fingerprint.equals("test-certificate"))throw new AssertionError("Successful pair not committed");
  reset();TvPairing rejected=new TvPairing();rejected.begin();LocalTvApi.reject=true;fails(()->rejected.finish(context,"3467"));
  reset();TvPairing expired=new TvPairing();expired.begin();SystemClock.now=61000;fails(()->expired.finish(context,"3467"));if(LocalTvApi.grants!=0)throw new AssertionError("Expired PIN reached TV");
  reset();TvPairing cancelled=new TvPairing();cancelled.begin();cancelled.close();fails(()->cancelled.finish(context,"3467"));
  reset();TvPairing unavailable=new TvPairing();LocalTvApi.unavailable=true;fails(unavailable::begin);
  reset();TvPairing malformed=new TvPairing();malformed.begin();LocalTvApi.badFrame=true;fails(()->malformed.finish(context,"3467"));
  reset();TvPairing interrupted=new TvPairing();interrupted.begin();LocalTvApi.duringGrant=interrupted::close;fails(()->interrupted.finish(context,"3467"));
  System.out.println("PASS: pairing commits only after grant + RGB validation; preserves old pair on rejection, expiry, cancellation, no API, malformed RGB and cancellation during grant; session certificate pinned.");
 }
}
