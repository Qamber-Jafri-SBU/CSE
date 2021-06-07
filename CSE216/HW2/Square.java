import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class Square extends Rectangle implements Snappable {

    // TODO: this constructor must NOT be changed. Instead, resolve the error by adding code elsewhere.
    public Square(List<TwoDPoint> vertices) {
        super(vertices);
    }

    /**
     * Given a list of vertices assumed to be provided in a counterclockwise order in a two-dimensional plane, checks
     * whether or not they constitute a valid square.
     *
     * @param vertices the specified list of vertices in a counterclockwise order
     * @return <code>true</code> if the four vertices can form a square, <code>false</code> otherwise.aw
     *
     */
    @Override
    public boolean isMember(List<? extends Point> vertices) {
        return vertices.size() == 4 &&
                DoubleStream.of(getSideLengths()).boxed().collect(Collectors.toSet()).size() == 1;
    }

    /**
     * Snaps the sides of the square such that each corner (x,y) is modified to be a corner (x',y') where x' is the
     * the integer value closest to x and y' is the integer value closest to y. This, of course, may change the shape
     * to a general quadrilateral, hence the return type. The only exception is when the square is positioned in a way
     * where this approximation will lead it to vanish into a single point. In that case, a call to {@link #snap()}
     * will not modify this square in any way.
     */
    @Override
    public Quadrilateral snap() {
        // TODO
        double[] values = new double[8];
        int j = 0;
        for(int i = 0; i < values.length; i = i + 2){
            values[i] = Math.round(getVertices()[j].getX());
            values[i+1] = Math.round(getVertices()[j++].getY());
        }

        List<TwoDPoint> newVertices = TwoDPoint.ofDoubles(values);
        for(int i = 0; i < newVertices.size(); i++){
            for(j = 1 + i; j < newVertices.size(); j++){
                if(newVertices.get(i).getX() == newVertices.get(j).getX() && newVertices.get(i).getY() == newVertices.get(j).getY()){
                    return this;
                }
            }
        }
        return new Quadrilateral(newVertices);
    }
}
