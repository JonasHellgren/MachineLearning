package multi_agent_rl.domain.abstract_classes;

import common.other.NumberFormatterUtil;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Generic action, handles both discrete and continuous actions, only one of the arguments can be non-empty
 */

public record ActionAgent(Optional<Integer> intValue, Optional<Double> doubleValue, boolean isSafeCorrected) {

    public static ActionAgent ofInteger(Integer intAction) {
        return new ActionAgent(Optional.of(intAction), Optional.empty(),false);
    }

    public static ActionAgent ofDouble(Double doubleAction) {
        return new ActionAgent(Optional.empty(), Optional.of(doubleAction),false);
    }


    public static ActionAgent ofDoubleSafeCorrected(Double doubleAction) {
        return new ActionAgent(Optional.empty(), Optional.of(doubleAction),true);
    }

    public ActionAgent {
        long nofInt = intValue.isPresent() ? 1 : 0;
        long nofDouble = doubleValue.isPresent() ? 1 : 0;
        if (nofInt + nofDouble != 1) {
            throw new IllegalArgumentException("Exactly one value in action must be defined");
        }
    }

    public int asInt() {
        return intValue.orElseThrow(() -> new NoSuchElementException("Int value not present"));
    }

    public double asDouble() {
        return doubleValue.orElseThrow(() -> new NoSuchElementException("Double value not present"));
    }



    @Override
    public String toString() {
        var f= NumberFormatterUtil.formatterOneDigit;
        return intValue.map(Object::toString).orElseGet(() -> f.format(doubleValue.orElseThrow()));
    }

}
