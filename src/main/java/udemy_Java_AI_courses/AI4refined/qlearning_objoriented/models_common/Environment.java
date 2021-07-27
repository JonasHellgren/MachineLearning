package udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common;

public interface Environment {
    StepReturnAbstract  step(int action, State state);
}
