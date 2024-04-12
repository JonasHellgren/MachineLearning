package policygradient.sink_the_ship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.common_episode_trainers.ParamActorTabCriticEpisodeTrainer;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.sink_the_ship.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

 class TestTrainerActorCriticShipParam {

     public static final double VALUE_TERMINAL_STATE = 0;
     static final double EXPECTED_ACTION0 = 0.2871;
     static final double EXPECTED_ACTION1 = 0.6711;
     static final double DELTA = 0.1;
     public static final int NOF_EPISODES = 5_000;
     static final int NOF_STEPS_MAX = 100;
     public static final double LEARNING_RATE = 1e-3;
     static final double GAMMA = 1.0;

     TrainerActorCriticShipParam trainer;
    AgentShipParam agent;

    @BeforeEach
     void init() {
        agent = AgentShipParam.newRandomStartStateDefaultThetas();
        var environment= new EnvironmentShip(ShipSettings.newDefault());
        createTrainer(environment);
    }

    private void createTrainer(EnvironmentShip environment) {
        trainer = TrainerActorCriticShipParam.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(1000).nofStepsMax(100).gamma(0.99d)
                        .learningRateNonNeuralActor(1e-3)
                        .build())
                .episodeTrainer(ParamActorTabCriticEpisodeTrainer.<VariablesShip>builder()
                .agent(agent)
                .parameters(getTrainerParameters())
                .valueTermState(VALUE_TERMINAL_STATE)
                .tabularCoder((v) -> v.pos())
                .isTerminal((s) -> false)
                .build())
                .build();
    }

    @Test
     void whenTrained_thenCorrectActionSelectionInEachState() {
        trainer.train();
        printPolicy();

        var meansStd0 = agent.getMeanAndStdFromThetaVector(0);
        var meansStd1 = agent.getMeanAndStdFromThetaVector(1);

        assertEquals(EXPECTED_ACTION0, meansStd0.getFirst(),DELTA);
        assertEquals(EXPECTED_ACTION1, meansStd1.getFirst(),DELTA);

    }

    private void printPolicy() {
        System.out.println("policy");
        for (int s: EnvironmentShip.POSITIONS) {
            System.out.println("s = "+s+", agent{mean,std} = " + agent.getMeanAndStdFromThetaVector(s));
            System.out.println("s = "+s+", trainer value = " + agent.getCritic().getValue(s));


        }
    }

     private static TrainerParameters getTrainerParameters() {
         return TrainerParameters.builder()
                 .nofEpisodes(NOF_EPISODES).nofStepsMax(NOF_STEPS_MAX)
                 .gamma(GAMMA).learningRateNonNeuralActor(LEARNING_RATE)
                 .build();
     }

}
