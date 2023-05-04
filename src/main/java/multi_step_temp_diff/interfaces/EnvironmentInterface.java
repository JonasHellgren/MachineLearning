package multi_step_temp_diff.interfaces;


import multi_step_temp_diff.models.StepReturn;

public interface EnvironmentInterface {
    StepReturn step(int action, int state);
    boolean isTerminalState(int state);

}
