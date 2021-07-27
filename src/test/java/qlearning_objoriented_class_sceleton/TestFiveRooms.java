package qlearning_objoriented_class_sceleton;

import org.junit.Assert;
import org.junit.Test;
import qlearning_objoriented_class_sceleton.models.FiveRooms;
import qlearning_objoriented_class_sceleton.models.State;

public class TestFiveRooms {

    State state = new State();
    FiveRooms env=new FiveRooms();

    @Test
    public void EnvironmentParameters() {
        Assert.assertEquals(env.parameters.discreteStateVariableNames.get(0),"roomNumber");
        Assert.assertEquals(env.parameters.discreteActionsSpace.size(),5,0.01);
    }

    @Test
    public void EnvironmentParameterNotYetKnownParameter() {
        Assert.assertEquals(env.parameters.discreteStateVariableNames.get(0),"roomNumber");
        Assert.assertEquals(env.parameters.notYetKnownParameter,0,0.01);
    }


    @Test
    public void StepReturnnotYetKnownParameter() {
        state.createVariable("roomNumber",1);
        FiveRooms.StepReturn stepReturn=env.step(1,state);
        Assert.assertEquals(stepReturn.notYetKnownVariable,1,0.01);
    }

    @Test
    public void StepReturn() {
        state.createVariable("roomNumber",1);
        FiveRooms.StepReturn stepReturn=env.step(1,state);
        Assert.assertEquals(stepReturn.state.getDiscreteVariable("roomNumber"),2,0.01);
    }

}
