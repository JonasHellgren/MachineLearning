package udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_fiverooms;

import udemy_Java_AI_courses.AI4refined.qlearning.Constants;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.Agent;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.EnvironmentParametersAbstract;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.StepReturnAbstract;

public class FiveRoomsAgentTabular implements Agent {

    public State state;
    public Double Qsa[][];

    public FiveRoomsAgentTabular(FiveRooms.EnvironmentParameters envParams) {
        state=new State();
        for (String varName:envParams.discreteStateVariableNames)
            state.createVariable(varName,0);

        int nofStates=envParams.stateSpace.size();
        int nofActions=envParams.discreteActionsSpace.size();

        Qsa=new Double[nofStates][nofActions];
        for(int state = 0; state< nofStates; ++state) {
            for (int action = 0; action < nofActions; ++action) {
                Qsa[state][action] = 0d;
            }
        }
    }

    @Override
    public Integer chooseBestAction(State state, EnvironmentParametersAbstract envParams) {
        int maxQState = 0;
        int nofActions=envParams.discreteActionsSpace.size();
        double maxQ = envParams.R_FAIL;
        for(int action=0;action<nofActions;action++) {
            int roomNr=state.getDiscreteVariable("roomNumber");
            if( Qsa[roomNr][action] > maxQ) {
                maxQ = Qsa[roomNr][action];
                maxQState = action;
            }
        }
        return maxQState;
    }

    @Override
    public Integer chooseRandomAction(State state, EnvironmentParametersAbstract envParams) {
        return null;
    }

    @Override
    public void updateMemory(State oldState, StepReturnAbstract stepReturn) {

    }

    @Override
    public Double[] readMemory(State state) {
        return Qsa[state.getDiscreteVariable("roomNumber")];
    }
}
