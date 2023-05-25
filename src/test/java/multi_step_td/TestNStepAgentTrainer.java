package multi_step_td;

import common.ListUtils;
import common.MathUtils;
import multi_step_temp_diff.helpers.NStepAgentTrainer;
import multi_step_temp_diff.models.AgentTabular;
import multi_step_temp_diff.models.ForkEnvironment;
import org.jcodec.common.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestNStepAgentTrainer {

    private static final int ONE_STEP = 1;
    private static final int THREE_STEPS = 3;
    NStepAgentTrainer trainer;

    @Before
    public void init() {

        trainer= NStepAgentTrainer.builder()
                .nofEpisodes(50)
                .environment(new ForkEnvironment()).agent(AgentTabular.newDefault())
                .build();
    }

    @Test public void whenIncreasingNofSteps_thenBetterStateValues() {

        trainer.setNofStepsBetweenUpdatedAndBackuped(ONE_STEP);
        trainer.train();
        Map<Integer,Double> mapOneStep= trainer.getStateValueMap();
        double avgErrOne=avgError(mapOneStep);

        trainer.setNofStepsBetweenUpdatedAndBackuped(THREE_STEPS);
        trainer.train();
        Map<Integer,Double> mapTreeSteps= trainer.getStateValueMap();
        double avgErrThree=avgError(mapTreeSteps);

        System.out.println("avgErrOne = " + avgErrOne+", avgErrThree = " + avgErrThree);

        Assert.assertTrue(avgErrOne>avgErrThree);

    }

    private double avgError(Map<Integer, Double> mapOneStep) {
        List<Double> errors=new ArrayList<>();
        errors.add(Math.abs(mapOneStep.get(0)-ForkEnvironment.R_HEAVEN));
        errors.add(Math.abs(mapOneStep.get(7)-ForkEnvironment.R_HEAVEN));
        errors.add(Math.abs(mapOneStep.get(6)-ForkEnvironment.R_HELL));
        errors.add(Math.abs(mapOneStep.get(11)-ForkEnvironment.R_HELL));
        return ListUtils.findAverage(errors).orElseThrow();
    }


}
