package udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_fiverooms;

import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.Environment;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.EnvironmentParametersAbstract;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.StepReturnAbstract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/***
 * Environment with 6 rooms. Inner class EnvironmentParameters defines data.
 * Inner class StepReturn is return protocol for step, transition method.
 * TransitionMatrix expresses new room number, for ex action 5 in room 0 gives new room is 0."
 */

public class SixRooms implements Environment {

    public EnvironmentParameters parameters = this.new EnvironmentParameters();

    // inner classes
    public class EnvironmentParameters extends EnvironmentParametersAbstract {
        public List<Integer> discreteStateSpace = Arrays.asList(0, 1, 2, 3, 4, 5);

        public final double R_FAIL = -1;  //non allowed transition
        public final double R_MOVE = -0.1;  //move transition
        public final double R_EXIT = 1;  //finding exit state reward

        private final int ROOM_EXIT_NUMBER = 5;
        public final int INIT_DEFAULT_ROOM_NUMBER = 1;

        private final double[][] rewardMatrix = {   //R(s,a) function
                {R_FAIL, R_FAIL, R_FAIL, R_FAIL, R_MOVE, R_FAIL},
                {R_FAIL, R_FAIL, R_FAIL, R_MOVE, R_FAIL, R_EXIT+R_MOVE},
                {R_FAIL, R_FAIL, R_FAIL, R_MOVE, R_FAIL, R_FAIL},
                {R_FAIL, R_MOVE, R_MOVE, R_FAIL, R_MOVE, R_FAIL},
                {R_MOVE, R_FAIL, R_FAIL, R_MOVE, R_FAIL, R_EXIT+R_MOVE},
                {R_FAIL, R_MOVE, R_FAIL, R_FAIL, R_MOVE, R_EXIT+R_MOVE},
        };

        private final int[][] transitionMatrix = {   //newRoomNr <- T(roomNr,action) function
                {0, 0, 0, 0, 4, 0},
                {1, 1, 1, 3, 1, 5},
                {2, 2, 2, 3, 2, 2},
                {3, 1, 2, 3, 4, 3},
                {0, 4, 4, 3, 4, 5},
                {5, 1, 5, 5, 4, 5},
        };

        private int minRoomNumber;
        private int minAction;
        public int nofStates;
        public int nofActions;

        public EnvironmentParameters() {
        }

        public int getIdxState(State state) {
            return state.getDiscreteVariable("roomNumber") - parameters.minRoomNumber;
        }

        public int getIdxAction(int action) {
            return action - parameters.minAction;
        }
    }


    public  class StepReturn extends StepReturnAbstract {

        public StepReturn() {
            super();
        }

        public StepReturn(State state, Double reward, Boolean termState) {
            super(state, reward, termState);
        }
    }

    //constructor
    public SixRooms() {
        parameters.discreteStateVariableNames.add("roomNumber");
        parameters.discreteActionsSpace.addAll(Arrays.asList(0, 1, 2, 3, 4, 5));
        parameters.minRoomNumber = parameters.discreteStateSpace.stream().min(Integer::compare).orElse(0);
        parameters.minAction = parameters.discreteActionsSpace.stream().min(Integer::compare).orElse(0);
        parameters.nofStates = parameters.discreteStateSpace.size();
        parameters.nofActions = parameters.discreteActionsSpace.size();
    }

    //methods
    @Override
    public StepReturn step(int action, State state) {
        State newState = new State(state);
        SixRooms.StepReturn stepReturn = new StepReturn();
        int newRoomNr=getNewRoomNr(state,action);
        newState.setVariable("roomNumber", newRoomNr);
        stepReturn.state = newState;
        stepReturn.reward = parameters.rewardMatrix[parameters.getIdxState(state)][parameters.getIdxAction(action)];
        stepReturn.termState = isTerminalState(newState);
        return stepReturn;
    }

    @Override
    public boolean isTerminalState(State state) {
        return (state.getDiscreteVariable("roomNumber") == parameters.ROOM_EXIT_NUMBER);
    }

    public List<Integer> getFeasibleActions(State state) {

        List<Integer> feasibleActions = new ArrayList<>();
        for (int action : parameters.discreteActionsSpace) {
            if (parameters.rewardMatrix[parameters.getIdxState(state)][parameters.getIdxAction(action)] > parameters.R_FAIL) {
                feasibleActions.add(parameters.discreteActionsSpace.get(parameters.getIdxAction(action)));
            }
        }
        return feasibleActions;
    }

    private int getNewRoomNr(State state, int action) {
        return parameters.transitionMatrix[parameters.getIdxState(state)][parameters.getIdxAction(action)];
    }

}
