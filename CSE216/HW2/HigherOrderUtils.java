import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class HigherOrderUtils {

    interface NamedBiFunction<T, U, R> extends BiFunction<T, U, R> {
        String name();
    }

    public static NamedBiFunction add = new  NamedBiFunction<Double, Double, Double>(){

        @Override
        public Double apply(Double aDouble, Double aDouble2) {
            return aDouble + aDouble2;
        }

        @Override
        public String name() {
            return "add";
        }
    };

    public static NamedBiFunction subtract = new NamedBiFunction<Double, Double, Double>(){

        @Override
        public Double apply(Double aDouble, Double aDouble2) {
            return aDouble - aDouble2;
        }

        @Override
        public String name() {
            return "subtract";
        }
    };

    public static NamedBiFunction multiply = new NamedBiFunction<Double, Double, Double>(){

        @Override
        public Double apply(Double aDouble, Double aDouble2) {
            return aDouble*aDouble2;
        }

        @Override
        public String name() {
            return "multiply";
        }
    };

    public static NamedBiFunction divide = new NamedBiFunction<Double, Double, Double>() {

        @Override
        public Double apply(Double aDouble, Double aDouble2) throws ArithmeticException{
            if(aDouble2 == 0){
                throw new ArithmeticException();
            }
            return aDouble/aDouble2;
        }

        @Override
        public String name() {
            return "divide";
        }
    };

    public static <T> T zip(List<T> args, List<NamedBiFunction<T, T, T>> bifunctions){
        if(args.size() != bifunctions.size() + 1){
            return null;
        }

        int i = 1;
        ListIterator fn = bifunctions.listIterator();
        ListIterator element = args.listIterator();
        T x = null;

        while(element.hasNext()){

            x = (T)((NamedBiFunction)fn.next()).apply(element.next(), element.next());

            element.previous();
            args.set(i++, x);
            if(i == args.size()){
                break;
            }

        }

        return args.get(args.size() - 1);
    }

    static class FunctionComposition<T, U, V>{
        BiFunction composition = new BiFunction<Function<T,U>, Function<U,V>, Function<T,V>>() {


            @Override
            public Function<T,V> apply(Function<T,U> function, Function<U,V> function2) {
                return function.andThen(function2);
            }
        };
    }
}
