package qlearning_objoriented_class_sceleton.models;

public interface Environment {
    StepReturnAbstract  step(int action, State state);
}
