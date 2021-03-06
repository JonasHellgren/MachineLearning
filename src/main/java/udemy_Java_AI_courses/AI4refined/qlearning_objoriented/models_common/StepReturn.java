package udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common;

public  class StepReturn {
    public State state;
    public Double reward;
    public Boolean termState;

    public StepReturn(State state, Double reward, Boolean termState) {
        this.state = state;
        this.reward = reward;
        this.termState = termState;
    }

    public StepReturn() {
        this.state = new State();
        this.reward = 0.0;
        this.termState = false;
    }

    @Override
    public String toString() {
        return "StepReturnAbstract{" +
                "state=" + state +
                ", reward=" + reward +
                ", termState=" + termState +
                '}';
    }
}
