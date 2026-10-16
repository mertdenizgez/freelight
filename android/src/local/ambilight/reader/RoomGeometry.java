package local.ambilight.reader;
/** Physical room coordinates are never mirrored for RTL languages. */
public final class RoomGeometry {
    public final float scale,imageOffset,left,top,right,bottom;
    private final float width,height,density;
    public RoomGeometry(float width,float height,float density,int imageWidth,int imageHeight){
        this.width=width;this.height=height;this.density=density;scale=width/imageWidth;
        imageOffset=Math.max(0,112*density-imageHeight*.217f*scale);
        left=width*.321f;right=width*.679f;top=imageOffset+imageHeight*.217f*scale;bottom=imageOffset+imageHeight*.562f*scale;
    }
    public float[] anchor(String source,int slot,int count){
        int region=BulbPosition.index(source);if(region<0||count<1||slot<0||slot>=count)throw new IllegalArgumentException("Invalid bulb position");float x,y;
        if(region==0||region==4){x=region==0?left-width*.10f:right+width*.10f;y=(top+bottom)/2+(slot-(count-1)/2f)*Math.max(84*density,(bottom-top)/(count+1));y=Math.max(36*density,Math.min(height-64*density,y));}
        else{x=(region==1?left-width*.025f:region==3?right+width*.025f:(left+right)/2)+(slot-(count-1)/2f)*140*density;y=top-72*density;}
        return new float[]{Math.max(40*density,Math.min(width-40*density,x)),y};
    }
}
