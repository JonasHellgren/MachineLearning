package policy_gradient_problems.abstract_classes;

import java.util.Optional;

public record Action(Optional<Integer> intValue, Optional<Double> doubleValue) {

    public static Action ofInteger(Integer intAction) {
        return new Action(Optional.of(intAction), Optional.empty());
    }

    public static Action ofDouble(Double doubleAction) {
        return new Action(Optional.empty(), Optional.of(doubleAction));
    }

    public Action {
        long nofInt = intValue.isPresent() ? 1 : 0;
        long nofDouble = doubleValue.isPresent() ? 1 : 0;

        if (nofInt + nofDouble != 1) {
            throw new IllegalArgumentException("Exactly one value in action must be defined");
        }
    }

    public int asInt() {
        return intValue.orElseThrow();
    }


    public double asDouble() {
        return doubleValue.orElseThrow();
    }

}
