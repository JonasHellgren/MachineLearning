package qlearning_objoriented;

import org.junit.Assert;
import org.junit.Test;

import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.StepReturn;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_mountaincar.MountainCar;

public class TestMountainCarEnvironment {

    State state = new State();
    MountainCar env=new MountainCar();

    @Test
    public void EnvironmentParameters() {
        Assert.assertEquals(env.parameters.continuousStateVariableNames.get(0),"position");
        Assert.assertEquals(env.parameters.continuousStateVariableNames.get(1),"velocity");
        Assert.assertEquals(env.parameters.discreteActionsSpace.size(),3,0.01);
    }


    @Test
    public void setAction1NoStateChange() {
        state.createContinuousVariable("position",-0.5);
        state.createContinuousVariable("velocity",0.0);
        state.createDiscreteVariable("nofSteps",0);
        System.out.println(state);
        StepReturn stepReturn=env.step(1,state);
        System.out.println(state);
        Assert.assertEquals(-0.5,stepReturn.state.getContinuousVariable("position"),0.01);
    }

}
