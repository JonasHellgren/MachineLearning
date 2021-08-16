package java_ai_gym.models_common;

import java_ai_gym.swing.FrameEnvironment;

public abstract class  Environment {

    protected FrameEnvironment frame;
    protected abstract StepReturn step(int action, State state);
    protected abstract boolean isTerminalState(State state);
    protected double clip(double variable, double minValue, double maxValue) {
        double lowerThanMax= Math.min(variable, maxValue);
        return Math.max(lowerThanMax, minValue);
    }
}
