package udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common;

import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_sixrooms.SixRooms;

public class Experience {
    public State s;
    public int action;
    public StepReturn stepReturn;  //includes reward and sNew

    public Experience(State s, int action, StepReturn stepReturn) {
        this.s = s;
        this.action = action;
        this.stepReturn = stepReturn;
    }

    @Override
    public String toString() {
        return "Experience{" +
                "s=" + s +
                ", action=" + action +
                ", reward=" + stepReturn.reward +
                ", termState=" + stepReturn.termState +
                ", sNew=" + stepReturn.state +
                '}' + System.lineSeparator();
    }
}
