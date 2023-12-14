package policy_gradient_problems.runners;

import plotters.PlotterMultiplePanelsTrajectory;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.sink_the_ship.AgentShip;
import policy_gradient_problems.the_problems.sink_the_ship.EnvironmentShip;
import policy_gradient_problems.the_problems.sink_the_ship.TrainerActorCriticShip;

import java.util.List;

public class RunnerTrainerActorCriticShip {

    public static final int NOF_EPISODES = 5_000, NOF_STEPS_MAX = 100;
    public static final double LEARNING_RATE = 1e-3, GAMMA = 1.0, BETA = 1e-2;

    public static void main(String[] args) {

        var trainerActorCritic = createTrainerActorCritic(
                new EnvironmentShip(),
                AgentShip.newRandomStartStateDefaultThetas());
        trainerActorCritic.train();
        plotActionProbabilitiesDuringTraining(trainerActorCritic);
    }


    private static void plotActionProbabilitiesDuringTraining(TrainerActorCriticShip trainer) {
        for (int s: EnvironmentShip.STATES) {
            var plotter = new PlotterMultiplePanelsTrajectory(
                    "ActorCritic",
                    List.of("state = "+s+", mean", "std","value"), "episode");
            plotter.plot(trainer.getTracker().getMeasureTrajectoriesForState(s));
        }
    }

    private static TrainerActorCriticShip createTrainerActorCritic(EnvironmentShip environment, AgentShip agent) {
        return TrainerActorCriticShip.builder()
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