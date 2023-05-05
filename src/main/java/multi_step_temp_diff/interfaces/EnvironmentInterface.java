package multi_step_temp_diff.interfaces;


import multi_step_temp_diff.models.StepReturn;

import java.util.Set;

public interface EnvironmentInterface {
    StepReturn step(int state,int action);
    boolean isTerminalState(int state);
    Set<Integer> actionSet();

}
