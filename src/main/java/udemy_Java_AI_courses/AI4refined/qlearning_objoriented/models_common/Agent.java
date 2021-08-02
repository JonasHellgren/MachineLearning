package udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common;

import java.util.List;

public interface Agent {
    State getState();
    double getRB_EPS();
    double getRB_ALP();
    double getBETA0();

    int chooseBestAction(State state);
    double findMaxQ(State state);
    int chooseRandomAction(List<Integer> actions);
    void writeMemory(State oldState, Integer Action, Double value);
    double readMemory(State state, int Action);
}
