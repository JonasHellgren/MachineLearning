package multi_step_td;

import multi_step_temp_diff.helpers.NStepNeuralAgentTrainer;
import multi_step_temp_diff.models.AgentForkNeural;
import multi_step_temp_diff.models.AgentForkTabular;
import multi_step_temp_diff.models.ForkEnvironment;
import org.junit.Before;
import org.junit.Test;

public class TestNStepNeuralAgentTrainer {
    private static final int ONE_STEP = 1;
    NStepNeuralAgentTrainer trainer;

    @Before
    public void init() {
        AgentForkNeural agent= AgentForkNeural.newDefault();
        trainer= NStepNeuralAgentTrainer.builder()
                .nofEpisodes(5).agentNeural(agent)
                .environment(new ForkEnvironment()).agent(AgentForkTabular.newDefault())
                .build();
    }

    @Test
    public void when_then() {
        trainer.train();

        System.out.println("trainer.getBuffer() = " + trainer.getBuffer());

        System.out.println("trainer.getStateValueMap() = " + trainer.getStateValueMap());

    }
}
