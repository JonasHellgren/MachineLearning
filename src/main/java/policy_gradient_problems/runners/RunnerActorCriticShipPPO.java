package policy_gradient_problems.runners;

import common.plotters.PlotterMultiplePanelsTrajectory;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.common_episode_trainers.ActorCriticEpisodeTrainerPPOCont;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.sink_the_ship.*;

import java.util.List;

@Log
public class RunnerActorCriticShipPPO {

    public static final int NOF_EPISODES = 1000;
    static final int NOF_STEPS_MAX = 100;
    static final double GAMMA = 0.0;

    public static void main(String[] args) {
        var trainer = createTrainerActorCritic(
                 EnvironmentShip.newDefault(), AgentShipPPO.newDefault());
        trainer.train();
        plotActionProbabilitiesDuringTraining(trainer);
        log.info("Training finished");
    }


    private static void plotActionProbabilitiesDuringTraining(TrainerActorCriticShipPPO trainer) {
        for (int s: EnvironmentShip.POSITIONS) {
            var plotter = new PlotterMultiplePanelsTrajectory(
                    "PPO",
                    List.of("stateNew = "+s+", mean", "std","value"), "episode");
            List<List<Double>> listOfTrajectories = trainer.getRecorderStateValues().valuesTrajectoryForEachAction(s);
            plotter.plot(listOfTrajectories);
        }
    }

    private static TrainerActorCriticShipPPO createTrainerActorCritic(EnvironmentShip environment, AgentShipPPO agent) {
        return TrainerActorCriticShipPPO.builder()
                .environment(environment).agent(agent)
                .shipSettings(ShipSettings.newDefault())
                .parameters(getTrainerParameters())
                .episodeTrainer(ActorCriticEpisodeTrainerPPOCont.<VariablesShip>builder()
                        .valueTermState(0d).agent(agent).parameters(getTrainerParameters()).build())
                .build();
    }


    private static TrainerParameters getTrainerParameters() {
        return TrainerParameters.builder()
                .nofEpisodes(NOF_EPISODES).nofStepsMax(NOF_STEPS_MAX).gamma(GAMMA)
                .build();
    }

}
