package udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_fiverooms;

import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.Agent;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.EnvironmentParametersAbstract;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.StepReturnAbstract;

public class FiveRoomsAgentTabular implements Agent {

    State state;
    Double Qsa[][];

    public FiveRoomsAgentTabular(EnvironmentParametersAbstract envParams) {
        state=new State();
        for (String varName:envParams.discreteStateVariableNames)
            state.createVariable(varName,0);

        int nofStates=5; //TODO extract from envParams
        int nofActions=envParams.discreteActionsSpace.size();

        Qsa=new Double[nofStates][nofActions];
    }

    @Override
    public Integer chooseBestAction(State state, EnvironmentParametersAbstract envParams) {
        return null;
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
