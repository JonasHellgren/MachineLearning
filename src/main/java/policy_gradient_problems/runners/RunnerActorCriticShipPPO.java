package policy_gradient_problems.runners;

import common.plotters.PlotterMultiplePanelsTrajectory;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.sink_the_ship.AgentShipParam;
import policy_gradient_problems.environments.sink_the_ship.EnvironmentShip;
import policy_gradient_problems.environments.sink_the_ship.TrainerActorCriticShipParam;

import java.util.List;

@Log
public class RunnerActorCriticShipPPO {

    public static final int NOF_EPISODES = 5_000;
    static final int NOF_STEPS_MAX = 100;
    public static final double LEARNING_RATE = 1e-3;
    static final double GAMMA = 1.0;

/*    public static void main(String[] args) {
        var trainerActorCritic = createTrainerActorCritic(
                new EnvironmentShip(), AgentShipPPO.newDefault());
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
        return TrainerActorCriticShipPPO.builder()
                .environment(environment).agent(agent)
                .parameters(getTrainerParameters())
                .build();
    }*/


    private static TrainerParameters getTrainerParameters() {
        return TrainerParameters.builder()
                .nofEpisodes(NOF_EPISODES).nofStepsMax(NOF_STEPS_MAX).gamma(GAMMA)
                .build();
    }

}
