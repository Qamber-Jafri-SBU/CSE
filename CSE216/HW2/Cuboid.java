import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO : a missing interface method must be implemented in this class to make it compile. This must be in terms of volume().
public class Cuboid implements ThreeDShape, Edged {

    private final ThreeDPoint[] vertices = new ThreeDPoint[8];

    /**
     * Creates a cuboid out of the list of vertices. It is expected that the vertices are provided in
     * the order as shown in the figure given in the homework document (from v0 to v7).
     *
     * @param vertices the specified list of vertices in three-dimensional space.
     */
    public Cuboid(List<ThreeDPoint> vertices) {
        if (vertices.size() != 8)
            throw new IllegalArgumentException(String.format("Invalid set of vertices specified for %s",
                                                             this.getClass().getName()));
        int n = 0;
        for (ThreeDPoint p : vertices) this.vertices[n++] = p;
    }

    @Override
    public double volume() {
        // TODO
        double l = ThreeDPoint.distance(vertices[0], vertices[1]), w = ThreeDPoint.distance(vertices[0], vertices[5]),
                h = ThreeDPoint.distance(vertices[0], vertices[3]);
        return Math.abs(l*w*h);
    }

    @Override
    public ThreeDPoint center() {
        // TODO
        double x = 0, y = 0, z = 0;

        for(int i = 0; i < vertices.length; i++){
            x += vertices[i].getX();
            y += vertices[i].getY();
            z += vertices[i].getZ();
        }
        return new ThreeDPoint(x/8, y/8, z/8);
    }

    @Override
    public int numEdges() {
        return 12;
    }

    public double getSurfaceArea(){
        double l = ThreeDPoint.distance(vertices[0], vertices[1]), w = ThreeDPoint.distance(vertices[0], vertices[5]),
                h = ThreeDPoint.distance(vertices[0], vertices[3]);
        return 2*l*w + 2*l*h + 2*w*h;
    }

    @Override
    public int compareTo(ThreeDShape o) {
        if(volume() > o.volume()){
            return 1;
        }if(volume() == o.volume()){
            return 0;
        }
        return -1;
    }

    public static Cuboid random(){
        int maxVal = Integer.MAX_VALUE;
        ThreeDPoint v0 = new ThreeDPoint(Math.random() * maxVal * negate(), Math.random() * maxVal * negate(), Math.random() * maxVal * negate());
        ThreeDPoint v1 = new ThreeDPoint(Math.random() * maxVal * negate(), v0.getY(), v0.getZ());
        ThreeDPoint v3 = new ThreeDPoint(v0.getX(), Math.random() * maxVal * negate(), v0.getZ());
        ThreeDPoint v5 = new ThreeDPoint(v0.getX(), v0.getY(), Math.random() * maxVal * negate());
        ThreeDPoint v2 = new ThreeDPoint(v1.getX(), v3.getY(), v0.getZ());
        ThreeDPoint v4 = new ThreeDPoint(v3.getX(), v3.getY(), v5.getZ());
        ThreeDPoint v6 = new ThreeDPoint(v1.getX(), v0.getY(), v5.getZ());
        ThreeDPoint v7 = new ThreeDPoint(v1.getX(), v3.getY(), v5.getZ());
        List<ThreeDPoint> l = new ArrayList<ThreeDPoint>(Arrays.asList(new ThreeDPoint[] {v0, v1, v2, v3, v4, v5, v6, v7}));

        return new Cuboid(l);

    }

    public static int negate(){
        if(Math.random() > .5){
            return 1;
        }else{
            return -1;
        }
    }
}
