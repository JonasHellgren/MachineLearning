package common;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SetUtils {


    public static Set<Integer> getSetFromRange(int min, int maxExclusive) {
        return IntStream.range(min, maxExclusive).boxed().collect(Collectors.toSet());
    }

}
