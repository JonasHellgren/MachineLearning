package qlearning_objoriented.models;

public interface Environment {
    //<T extends StateInterface> StepReturnInterface  step(int action, T state);
    StepReturnInterface  step(int action, StateInterface state);
}
