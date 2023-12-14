package policygradient.cart_pole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.cart_pole.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTrainerActorCriticPole {

    TrainerActorCriticPole trainer;
    AgentPole agent;
    EnvironmentPole environment;

    @BeforeEach
    public void init() {
        environment = EnvironmentPole.newDefault();
        agent = AgentPole.newRandomStartStateDefaultThetas(environment.getParameters());
        createTrainer(environment);
        trainer.train();
    }

    private void createTrainer(EnvironmentPole environment) {
        trainer = TrainerActorCriticPole.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(1000).nofStepsMax(100).gamma(0.99).stepHorizon(50).beta(1e-3).build())
                .build();
    }

    @Test
    public void whenTrained_thenManySteps() {
        PoleHelper helper = PoleHelper.builder().environment(environment).agent(agent).build();
        int nofSteps = helper.runTrainedAgent(StatePole.newUprightAndStill());

        System.out.println("nofSteps = " + nofSteps);

        NeuralMemoryPole memory=trainer.getMemory();
        double valAll0=memory.getOutValue(StatePole.newUprightAndStill().asList());
        double valBigAngle=memory.getOutValue(StatePole.builder().angle(0.2).build().asList());

        System.out.println("valAll0 = " + valAll0);
        System.out.println("valBigAngle = " + valBigAngle);

        for (int i = 0; i < 10 ; i++) {
            StatePole statePole = StatePole.newAllRandom(environment.getParameters());
            double valAllRandom=memory.getOutValue(statePole.asList());
            System.out.println("state = "+statePole+", valAllRandom = " + valAllRandom);
        }

        assertTrue(valAll0>valBigAngle);
        assertTrue(nofSteps > 50);
    }


}
