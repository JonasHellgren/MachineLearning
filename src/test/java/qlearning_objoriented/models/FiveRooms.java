package qlearning_objoriented.models;

public class FiveRooms implements Environment {

    // inner classes
    class State implements StateInterface {
        public int roomNumber;
    }

    class StepReturn implements StepReturnInterface {
        public State state;
        public Double reward;
        public Boolean done;
    }


    @Override
    //public <T extends StateInterface> StepReturnInterface  step(int action, T state) {
    public  StepReturnInterface  step(int action, StateInterface  state) {
        FiveRooms.State newState = new State();
        FiveRooms.StepReturn stepReturn = new StepReturn();

        //newState.roomNumber=(FiveRooms.State ) state.roomNumber + action;
        stepReturn.state = newState;
        stepReturn.reward = 1d;
        stepReturn.done = false;

        return stepReturn;
    }
}
