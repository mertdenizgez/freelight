package local.ambilight.reader;

/** Positions from a viewer facing the TV; corner regions weight each edge equally. */
public final class BulbPosition {
    private BulbPosition() {}
    public static final String[] VALUES={"left","upper_left","top","upper_right","right"};
    public static final String[] LABELS={"Sol","Sol üst çapraz","Üst","Sağ üst çapraz","Sağ"};
    public static int index(String source){for(int i=0;i<VALUES.length;i++)if(VALUES[i].equals(source))return i;return -1;}
    public static String normalize(String source){return index(source)<0?"left":source;}
    public static String label(String source){return LABELS[index(normalize(source))];}
    private static int[] average(int[][] colors,int start,int end){
        if(colors==null||start<0||end>colors.length||start>=end)throw new IllegalArgumentException("Eksik Ambilight bölgesi");
        int[] result=new int[3];for(int i=start;i<end;i++)for(int c=0;c<3;c++)result[c]+=colors[i][c];
        for(int c=0;c<3;c++)result[c]/=end-start;return result;
    }
    public static int[] sample(String source,int[][] left,int[][] top,int[][] right){
        int[] a,b;
        switch(source){
            case "left":return average(left,0,left.length);
            case "top":return average(top,0,top.length);
            case "right":return average(right,0,right.length);
            case "upper_left":a=average(left,left.length-(left.length+2)/3,left.length);b=average(top,0,(top.length+2)/3);break;
            case "upper_right":a=average(right,0,(right.length+2)/3);b=average(top,top.length-(top.length+2)/3,top.length);break;
            default:throw new IllegalArgumentException("Geçersiz konum");
        }
        return new int[]{(a[0]+b[0])/2,(a[1]+b[1])/2,(a[2]+b[2])/2};
    }
}
