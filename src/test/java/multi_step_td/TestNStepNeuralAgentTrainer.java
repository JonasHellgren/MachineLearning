package multi_step_td;

import multi_step_temp_diff.helpers.AgentInfo;
import multi_step_temp_diff.helpers.NStepNeuralAgentTrainer;
import multi_step_temp_diff.interfaces.AgentNeuralInterface;
import multi_step_temp_diff.models.AgentForkNeural;
import multi_step_temp_diff.models.AgentForkTabular;
import multi_step_temp_diff.models.ForkEnvironment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Big batch seems to destabilize
 */

public class TestNStepNeuralAgentTrainer {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 5;
    NStepNeuralAgentTrainer trainer;
    AgentNeuralInterface agent;
    AgentForkNeural agentCasted;
    ForkEnvironment environment;




    @Test
    public void givenDiscountFactorOne_whenTrained_thenCorrectStateValues() {
        agent= AgentForkNeural.builder().discountFactor(1.0).build();
        init();
        trainer.train();

      //  System.out.println("trainer.getBuffer() = " + trainer.getBuffer());
        TestHelper.printStateValues(agentCasted.getMemory());
        AgentInfo agentInfo=new AgentInfo(agent);
        double avgErrThree=TestHelper.avgError(agentInfo.stateValueMap(environment.stateSet()));
        System.out.println("avgErrThree = " + avgErrThree);
        System.out.println("trainer.getBuffer().size() = " + trainer.getBuffer().size());
        final double maxError = 2d;
        Assert.assertTrue(avgErrThree < maxError);
    }

    @Test
    public void givenDiscountFactorDot9_whenTrained_thenCorrectStateValues() {
        final double discountFactor = 0.9;
        agent= AgentForkNeural.builder().discountFactor(discountFactor).build();
        init();
        trainer.train();

        TestHelper.printStateValues(agentCasted.getMemory());

        final double rHeaven = ForkEnvironment.R_HEAVEN, rHell = ForkEnvironment.R_HELL;
        final double delta = 2d;
        Assert.assertEquals(Math.pow(discountFactor,10-7)* rHeaven,agentCasted.getMemory().read(7), delta);
        Assert.assertEquals(Math.pow(discountFactor,10-9)*rHeaven,agentCasted.getMemory().read(9), delta);
        Assert.assertEquals(Math.pow(discountFactor,9)*rHeaven,agentCasted.getMemory().read(0), delta);

        Assert.assertEquals(Math.pow(discountFactor,15-13)*rHell,agentCasted.getMemory().read(13), delta);
        Assert.assertEquals(Math.pow(discountFactor,5)*rHell,agentCasted.getMemory().read(6), delta);

    }

    public void init() {
        agentCasted=(AgentForkNeural) agent;
        environment = new ForkEnvironment();
        trainer= NStepNeuralAgentTrainer.builder()
                .nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                .nofEpisodes(300).batchSize(10).agentNeural(agent)
                .probStart(0.5).probEnd(0.01).nofTrainingIterations(1)
                .environment(environment)
                .agentNeural(agent)
                .build();
    }

}
