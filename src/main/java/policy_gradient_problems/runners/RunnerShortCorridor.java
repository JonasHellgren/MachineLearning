package policy_gradient_problems.runners;

import plotters.PlotterMultiplePanelsTrajectory;
import policy_gradient_problems.common.TrainerParameters;
import policy_gradient_problems.short_corridor.*;

import java.util.List;

public class RunnerShortCorridor {

    public static final double LEARNING_RATE = 0.05;
    public static final int NOF_EPISODES = 500;
    public static final int NOF_STEPS_MAX = 100;
    public static final double GAMMA = 1.0d;
    public static final double BETA = 0.1;

    public static void main(String[] args) {
        var trainer = createTrainer(EnvironmentSC.create(), AgentSC.newRandomStartStateDefaultThetas());
        trainer.train();
        plotActionProbabilitiesDuringTraining("Vanilla", trainer);

        var trainerBaseline = createTrainerBaseline(EnvironmentSC.create(), AgentSC.newRandomStartStateDefaultThetas());
        trainerBaseline.train();
        plotActionProbabilitiesDuringTraining("Baseline", trainerBaseline);

        var trainerActorCritic = createTrainerActorCritic(EnvironmentSC.create(), AgentSC.newRandomStartStateDefaultThetas());
        trainerActorCritic.train();
        plotActionProbabilitiesDuringTraining("ActorCritic", trainerActorCritic);
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
                .parameters(getTrainerParameters())
                .build();
    }

    private static TrainerBaselineSC createTrainerBaseline(EnvironmentSC environment, AgentSC agent) {
        return TrainerBaselineSC.builder()
                .environment(environment).agent(agent)
                .parameters(getTrainerParameters())
                .build();
    }

    private static TrainerActorCriticSC createTrainerActorCritic(EnvironmentSC environment, AgentSC agent) {
        return TrainerActorCriticSC.builder()
                .environment(environment).agent(agent)
                .parameters(getTrainerParameters())
                .build();
    }


    private static TrainerParameters getTrainerParameters() {
        return TrainerParameters.builder()
                .nofEpisodes(NOF_EPISODES).nofStepsMax(NOF_STEPS_MAX)
                .gamma(GAMMA).learningRate(LEARNING_RATE)
                .beta(BETA)  //not used by Vanilla
                .build();
    }

}
