package udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common;

public interface Agent {
    Integer chooseBestAction(State state, EnvironmentParametersAbstract envParams);
    Integer chooseRandomAction(State state, EnvironmentParametersAbstract envParams);
    void updateMemory(State oldState, StepReturnAbstract stepReturn);
    Double[] readMemory(State state);
}
