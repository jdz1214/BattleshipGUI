package commoncore;

/**
 * Created by jdz on 8/29/16.
 */
public class Spot {
    private int x;
    private int y;
    private String z;
    //Constructors
    public Spot (int x, int y, String z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    //Methods
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public String getZ() { return z; }
}