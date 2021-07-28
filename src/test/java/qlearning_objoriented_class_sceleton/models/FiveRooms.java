package qlearning_objoriented_class_sceleton.models;

import java.util.Arrays;

public class FiveRooms implements Environment {


    // inner classes
    public class EnvironmentParameters extends EnvironmentParametersAbstract {
        public int notYetKnownParameter =0;
    }

    public class StepReturn extends StepReturnAbstract {
        public int notYetKnownVariable =0;
    }

    public EnvironmentParameters parameters=this.new EnvironmentParameters();

    //constructor
    public FiveRooms() {
        parameters.discreteStateVariableNames.add("roomNumber");
        parameters.discreteActionsSpace.addAll(Arrays.asList(1,2,3,4,5));
    }

    //methods
    @Override
    public  StepReturn  step(int action, State  state) {
        State newState = new State(state);
        FiveRooms.StepReturn stepReturn = new StepReturn();

        newState.setVariable("roomNumber",state.getDiscreteVariable("roomNumber")+action);
        stepReturn.state = newState;
        stepReturn.reward = 1d;
        stepReturn.done = false;

        stepReturn.notYetKnownVariable = 1;

        return stepReturn;
    }
}
