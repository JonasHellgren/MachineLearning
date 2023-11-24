package policy_gradient_problems.runners;

import plotters.PlotterMultiplePanelsTrajectory;
import policy_gradient_problems.common.TrainerParameters;
import policy_gradient_problems.short_corridor.*;

import java.util.List;

public class RunnerShortCorridor {

    public static final double LEARNING_RATE = 0.05;
    public static final int NOF_EPISODES = 500;

    public static void main(String[] args) {
        var trainer = createTrainer(EnvironmentSC.create(), AgentSC.newRandomStartStateDefaultThetas());
        trainer.train();
        plotActionProbabilitiesDuringTraining("Vanilla", trainer);

        TrainerBaselineSC trainerBaseline = createTrainerBaseline(EnvironmentSC.create(), AgentSC.newRandomStartStateDefaultThetas());
        trainerBaseline.train();
        plotActionProbabilitiesDuringTraining("Baseline", trainerBaseline);

    }


    private static void plotActionProbabilitiesDuringTraining(String title, TrainerAbstractSC trainer) {
        for (int s: EnvironmentSC.SET_OBSERVABLE_STATES_NON_TERMINAL) {
            var plotter = new PlotterMultiplePanelsTrajectory(title, List.of("state = "+s+", pi(0)", "pi(1)"), "episode");
            plotter.plot(trainer.getTracker().getProbabilitiesTrajectoriesForState(s));
        }
    }

    private static TrainerVanillaSC createTrainer(EnvironmentSC environment, AgentSC agent) {
        return TrainerVanillaSC.builder()
                .environment(environment).agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(NOF_EPISODES).nofStepsMax(100).gamma(1d).learningRate(LEARNING_RATE)
                        .build())
                .build();
    }


    private static TrainerBaselineSC createTrainerBaseline(EnvironmentSC environment, AgentSC agent) {
        return TrainerBaselineSC.builder()
                .environment(environment).agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(NOF_EPISODES).nofStepsMax(100).gamma(1d).beta(0.01).learningRate(LEARNING_RATE)
                        .build())
                .build();
    }

}
