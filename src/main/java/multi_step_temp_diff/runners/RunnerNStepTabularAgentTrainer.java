package multi_step_temp_diff.runners;

import common.MultiplePanelsPlotter;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironment;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.helpers.AgentInfo;
import multi_step_temp_diff.domain.trainer.NStepTabularAgentTrainer;
import multi_step_temp_diff.domain.agents.fork.AgentForkTabular;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/***
 * This runner shows that td error decreases more rapidly with many steps learning
 */

public class RunnerNStepTabularAgentTrainer {
    private static final int ONE_STEP = 1;
    private static final int THREE_STEPS = 3;
    private static final int NOF_EPISODES = 100;
    private static final int LENGTH_WINDOW = 50;
    static NStepTabularAgentTrainer<ForkVariables> trainer;
    static AgentForkTabular agent;

    public static void main(String[] args) {
        final ForkEnvironment environment = new ForkEnvironment();
        agent = AgentForkTabular.newDefault(environment);
        trainer= NStepTabularAgentTrainer.<ForkVariables>builder()
                .nofEpisodes(NOF_EPISODES)
                .alpha(0.2).probStart(0.25).probEnd(1e-5)
                .environment(environment).agent(agent)
                .build();

        AgentInfo<ForkVariables> agentInfo=new AgentInfo<>(agent);
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
