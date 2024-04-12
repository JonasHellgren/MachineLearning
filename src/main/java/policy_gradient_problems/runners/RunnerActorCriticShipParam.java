package policy_gradient_problems.runners;

import lombok.extern.java.Log;
import common.plotters.PlotterMultiplePanelsTrajectory;
import policy_gradient_problems.domain.common_episode_trainers.ParamActorTabCriticEpisodeTrainer;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.sink_the_ship.*;

import java.util.List;

@Log
public class RunnerActorCriticShipParam {

    public static final double VALUE_TERMINAL_STATE = 0;
    public static final int NOF_EPISODES = 5_000;
    static final int NOF_STEPS_MAX = 100;
    public static final double LEARNING_RATE = 1e-3;
    static final double GAMMA = 1.0;

    public static void main(String[] args) {
        var trainerActorCritic = createTrainerActorCritic(
                new EnvironmentShip(ShipSettings.newDefault()), AgentShipParam.newRandomStartStateDefaultThetas());
        trainerActorCritic.train();
        plotActionProbabilitiesDuringTraining(trainerActorCritic);
        log.info("Training finished");
    }


    private static void plotActionProbabilitiesDuringTraining(TrainerActorCriticShipParam trainer) {
        for (int s: EnvironmentShip.POSITIONS) {
            var plotter = new PlotterMultiplePanelsTrajectory(
                    "ActorCritic",
                    List.of("state = "+s+", mean", "std","value"), "episode");
          plotter.plot(trainer.getRecorderStateValues().valuesTrajectoryForEachAction(s));
        }
    }

    private static TrainerActorCriticShipParam createTrainerActorCritic(EnvironmentShip environment, AgentShipParam agent) {
        return TrainerActorCriticShipParam.builder()
                .environment(environment).agent(agent)
                .parameters(getTrainerParameters())
                .episodeTrainer(ParamActorTabCriticEpisodeTrainer.<VariablesShip>builder()
                        .agent(agent)
                        .parameters(getTrainerParameters())
                        .valueTermState(VALUE_TERMINAL_STATE)
                        .tabularCoder((v) -> v.pos())
                        .isTerminal((s) -> false)
                        .build())
                .build();
    }


    private static TrainerParameters getTrainerParameters() {
        return TrainerParameters.builder()
                .nofEpisodes(NOF_EPISODES).nofStepsMax(NOF_STEPS_MAX)
                .gamma(GAMMA).learningRateNonNeuralActor(LEARNING_RATE)
                .build();
    }


}
