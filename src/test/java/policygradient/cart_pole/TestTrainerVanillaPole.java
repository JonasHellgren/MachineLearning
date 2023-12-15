package policygradient.cart_pole;

import common.Counter;
import org.junit.jupiter.api.*;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.cart_pole.*;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class TestTrainerVanillaPole {

    TrainerVanillaPole trainer;
    AgentPole agent;
    EnvironmentPole environment;

    @BeforeEach
    public void init() {
        environment= EnvironmentPole.newDefault();
        agent = AgentPole.newRandomStartStateDefaultThetas(environment.getParameters());
        createTrainer(environment);
    }

    private void createTrainer(EnvironmentPole environment) {
        trainer = TrainerVanillaPole.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(2000).nofStepsMax(100).gamma(0.99).learningRateActor(2e-3)
                        .build())
                .build();
    }

    @Test
    public void whenTrained_thenManySteps() {
        trainer.train();
        System.out.println("agent.getThetaVector() = " + agent.getThetaVector());

        PoleHelper helper= PoleHelper.builder().environment(environment).agent(agent).build();

        int nofSteps= helper.runTrainedAgent(StatePole.newUprightAndStill());
        System.out.println("nofSteps = " + nofSteps);

        assertTrue(nofSteps>50);

    }




}
