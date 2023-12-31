package policy_gradient_problems.runners;

import plotters.PlotterMultiplePanelsTrajectory;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.short_corridor.*;

import java.util.List;

public class RunnerShortCorridor {

    public static final int NOF_EPISODES = 500, NOF_STEPS_MAX = 100;
    public static final double LEARNING_RATE = 0.05, GAMMA = 1.0, BETA = 0.1;

    public static void main(String[] args) {
        var trainer = createTrainer(AgentParamActorSC.newRandomStartStateDefaultThetas());
        trainer.train();
        plotActionProbabilitiesDuringTraining("Vanilla", trainer);

        var trainerBaseline = createTrainerBaseline(AgentParamActorTabCriticSC.newRandomStartStateDefaultThetas());
        trainerBaseline.train();
        plotActionProbabilitiesDuringTraining("Baseline", trainerBaseline);

        var trainerActorCritic = createTrainerActorCritic(AgentParamActorTabCriticSC.newRandomStartStateDefaultThetas());
        trainerActorCritic.train();
        plotActionProbabilitiesDuringTraining("ActorCritic", trainerActorCritic);
    }


    private static void plotActionProbabilitiesDuringTraining(String title, TrainerAbstractSC trainer) {
        for (int s: EnvironmentSC.SET_OBSERVABLE_STATES_NON_TERMINAL) {
            var plotter = new PlotterMultiplePanelsTrajectory(title, List.of("state = "+s+", pi(0)", "pi(1)"), "episode");
            plotter.plot(trainer.getTracker().getMeasureTrajectoriesForState(s));
        }
    }

    private static TrainerVanillaSC createTrainer(AgentParamActorSC agent) {
        return TrainerVanillaSC.builder()
                .environment(EnvironmentSC.create()).agent(agent)
                .parameters(getTrainerParameters())
                .build();
    }

    private static TrainerBaselineSC createTrainerBaseline(AgentParamActorTabCriticSC agent) {
        return TrainerBaselineSC.builder()
                .environment(EnvironmentSC.create()).agent(agent)
                .parameters(getTrainerParameters())
                .build();
    }

    private static TrainerActorCriticSC createTrainerActorCritic(AgentParamActorTabCriticSC agent) {
        return TrainerActorCriticSC.builder()
                .environment(EnvironmentSC.create()).agent(agent)
                .parameters(getTrainerParameters())
                .build();
    }


    private static TrainerParameters getTrainerParameters() {
        return TrainerParameters.builder()
                .nofEpisodes(NOF_EPISODES).nofStepsMax(NOF_STEPS_MAX)
                .gamma(GAMMA).learningRateActor(LEARNING_RATE)
                .learningRateCritic(BETA)  //not used by Vanilla
                .build();
    }

}
