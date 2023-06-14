package multi_step_temp_diff.runners;

import common.ListUtils;
import common.MultiplePanelsPlotter;
import multi_step_temp_diff.environments.ForkEnvironment;
import multi_step_temp_diff.helpers.AgentInfo;
import multi_step_temp_diff.helpers.NStepTabularAgentTrainer;
import multi_step_temp_diff.models.AgentForkTabular;
import org.jcodec.common.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RunnerNStepTabularAgentTrainer {
    private static final int ONE_STEP = 1;
    private static final int THREE_STEPS = 3;
    private static final int NOF_EPISODES = 100;
    private static final int LENGTH_WINDOW = 50;
    static NStepTabularAgentTrainer trainer;
    static AgentForkTabular agent;

    public static void main(String[] args) {
        final ForkEnvironment environment = new ForkEnvironment();
        agent = AgentForkTabular.newDefault(environment);
        trainer= NStepTabularAgentTrainer.builder()
                .nofEpisodes(NOF_EPISODES)
                .alpha(0.2).probStart(0.25).probEnd(1e-5)
                .environment(environment).agent(agent)
                .build();

        AgentInfo agentInfo=new AgentInfo(agent);
        List<List<Double>> listOfTrajectories=new ArrayList<>();

        trainer.setNofStepsBetweenUpdatedAndBackuped(ONE_STEP);
        trainer.train();
        List<Double> filtered1 = agentInfo.getFilteredTemporalDifferenceList(LENGTH_WINDOW);
        listOfTrajectories.add(filtered1);

        agent.clear();
        trainer.setNofStepsBetweenUpdatedAndBackuped(THREE_STEPS);
        trainer.train();
        List<Double> filtered3 = agentInfo.getFilteredTemporalDifferenceList(LENGTH_WINDOW);
        listOfTrajectories.add(filtered3);

        MultiplePanelsPlotter plotter=new MultiplePanelsPlotter(Arrays.asList("Error - 1","Error - 3"), "Step");
        plotter.plot(listOfTrajectories);


    }

}
