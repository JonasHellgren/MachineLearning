package udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common;

import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_sixrooms.SixRooms;

public interface Environment {
    EnvironmentParametersAbstract getParameters();
    StepReturn step(int action, State state);  //polymorphism: step can return any sub class of StepReturnAbstract
    boolean isTerminalState(State state);
}
