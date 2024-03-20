package policygradient.sink_the_ship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.sink_the_ship.AgentShip;
import policy_gradient_problems.environments.sink_the_ship.EnvironmentShip;
import policy_gradient_problems.environments.sink_the_ship.TrainerActorCriticShip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

 class TestTrainerActorCriticShip {

     static final double EXPECTED_ACTION0 = 0.2871;
     static final double EXPECTED_ACTION1 = 0.6711;
     static final double DELTA = 0.1;
    TrainerActorCriticShip trainer;
    AgentShip agent;

    @BeforeEach
     void init() {
        agent = AgentShip.newRandomStartStateDefaultThetas();
        var environment= new EnvironmentShip();
        createTrainer(environment);
    }

    private void createTrainer(EnvironmentShip environment) {
        trainer = TrainerActorCriticShip.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(1000).nofStepsMax(100).gamma(0.99d)
                        .learningRateActor(1e-3)
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

}
