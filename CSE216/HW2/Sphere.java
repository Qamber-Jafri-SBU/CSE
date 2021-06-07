public class Sphere implements ThreeDShape, Edged {

    private ThreeDPoint center;
    private double radius;

    public Sphere(double centerx, double centery, double centerz, double radius){
        center = new ThreeDPoint(centerx, centery, centerz);
        this.radius = radius;
    }

    @Override
    public Point center() {
        return center;
    }

    @Override
    public double volume() {
        return (4/3) * Math.PI * Math.pow(radius, 3);
    }


    @Override
    public int numEdges() {
        return 0;
    }

    public double getSurfaceArea(){
        return 4 * Math.PI * Math.pow(radius, 2);
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

    public static Sphere random(){
        int maxVal = Integer.MAX_VALUE;
        double centerx = Math.random() * maxVal * negate(),centery = Math.random() * maxVal * negate(),
                centerz =Math.random() * maxVal * negate();
        double radius = Math.random() * maxVal;
        return new Sphere(centerx, centery, centerz, radius);
    }

    public static int negate(){
        if(Math.random() > .5){
            return 1;
        }else{
            return -1;
        }
    }
}
