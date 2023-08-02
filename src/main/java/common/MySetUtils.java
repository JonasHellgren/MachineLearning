package common;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MySetUtils {


    public static Set<Integer> getSetFromRange(int min, int maxExclusive) {
        return IntStream.range(min, maxExclusive).boxed().collect(Collectors.toSet());
    }

    public static Set<Integer> getSetFromRangeInclusive(int min, int maxInclusive) {
        return IntStream.range(min, maxInclusive+1).boxed().collect(Collectors.toSet());
    }


}
