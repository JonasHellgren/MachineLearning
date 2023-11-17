package common;

import org.apache.commons.lang3.tuple.Pair;


import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class BestPairFinder {


    public static  Optional<Pair<Integer, Integer>> maxPairInts(List<Pair<Integer, Integer>> pairs) {
            return  pairs.stream()
                .max(Comparator.comparing(Pair::getRight));
    }

    public static  Optional<Pair<Double, Double>> maxPairDoubles(List<Pair<Double, Double>> pairs) {
        return  pairs.stream()
                .max(Comparator.comparing(Pair::getRight));
    }

}
