import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class StreamUtils {

    public static Collection<String> capitalized(Collection<String> strings){
           return strings.stream().filter(s -> Character.isUpperCase(s.charAt(0))).collect(Collectors.toList());
    }

    public static String longest(Collection<String> strings, boolean from_start){
        return strings.stream().reduce((String) strings.toArray()[0], (a,b) -> from_start?a.length() >= b.length()? a: b: b);
    }

    public static <T extends Comparable<T>> T least(Collection<T> items, boolean from_start){
        //compareTo -> -1
        return items.stream().reduce((a,b) -> from_start? (a.compareTo(b) <= 0? a: b): (a.compareTo(b) < 0? a: b)).get();
    }

    public static <K, V> List<String> flatten(Map<K, V> aMap){
        return aMap.entrySet().stream().map(e -> e.getKey() + " -> " + e.getValue()).collect(Collectors.toList());
    }
}
