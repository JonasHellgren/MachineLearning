package multi_step_temp_diff.runners;

import plotters.PlotterMultiplePanelsTrajectory;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironment;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.helpers.AgentInfo;
import multi_step_temp_diff.domain.trainer.NStepTabularAgentTrainer;
import multi_step_temp_diff.domain.agents.fork.AgentForkTabular;
import multi_step_temp_diff.domain.trainer_valueobj.NStepTabularAgentTrainerSettings;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/***
 * This runner shows that td error decreases more rapidly with many steps learning
 */

public class RunnerForkNStepTabularAgentTrainer {
    private static final int ONE_STEP = 1;
    private static final int THREE_STEPS = 3;
    private static final int NOF_EPISODES = 100;
    private static final int LENGTH_WINDOW = 50;
    public static final int START_POS = 0;
    static NStepTabularAgentTrainer<ForkVariables> trainer;
    static AgentForkTabular agent;

    public static void main(String[] args) {
        final ForkEnvironment environment = new ForkEnvironment();
        agent = AgentForkTabular.newDefault(environment);

        AgentInfo<ForkVariables> agentInfo=new AgentInfo<>(agent);
        List<List<Double>> listOfTrajectories=new ArrayList<>();

        NStepTabularAgentTrainerSettings settings = getnStepTabularAgentTrainerSettings(ONE_STEP);
        trainer= createTrainer(environment, settings);
        trainer.train();
        List<Double> filtered1 = agentInfo.getFilteredTemporalDifferenceList(LENGTH_WINDOW);
        listOfTrajectories.add(filtered1);

        agent.clear();
        settings = getnStepTabularAgentTrainerSettings(THREE_STEPS);
        trainer= createTrainer(environment, settings);
        trainer.train();
        List<Double> filtered3 = agentInfo.getFilteredTemporalDifferenceList(LENGTH_WINDOW);
        listOfTrajectories.add(filtered3);

        PlotterMultiplePanelsTrajectory plotter=new PlotterMultiplePanelsTrajectory(Arrays.asList("Error - 1","Error - 3"), "Step");
        plotter.plot(listOfTrajectories);

        DecimalFormat formatter = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US)); //US <=> only dots



    }

    private static NStepTabularAgentTrainer<ForkVariables> createTrainer(ForkEnvironment environment, NStepTabularAgentTrainerSettings settings) {
        return NStepTabularAgentTrainer.<ForkVariables>builder()
                .settings(settings).environment(environment).agent(agent)
                .startStateSupplier(() -> ForkState.newFromPos(START_POS))
                .build();
    }

    private static NStepTabularAgentTrainerSettings getnStepTabularAgentTrainerSettings(int nofStepsBetweenUpdatedAndBackuped) {
        return NStepTabularAgentTrainerSettings.builder()
                .nofEpis(NOF_EPISODES)
                .nofStepsBetweenUpdatedAndBackuped(nofStepsBetweenUpdatedAndBackuped)
                .alpha(0.2).probStart(0.25).probEnd(1e-5).build();
    }

}
