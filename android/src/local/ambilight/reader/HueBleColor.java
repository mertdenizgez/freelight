package local.ambilight.reader;
/** sRGB to CIE xy, clipped to Hue gamut C. No Android dependencies. */
final class HueBleColor {
    static int brightness(int r,int g,int b,int multiplier){return Math.max(0,Math.min(254,Math.max(r,Math.max(g,b))*Math.max(1,Math.min(2,multiplier))));}
    static int scaledBrightness(int r,int g,int b,int percent){return Math.max(0,Math.min(254,Math.max(r,Math.max(g,b))*Math.max(0,Math.min(400,percent))/100));}
    private static double linear(int v){double n=v/255.0;return n>0.04045?Math.pow((n+0.055)/1.055,2.4):n/12.92;}
    static byte[] xy(int r,int g,int b){
        double R=linear(r),G=linear(g),B=linear(b);
        double X=R*0.664511+G*0.154324+B*0.162028,Y=R*0.283881+G*0.668433+B*0.047685,Z=R*0.000088+G*0.072310+B*0.986039;
        double sum=X+Y+Z;double[] p=sum==0?new double[]{0.3127,0.3290}:new double[]{X/sum,Y/sum};
        double[] a={0.6915,0.3083},c={0.17,0.7},d={0.1532,0.0475};
        double denom=(c[1]-d[1])*(a[0]-d[0])+(d[0]-c[0])*(a[1]-d[1]);
        double u=((c[1]-d[1])*(p[0]-d[0])+(d[0]-c[0])*(p[1]-d[1]))/denom;
        double v=((d[1]-a[1])*(p[0]-d[0])+(a[0]-d[0])*(p[1]-d[1]))/denom;
        if(u<0||v<0||u+v>1){double[] best=null;double min=Double.MAX_VALUE;double[][] points={a,c,d,a};for(int i=0;i<3;i++){double[] q=closest(p,points[i],points[i+1]);double distance=Math.pow(q[0]-p[0],2)+Math.pow(q[1]-p[1],2);if(distance<min){min=distance;best=q;}}p=best;}
        int x=(int)Math.round(p[0]*65535),y=(int)Math.round(p[1]*65535);return new byte[]{(byte)x,(byte)(x>>8),(byte)y,(byte)(y>>8)};
    }
    private static double[] closest(double[] p,double[] a,double[] b){double x=b[0]-a[0],y=b[1]-a[1],t=Math.max(0,Math.min(1,((p[0]-a[0])*x+(p[1]-a[1])*y)/(x*x+y*y)));return new double[]{a[0]+t*x,a[1]+t*y};}
}
