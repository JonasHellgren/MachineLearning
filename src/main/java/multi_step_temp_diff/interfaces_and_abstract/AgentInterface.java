package multi_step_temp_diff.interfaces_and_abstract;

import multi_step_temp_diff.models.StepReturn;

//https://www.cs.ubc.ca/labs/lci/mlrg/slides/Multi-step_Bootstrapping.pdf

public interface AgentInterface {
    int getState();
    void setState(int state);
    int chooseAction(double probRandom);
    void updateState(StepReturn stepReturn);
    double readValue(int state);
    void clear();
}
