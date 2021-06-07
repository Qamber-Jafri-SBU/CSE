import java.util.*;

public class Ordering {

    static class XLocationComparator implements Comparator<TwoDShape> {
        @Override public int compare(TwoDShape o1, TwoDShape o2) {
            // TODO
            if(XLocationComparator.getX(o1) == XLocationComparator.getX(o2)){
                return 0;
            }
            if(XLocationComparator.getX(o1) > XLocationComparator.getX(o2)){
                return 1;
            }
            else{
                return -1;
            }

        }

        static double getX(TwoDShape o){
            if(o.numSides() == 0){
                return (((TwoDPoint) ((Circle) o).getPosition().get(0)).getX()) - ((Circle) o).getRadius();
            }else{
                return ((Quadrilateral) o).getPosition().get(2).getX();
            }
        }


    }

    static class AreaComparator implements Comparator<SymmetricTwoDShape> {
        @Override public int compare(SymmetricTwoDShape o1, SymmetricTwoDShape o2) {
            // TODO
            if(o1.area() == o2.area()){
                return 0;
            }
            if(o1.area() > o2.area()){
                return 1;
            }
            else{
                return -1;
            }

        }
    }

    static class SurfaceAreaComparator implements Comparator<ThreeDShape> {
        @Override public int compare(ThreeDShape o1, ThreeDShape o2) {
            // TODO

            if(SurfaceAreaComparator.getSurfaceArea(o1) == SurfaceAreaComparator.getSurfaceArea(o2)){
                return 0;
            }
            if(SurfaceAreaComparator.getSurfaceArea(o1) > SurfaceAreaComparator.getSurfaceArea(o2)){
                return 1;
            }
            else{
                return -1;
            }
        }

        static double getSurfaceArea(ThreeDShape o){
            if(o instanceof Cuboid){
                return ((Cuboid) o).getSurfaceArea();
            }
            if(o instanceof Sphere){
                return ((Sphere) o).getSurfaceArea();
            }
            return 0;
        }

    }

    static <T> void copy(Collection<? extends T> source, Collection<? super T> destination) {
        for(int i = 0; i < source.size(); i++){
            destination.addAll(source);
        }
    }
    // TODO: there's a lot wrong with this method. correct it so that it can work properly with generics.


    public static void main(String[] args) {
        List<TwoDShape>          shapes          = new ArrayList<>();
        List<SymmetricTwoDShape> symmetricshapes = new ArrayList<>();
        List<ThreeDShape>        threedshapes    = new ArrayList<>();

        /*
         * uncomment the following block and fill in the "..." constructors to create actual instances. If your
         * implementations are correct, then the code should compile and yield the expected results of the various
         * shapes being ordered by their smallest x-coordinate, area, volume, surface area, etc. */

        Cuboid c = Cuboid.random();
        Sphere s = Sphere.random();
        ThreeDShape m = Cuboid.random();
        List<TwoDPoint> l = new ArrayList<>(4);
        List<TwoDPoint> r = new ArrayList<>(4);
        l.add(new TwoDPoint(2,2));
        l.add(new TwoDPoint(1,2));
        l.add(new TwoDPoint(1,1));
        l.add(new TwoDPoint(2,1));
        r.add(new TwoDPoint(2,2));
        r.add(new TwoDPoint(1,2));
        r.add(new TwoDPoint(1,1));
        r.add(new TwoDPoint(2,1));
        symmetricshapes.add(new Rectangle(r));
        symmetricshapes.add(new Square(l));
        symmetricshapes.add(new Circle(0, 0, 1));

        copy(symmetricshapes, shapes); // note-1 //
        shapes.add(new Quadrilateral(1,1,2,2,3,3,4,4));

        // sorting 2d shapes according to various criteria
        shapes.sort(new XLocationComparator());
        symmetricshapes.sort(new XLocationComparator());
        symmetricshapes.sort(new AreaComparator());

        // sorting 3d shapes according to various criteria
        Collections.sort(threedshapes);
        threedshapes.sort(new SurfaceAreaComparator());

        /*
         * if your changes to copy() are correct, uncommenting the following block will also work as expected note that
         * copy() should work for the line commented with 'note-1' while at the same time also working with the lines
         * commented with 'note-2' and 'note-3'. */

        List<Number> numbers = new ArrayList<>();
        List<Double> doubles = new ArrayList<>();
        Set<Square>        squares = new HashSet<>();
        Set<Quadrilateral> quads   = new LinkedHashSet<>();

        copy(doubles, numbers); // note-2 //
        copy(squares, quads);   // note-3 //
    }
}
