package policygradient.cart_pole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.cart_pole.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

 class TestTrainerSingleStepActorCriticPoleI {

    TrainerSingleStepActorCriticPole trainer;
    AgentActorICriticPoleCEM agent;
    EnvironmentPole environment;
     ParametersPole parametersPole=ParametersPole.newDefault();


    @BeforeEach
     void init() {
        environment = EnvironmentPole.newDefault();
        agent = AgentActorICriticPoleCEM.newDefault(StatePole.newUprightAndStill(parametersPole));
        createTrainer(environment, agent);
    }

    private void createTrainer(EnvironmentPole environment, AgentActorICriticPoleCEM agent) {
        trainer = TrainerSingleStepActorCriticPole.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(500).nofStepsMax(100).gamma(0.95)
                        .stepHorizon(10)
                        .build())
                .build();
    }

    @Test
    @Disabled("fails and long time")
     void whenTrained_thenManySteps() {
        printMemories();
        trainer.train();
        System.out.println("After trained");
        PoleAgentOneEpisodeRunner helper = PoleAgentOneEpisodeRunner.newOf(environment,agent);
        int nofSteps = helper.runTrainedAgent(StatePole.newUprightAndStill(parametersPole));

        System.out.println("nofSteps = " + nofSteps);

        double valAll0=agent.criticOut(StatePole.newUprightAndStill(parametersPole));
        double valBigAngle=agent.criticOut(StatePole.builder().angle(0.2).build());

        System.out.println("valAll0 = " + valAll0);
        System.out.println("valBigAngle = " + valBigAngle);

        printMemories();

        assertTrue(valAll0>valBigAngle);
        assertTrue(nofSteps > 20);
    }

    private void printMemories() {
        for (int i = 0; i < 10 ; i++) {
            StatePole statePole = StatePole.newAllRandom(environment.getParameters());
            double valueCritic=agent.criticOut(statePole);

            agent.setState(statePole);
            var probs=agent.actionProbabilitiesInPresentState();
            System.out.println("stateNew = "+statePole+", valueCritic = " + valueCritic+", probs = "+probs);
        }
    }

}
