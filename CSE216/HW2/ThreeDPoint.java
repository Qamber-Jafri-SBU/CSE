/**
 * An unmodifiable point in the three-dimensional space. The coordinates are specified by exactly three doubles (its
 * <code>x</code>, <code>y</code>, and <code>z</code> values).
 */
public class ThreeDPoint implements Point {

    private double x, y, z;

    public static void main(String[] args) {
        ThreeDPoint p1 = new ThreeDPoint(-1,-1,-1);
        ThreeDPoint p2 = new ThreeDPoint(1,1,1);
        System.out.println(ThreeDPoint.distance(p1, p2));
    }

    public ThreeDPoint(double x, double y, double z) {
        //TODO
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * @return the (x,y,z) coordinates of this point as a <code>double[]</code>.
     */
    @Override
    public double[] coordinates() {
        //TODO
        return new double[] {x, y, z};
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public double getZ(){
        return z;
    }

    public static double distance(ThreeDPoint p1, ThreeDPoint p2){
        double distanceSquared = Math.pow((p2.getX() - p1.getX()), 2) + Math.pow((p2.getY() - p1.getY()), 2) + Math.pow((p2.getZ() - p1.getZ()), 2);
        double distance = Math.sqrt(distanceSquared);
        return distance;
        //return Math.floor(distance * 100000) / 100000;
    }
}
