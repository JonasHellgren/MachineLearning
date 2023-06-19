package multi_step_td.fork;

import lombok.SneakyThrows;
import multi_step_td.TestHelper;
import multi_step_temp_diff.environments.ForkEnvironment;
import multi_step_temp_diff.environments.ForkState;
import multi_step_temp_diff.environments.ForkVariables;
import multi_step_temp_diff.helpers.NStepTabularAgentTrainer;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.agents.AgentForkTabular;
import org.jcodec.common.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class TestNStepTabularAgentTrainer {

    private static final int ONE_STEP = 1;
    private static final int THREE_STEPS = 3;
    private static final int NOF_EPISODES = 100;
    NStepTabularAgentTrainer<ForkVariables> trainer;
    AgentForkTabular agent;

    @Before
    public void init() {
        final ForkEnvironment environment = new ForkEnvironment();
        agent = AgentForkTabular.newDefault(environment);
        trainer= NStepTabularAgentTrainer.<ForkVariables>builder()
                .nofEpisodes(NOF_EPISODES)
                .alpha(0.2).probStart(0.25).probEnd(1e-5)
                .environment(environment).agent(agent)
                .startStateSupplier(() -> ForkState.newFromPos(0))
                .build();
    }

    @SneakyThrows
    @Test public void whenIncreasingNofSteps_thenBetterStateValues() {
        trainer.setNofStepsBetweenUpdatedAndBackuped(ONE_STEP);
        trainer.train();
        Map<StateInterface<ForkVariables>, Double> mapOneStep= trainer.getStateValueMap();
        double avgErrOne= TestHelper.avgErrorFork(mapOneStep);

        agent.clear();
        trainer.setNofStepsBetweenUpdatedAndBackuped(THREE_STEPS);
        trainer.train();
        Map<StateInterface<ForkVariables>, Double> mapTreeSteps= trainer.getStateValueMap();
        double avgErrThree=TestHelper.avgErrorFork(mapTreeSteps);

        System.out.println("mapTreeSteps = " + mapTreeSteps);
        System.out.println("avgErrOne = " + avgErrOne+", avgErrThree = " + avgErrThree);

        Assert.assertTrue(avgErrOne>avgErrThree);
    }




}
