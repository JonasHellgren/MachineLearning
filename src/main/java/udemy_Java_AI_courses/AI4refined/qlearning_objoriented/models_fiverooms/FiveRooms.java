package udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_fiverooms;

import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.Environment;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.EnvironmentParametersAbstract;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.StepReturnAbstract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FiveRooms implements Environment {

    // inner classes
    public class EnvironmentParameters extends EnvironmentParametersAbstract {
        public int notYetKnownParameter = 0;
        public List<Integer> stateSpace = Arrays.asList(0, 1, 2, 3, 4, 5);

        public final double R_FAIL = -1e5;  //non allowed transition
        public final double R_MOVE = -0.1;  //move transition
        public final double R_EXIT = 100;  //finding exit state reward

        private final int ROOM_EXIT_NUMBER = 5;
        public final int INIT_DEFAULT_ROOM_NUMBER =1;

        public final double GAMMA = 0.9;  // gamma discount factor
        public final double ALPHA = 0.1;  // learning rate
        public final int NUM_OF_EPISODES = 100000; // number of iterations

        private final double[][] R = {   //R(s,a) function
                {R_FAIL, R_FAIL, R_FAIL, R_FAIL, R_MOVE, R_FAIL},
                {R_FAIL, R_FAIL, R_FAIL, R_MOVE, R_FAIL, R_EXIT},
                {R_FAIL, R_FAIL, R_FAIL, R_MOVE, R_FAIL, R_FAIL},
                {R_FAIL, R_MOVE, R_MOVE, R_FAIL, R_MOVE, R_FAIL},
                {R_MOVE, R_FAIL, R_FAIL, R_MOVE, R_FAIL, R_EXIT},
                {R_FAIL, R_MOVE, R_FAIL, R_FAIL, R_MOVE, R_EXIT},
        };

        private int minRoomNumber;
        private int minAction;
        public  int nofStates;
        public int nofActions;

        public EnvironmentParameters() {
        }

        public int getIdxState(State state) {
            return state.getDiscreteVariable("roomNumber") - parameters.minRoomNumber;
        }

        public int getIdxAction(int action) {
            return action -parameters.minAction;
        }
    }


    public class StepReturn extends StepReturnAbstract {
        public int notYetKnownVariable = 0;
    }

    public EnvironmentParameters parameters = this.new EnvironmentParameters();

    //constructor
    public FiveRooms() {
        parameters.discreteStateVariableNames.add("roomNumber");
        parameters.discreteActionsSpace.addAll(Arrays.asList(0, 1, 2, 3, 4, 5));
        parameters.minRoomNumber = parameters.stateSpace.stream().min(Integer::compare).get();
        parameters.minAction = parameters.discreteActionsSpace.stream().min(Integer::compare).get();
        parameters.nofStates=parameters.stateSpace.size();
        parameters.nofActions=parameters.discreteActionsSpace.size();
    }

    //methods
    @Override
    public StepReturn step(int action, State state) {
        State newState = new State(state);
        FiveRooms.StepReturn stepReturn = new StepReturn();
        newState.setVariable("roomNumber", action);
        stepReturn.state = newState;
        stepReturn.reward = parameters.R[parameters.getIdxState(state)][parameters.getIdxAction(action)];
        stepReturn.termState = isTerminalState(newState);
        return stepReturn;
    }

    @Override
    public boolean isTerminalState(State state) {
        return (state.getDiscreteVariable("roomNumber") == parameters.ROOM_EXIT_NUMBER);
    }

    public List<Integer> getFeasibleActions(State state) {

        List<Integer> feasibleActions = new ArrayList<>();
        for (int action:parameters.discreteActionsSpace) {
            if (parameters.R[parameters.getIdxState(state)][parameters.getIdxAction(action)] > parameters.R_FAIL) {
                feasibleActions.add(parameters.discreteActionsSpace.get(parameters.getIdxAction(action)));
            }
        }
        return feasibleActions;
    }

}
