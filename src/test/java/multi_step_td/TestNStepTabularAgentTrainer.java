package multi_step_td;

import common.ListUtils;
import common.MultiplePanelsPlotter;
import lombok.SneakyThrows;
import multi_step_temp_diff.helpers.AgentInfo;
import multi_step_temp_diff.helpers.NStepTabularAgentTrainer;
import multi_step_temp_diff.models.AgentForkTabular;
import multi_step_temp_diff.environments.ForkEnvironment;
import org.jcodec.common.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestNStepTabularAgentTrainer {

    private static final int ONE_STEP = 1;
    private static final int THREE_STEPS = 3;
    private static final int NOF_EPISODES = 100;
    private static final int LENGTH_WINDOW = 50;
    private static final int SLEEP_TIME_MILLIS = 1_000;
    NStepTabularAgentTrainer trainer;
    AgentForkTabular agent;

    @Before
    public void init() {
        final ForkEnvironment environment = new ForkEnvironment();
        agent = AgentForkTabular.newDefault(environment);
        trainer= NStepTabularAgentTrainer.builder()
                .nofEpisodes(NOF_EPISODES)
                .alpha(0.2).probStart(0.25).probEnd(1e-5)
                .environment(environment).agent(agent)
                .build();
    }

    @SneakyThrows
    @Test public void whenIncreasingNofSteps_thenBetterStateValues() {
        AgentInfo agentInfo=new AgentInfo(agent);
        List<List<Double>> listOfTrajectories=new ArrayList<>();

        trainer.setNofStepsBetweenUpdatedAndBackuped(ONE_STEP);
        trainer.train();
        Map<Integer,Double> mapOneStep= trainer.getStateValueMap();
        double avgErrOne=TestHelper.avgError(mapOneStep);

        List<Double> filtered1 = agentInfo.getFilteredTemporalDifferenceList(LENGTH_WINDOW);
        listOfTrajectories.add(filtered1);

        System.out.println("agentInfo.getNofSteps()  1= " + agentInfo.getNofSteps());

        agent.clear();
        trainer.setNofStepsBetweenUpdatedAndBackuped(THREE_STEPS);
        trainer.train();
        Map<Integer,Double> mapTreeSteps= trainer.getStateValueMap();
        double avgErrThree=TestHelper.avgError(mapTreeSteps);

        System.out.println("agentInfo.getNofSteps() 3 = " + agentInfo.getNofSteps());

        List<Double> filtered3 = agentInfo.getFilteredTemporalDifferenceList(LENGTH_WINDOW);
        listOfTrajectories.add(filtered3);

        System.out.println("mapTreeSteps = " + mapTreeSteps);
        System.out.println("avgErrOne = " + avgErrOne+", avgErrThree = " + avgErrThree);
        System.out.println("ListUtils.findAverage(filtered1) = " + ListUtils.findAverage(filtered1));
        System.out.println("ListUtils.findAverage(filtered3) = " + ListUtils.findAverage(filtered3));

        Assert.assertTrue(avgErrOne>avgErrThree);

        MultiplePanelsPlotter plotter=new MultiplePanelsPlotter(Arrays.asList("ONE","THREE_STEPS"), "Step");
        plotter.plot(listOfTrajectories);
        Thread.sleep(SLEEP_TIME_MILLIS);


    }




}
