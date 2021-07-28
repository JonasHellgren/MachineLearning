package udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common;

public abstract  class StepReturnAbstract {
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
