package multi_step_td;

import multi_step_temp_diff.helpers.AgentInfo;
import multi_step_temp_diff.helpers.NStepNeuralAgentTrainer;
import multi_step_temp_diff.interfaces.AgentNeuralInterface;
import multi_step_temp_diff.models.AgentForkNeural;
import multi_step_temp_diff.models.AgentForkTabular;
import multi_step_temp_diff.models.ForkEnvironment;
import org.jcodec.common.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestNStepNeuralAgentTrainer {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 1;
    private static final double MAX_ERROR = 2;
    NStepNeuralAgentTrainer trainer;
    AgentNeuralInterface agent;
    AgentForkNeural agentCasted;
    ForkEnvironment environment;

    @Before
    public void init() {
        agent= AgentForkNeural.newDefault();
        agentCasted=(AgentForkNeural) agent;
        environment = new ForkEnvironment();
        trainer= NStepNeuralAgentTrainer.builder()
                .nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                .nofEpisodes(10).batchSize(10).agentNeural(agent)
                .probStart(0.5).probEnd(0.01)
                .environment(environment)
                .agentNeural(agent)
                .build();
    }

    @Test
    public void whenTrained_thenCorrectStateValues() {
        trainer.train();

      //  System.out.println("trainer.getBuffer() = " + trainer.getBuffer());

        TestHelper.printStateValues(agentCasted.getMemory());
        AgentInfo agentInfo=new AgentInfo(agent);
        double avgErrThree=TestHelper.avgError(agentInfo.stateValueMap(environment.stateSet()));
        System.out.println("avgErrThree = " + avgErrThree);
        System.out.println("trainer.getBuffer().size() = " + trainer.getBuffer().size());

        Assert.assertTrue(avgErrThree < MAX_ERROR);

    }
}
