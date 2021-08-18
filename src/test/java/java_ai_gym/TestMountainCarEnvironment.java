package java_ai_gym;

import java_ai_gym.models_common.EnvironmentParametersAbstract;
import java_ai_gym.models_common.State;
import java_ai_gym.models_common.StepReturn;
import java_ai_gym.models_mountaincar.MountainCar;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


import java.util.concurrent.TimeUnit;

public class TestMountainCarEnvironment {

    State state = new State();
    MountainCar env=new MountainCar();

    @Test @Ignore
    public void EnvironmentParameters() {
        Assert.assertEquals("position",env.parameters.continuousStateVariableNames.get(0));
        Assert.assertEquals("velocity",env.parameters.continuousStateVariableNames.get(1));
        Assert.assertEquals(3,env.parameters.discreteActionsSpace.size(),0.01);
        Assert.assertEquals(-1.2,env.parameters.MIN_POSITION,0.01);

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

    @Test @Ignore
    public void setAction0MovesLeftManyTimes() throws InterruptedException {
        state.createContinuousVariable("position",-0.5);
        state.createContinuousVariable("velocity",0.0);
        state.createDiscreteVariable("nofSteps",0);
        System.out.println(state);
        StepReturn stepReturn=env.step(0,state);
        System.out.println(stepReturn.state);

        for (int i = 0; i <1300 ; i++) {
            stepReturn=env.step(0,state);
            state.copyState(stepReturn.state);
            System.out.println(state);
            double position=i*-0.001;
            env.panel.setCarStates(position,env.height(position),state.getContinuousVariable("velocity"));
            env.panel.repaint();
            TimeUnit.MILLISECONDS.sleep(10);
        }

    }

    @Test
    public void setRuleBasedAction() throws InterruptedException {
        state.createContinuousVariable("position",env.startPosition);
        state.createContinuousVariable("velocity",env.startVelocity);
        state.createDiscreteVariable("nofSteps",0);
        System.out.println(state);
        StepReturn stepReturn=env.step(0,state);
        System.out.println(stepReturn.state);
        int action=1;

        do {

            double position=state.getContinuousVariable("position");
            double velocity=state.getContinuousVariable("velocity");

            action=(velocity <0)?0:2;
            stepReturn=env.step(action,state);
            state.copyState(stepReturn.state);

            env.panel.setCarStates(position,env.height(position),velocity);
            env.panel.repaint();
            TimeUnit.MILLISECONDS.sleep(100);

            if (stepReturn.termState) {
                System.out.println("Terminal state reached");
                System.out.println(state);
                System.out.println(stepReturn);
            }

        } while (!stepReturn.termState);

        TimeUnit.MILLISECONDS.sleep(1000);

    }


}
