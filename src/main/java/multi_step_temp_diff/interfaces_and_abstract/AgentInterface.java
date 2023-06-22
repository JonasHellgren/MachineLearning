package multi_step_temp_diff.interfaces_and_abstract;

import multi_step_temp_diff.models.StepReturn;

//https://www.cs.ubc.ca/labs/lci/mlrg/slides/Multi-step_Bootstrapping.pdf

public interface AgentInterface<S> {
    StateInterface<S> getState();
    void setState(StateInterface<S> state);
    int chooseAction(double probRandom);
    void updateState(StepReturn<S> stepReturn);
    double readValue(StateInterface<S> state);
    void clear();
    void storeTemporalDifference(double difference);
}
