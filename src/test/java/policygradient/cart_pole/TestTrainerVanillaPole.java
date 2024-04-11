package policygradient.cart_pole;

import org.junit.jupiter.api.*;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.cart_pole.*;

import static org.junit.jupiter.api.Assertions.assertTrue;


 class TestTrainerVanillaPole {

    TrainerParamActorPole trainer;
    AgentParamActorPole agent;
    EnvironmentPole environment;
     ParametersPole parametersPole=ParametersPole.newDefault();


    @BeforeEach
     void init() {
        environment= EnvironmentPole.newDefault();
        agent = AgentParamActorPole.newRandomStartStateDefaultThetas(environment.getParameters());
        createTrainer(environment);
    }

    private void createTrainer(EnvironmentPole environment) {
        trainer = TrainerParamActorPole.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(2000).nofStepsMax(100).gamma(0.99)
                        .build())
                .build();
    }

    @Test
     void whenTrained_thenManySteps() {
        trainer.train();
        var helper = PoleAgentOneEpisodeRunner.newOf(environment,agent);
        int nofSteps= helper.runTrainedAgent(StatePole.newUprightAndStill(parametersPole));
        assertTrue(nofSteps>50);

    }




}
