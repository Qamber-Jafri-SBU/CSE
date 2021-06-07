import java.util.ArrayList;
import java.util.List;

/**
 * An unmodifiable point in the standard two-dimensional Euclidean space. The coordinates of such a point is given by
 * exactly two doubles specifying its <code>x</code> and <code>y</code> values.
 */
public class TwoDPoint implements Point {

    private double x, y;

    public TwoDPoint(double x, double y) {
        // TODO
        this.x = x;
        this.y = y;
    }

    /**
     * @return the coordinates of this point as a <code>double[]</code>.
     */
    @Override
    public double[] coordinates() {
        // TODO
        return new double[] {x, y};
    }

    /**
     * Returns a list of <code>TwoDPoint</code>s based on the specified array of doubles. A valid argument must always
     * be an even number of doubles so that every pair can be used to form a single <code>TwoDPoint</code> to be added
     * to the returned list of points.
     *
     * @param coordinates the specified array of doubles.
     * @return a list of two-dimensional point objects.
     * @throws IllegalArgumentException if the input array has an odd number of doubles.
     */
    public static List<TwoDPoint> ofDoubles(double... coordinates) throws IllegalArgumentException {
        // TODO
        if(coordinates.length % 2 != 0){
            throw new IllegalArgumentException();
        }

        List<TwoDPoint> pairs = new ArrayList<>(coordinates.length/2);
        for(int i = 0; i < coordinates.length - 1; i = i + 2){
            pairs.add(new TwoDPoint(coordinates[i], coordinates[i + 1]));
        }
        return pairs;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public static double distance(TwoDPoint p1, TwoDPoint p2){
        double distanceSquared = Math.pow((p2.getX() - p1.getX()), 2) + Math.pow((p2.getY() - p1.getY()), 2);
        double distance = Math.sqrt(distanceSquared);
        return Math.floor(distance * 100000) / 100000;
    }
}
