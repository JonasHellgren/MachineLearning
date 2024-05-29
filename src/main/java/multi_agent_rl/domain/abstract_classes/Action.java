package multi_agent_rl.domain.abstract_classes;

import common.other.NumberFormatterUtil;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Generic action, handles both discrete and continuous actions, only one of the arguments can be non-empty
 */

public record Action(Optional<List<Integer>> intValues, Optional<List<Double>> doubleValues) {

    public static Action ofInteger(List<Integer> intValues) {
        return new Action(Optional.of(intValues), Optional.empty());
    }

    public static Action ofDouble(List<Double> doubleValues) {
        return new Action(Optional.empty(), Optional.of(doubleValues));
    }



    public Action {
        long nofInt = intValues.isPresent() ? 1 : 0;
        long nofDouble = doubleValues.isPresent() ? 1 : 0;
        if (nofInt + nofDouble != 1) {
            throw new IllegalArgumentException("Exactly one value in action must be defined");
        }
    }

    public List<Integer> asInts() {
        return intValues.orElseThrow(() -> new NoSuchElementException("Int value not present"));
    }

    public List<Double> asDoubles() {
        return doubleValues.orElseThrow(() -> new NoSuchElementException("Double value not present"));
    }



    @Override
    public String toString() {
        var f= NumberFormatterUtil.formatterOneDigit;
        return intValues.map(Object::toString).orElseGet(() -> f.format(doubleValues.orElseThrow()));
    }

}
