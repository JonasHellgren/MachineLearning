package qlearning_objoriented_class_sceleton.models;

public abstract class StepReturnAbstract {
public State state;
public Double reward;
public Boolean done;

    @Override
    public String toString() {
        return "StepReturnAbstract{" +
                "state=" + state +
                ", reward=" + reward +
                ", done=" + done +
                '}';
    }
}
