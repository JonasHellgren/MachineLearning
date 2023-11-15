package policy_gradient_problems.zeroOrOne;

import plotters.PlotterMultiplePanelsTrajectory;
import policy_gradient_problems.short_corridor.AgentSC;
import policy_gradient_problems.short_corridor.EnvironmentSC;
import policy_gradient_problems.short_corridor.TrainerSC;

import java.util.List;

public class RunnerShortCorridor {

    public static void main(String[] args) {
        var trainer = createEnvironmentAgentTrainerAndReturnTrainer();
        trainer.train();
        plotActionProbabilitiesDuringTraining(trainer);
    }

    private static TrainerSC createEnvironmentAgentTrainerAndReturnTrainer() {
        var environment = new EnvironmentSC();
        var agent = AgentSC.newRandomStartStateDefaultThetas();
        return createTrainer(environment, agent);
    }

    private static void plotActionProbabilitiesDuringTraining(TrainerSC trainer) {
        for (int s: EnvironmentSC.SET_OBSERVABLE_STATES_NON_TERMINAL) {
            var plotter = new PlotterMultiplePanelsTrajectory(List.of("state = "+s+", pi(0)", "pi(1)"), "episode");
            plotter.plot(trainer.getTracker().getProbabilitiesTrajectoriesForState(s));
        }
    }

    private static TrainerSC createTrainer(EnvironmentSC environment, AgentSC agent) {
        return TrainerSC.builder()
                .environment(environment)
                .agent(agent)
                .nofEpisodes(1000).nofStepsMax(10).gamma(1d).learningRate(0.02)
                .build();
    }

}
