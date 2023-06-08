package multi_step_temp_diff.interfaces;

import multi_step_temp_diff.models.StepReturn;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;

import java.util.List;

//https://www.cs.ubc.ca/labs/lci/mlrg/slides/Multi-step_Bootstrapping.pdf

public interface AgentInterface {
    int getState();
    void setState(int state);
    int chooseAction(double probRandom);
    int chooseRandomAction();
    int chooseBestAction(int state);
    void updateState(StepReturn stepReturn);
    double readValue(int state);
    double getDiscountFactor();

}
