package policy_gradient_problems.abstract_classes;

public record Action (Integer intValue, Double doubleValue) {

    public static Action asInteger(Integer intAction) {
        return new Action(intAction,null);
    }

    public static Action asDouble(Double doubleAction) {
        return new Action(null,doubleAction);
    }

    public Action {
        if ((intValue == null && doubleValue == null) || (intValue != null && doubleValue != null)) {
            throw new IllegalArgumentException("Exactly one of intValue or doubleValue must be non-null");
        }
    }

    public int asInt() {
        if (intValue == null) {
            throw new RuntimeException("Int value not defined");
        }
        return intValue;
    }


    public double asDouble() {
        if (doubleValue == null) {
            throw new RuntimeException ("Double value not defined");
        }
        return doubleValue;
    }

}
