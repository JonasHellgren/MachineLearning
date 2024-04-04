package policy_gradient_problems.environments.short_corridor;


public record VariablesSC(int posReal, int posObserved) {

    public static final int POS_REAL_DUMMY = 1;

    public static VariablesSC newDefault() {
        return new VariablesSC(POS_REAL_DUMMY,EnvironmentSC.getObservedPos(POS_REAL_DUMMY));
    }

    public static VariablesSC newOfReal(int posReal) {
        return new VariablesSC(posReal,EnvironmentSC.getObservedPos(posReal));
    }

    public static VariablesSC newOfObserved(int posObserved) {
        return new VariablesSC(POS_REAL_DUMMY,posObserved);
    }


    public VariablesSC copy() {
        return new VariablesSC(posReal,posObserved);
    }
}
