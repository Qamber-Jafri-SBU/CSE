import java.util.List;

public class Rectangle extends Quadrilateral implements SymmetricTwoDShape {

    private final TwoDPoint[] vertices = new TwoDPoint[4];

    public Rectangle(List<TwoDPoint> vertices){
        super(vertices);
        if (vertices.size() != 4)
            throw new IllegalArgumentException(String.format("Invalid set of vertices specified for %s",
                    this.getClass().getName()));
        int n = 0;
        for (Point p : vertices) this.vertices[n++] = (TwoDPoint) p;
    }

    /**
     * The center of a rectangle is calculated to be the point of intersection of its diagonals.
     *
     * @return the center of this rectangle.
     */
    @Override
    public Point center() {
        // TODO
        double x = 0, y = 0;
        for(TwoDPoint pt : vertices){
            x += pt.getX();
            y += pt.getY();
        }

        return new TwoDPoint(x/2,y/2);
    }

    @Override
    public boolean isMember(List<? extends Point> vertices) {
         // TODO
        return TwoDPoint.distance((TwoDPoint) vertices.get(0), (TwoDPoint) vertices.get(2)) ==
                TwoDPoint.distance((TwoDPoint) vertices.get(1), (TwoDPoint) vertices.get(3));
    }

    @Override
    public double area() {
        // TODO
        return TwoDPoint.distance(vertices[0], vertices[1]) * TwoDPoint.distance(vertices[0], vertices[3]);
    }

    public TwoDPoint[] getVertices(){
        return vertices;
    }
}
