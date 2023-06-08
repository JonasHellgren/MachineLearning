package multi_step_td;

import multi_step_temp_diff.helpers.NStepTabularAgentTrainer;
import multi_step_temp_diff.models.AgentForkTabular;
import multi_step_temp_diff.environments.ForkEnvironment;
import org.jcodec.common.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class TestNStepTabularAgentTrainer {

    private static final int ONE_STEP = 1;
    private static final int THREE_STEPS = 3;
    private static final int NOF_EPISODES = 50;
    NStepTabularAgentTrainer trainer;

    @Before
    public void init() {
        final ForkEnvironment environment = new ForkEnvironment();
        trainer= NStepTabularAgentTrainer.builder()
                .nofEpisodes(NOF_EPISODES)
                .environment(environment).agent(AgentForkTabular.newDefault(environment))
                .build();
    }

    @Test public void whenIncreasingNofSteps_thenBetterStateValues() {

        trainer.setNofStepsBetweenUpdatedAndBackuped(ONE_STEP);
        trainer.train();
        Map<Integer,Double> mapOneStep= trainer.getStateValueMap();
        double avgErrOne=TestHelper.avgError(mapOneStep);

        trainer.setNofStepsBetweenUpdatedAndBackuped(THREE_STEPS);
        trainer.train();
        Map<Integer,Double> mapTreeSteps= trainer.getStateValueMap();
        double avgErrThree=TestHelper.avgError(mapTreeSteps);

        System.out.println("mapTreeSteps = " + mapTreeSteps);
        System.out.println("avgErrOne = " + avgErrOne+", avgErrThree = " + avgErrThree);

        Assert.assertTrue(avgErrOne>avgErrThree);

    }




}
