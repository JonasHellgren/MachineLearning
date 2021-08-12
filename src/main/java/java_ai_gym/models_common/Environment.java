package java_ai_gym.models_common;

public interface Environment {
    EnvironmentParametersAbstract getParameters();
    StepReturn step(int action, State state);  //polymorphism: step can return any sub class of StepReturnAbstract
    boolean isTerminalState(State state);
    default  double clip(double variable, double minValue, double maxValue) {
        double lowerThanMax= Math.min(variable, maxValue);
        return Math.max(lowerThanMax, minValue);
    }
}
