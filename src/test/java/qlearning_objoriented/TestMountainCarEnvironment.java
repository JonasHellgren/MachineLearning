package qlearning_objoriented;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import org.junit.jupiter.api.Disabled;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.StepReturn;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_mountaincar.MountainCar;

import java.util.concurrent.TimeUnit;

public class TestMountainCarEnvironment {

    State state = new State();
    MountainCar env=new MountainCar();

    @Test @Ignore
    public void EnvironmentParameters() {
        Assert.assertEquals(env.parameters.continuousStateVariableNames.get(0),"position");
        Assert.assertEquals(env.parameters.continuousStateVariableNames.get(1),"velocity");
        Assert.assertEquals(env.parameters.discreteActionsSpace.size(),3,0.01);
    }


    @Test @Ignore
    public void setAction1NoStateChange() {
        state.createContinuousVariable("position",-0.5);
        state.createContinuousVariable("velocity",0.0);
        state.createDiscreteVariable("nofSteps",0);
        System.out.println(state);
        StepReturn stepReturn=env.step(1,state);
        System.out.println(stepReturn.state);
        Assert.assertEquals(-0.5,stepReturn.state.getContinuousVariable("position"),0.01);
    }

    @Test @Ignore
    public void setAction0MovesLeft() throws InterruptedException {
        state.createContinuousVariable("position",-0.5);
        state.createContinuousVariable("velocity",0.0);
        state.createDiscreteVariable("nofSteps",0);
        System.out.println(state);
        StepReturn stepReturn=env.step(0,state);
        System.out.println(stepReturn.state);
        Assert.assertTrue(stepReturn.state.getContinuousVariable("position")<-0.5);

        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    public void setAction0MovesLeftManyTimes() throws InterruptedException {
        state.createContinuousVariable("position",-0.5);
        state.createContinuousVariable("velocity",0.0);
        state.createDiscreteVariable("nofSteps",0);
        System.out.println(state);
        StepReturn stepReturn=env.step(0,state);
        System.out.println(stepReturn.state);

        for (int i = 0; i <10 ; i++) {

            stepReturn=env.step(0,state);
            state.copyState(stepReturn.state);
            System.out.println(state);
            double position=state.getContinuousVariable("position");
            double height=env.height(position);
            //env.panel.setCarPosition(position,height);
            env.panel.setCarPosition(i*-0.1,env.height(i*-0.1));
            System.out.println(env.panel.carPosition);
            env.panel.repaint();
            TimeUnit.SECONDS.sleep(1);
        }

    }

}
