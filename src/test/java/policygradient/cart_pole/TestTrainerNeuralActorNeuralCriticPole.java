package policygradient.cart_pole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.cart_pole.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

 class TestTrainerNeuralActorNeuralCriticPole {

    TrainerNeuralActorNeuralCriticPole trainer;
    AgentNeuralActorNeuralCriticPole agent;
    EnvironmentPole environment;

    @BeforeEach
     void init() {
        environment = EnvironmentPole.newDefault();
        agent = AgentNeuralActorNeuralCriticPole.newDefault(StatePole.newUprightAndStill());
        createTrainer(environment, agent);
    }

    private void createTrainer(EnvironmentPole environment, AgentNeuralActorNeuralCriticPole agent) {
        trainer = TrainerNeuralActorNeuralCriticPole.builder()
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
        PoleAgentOneEpisodeRunner helper = PoleAgentOneEpisodeRunner.builder()
                .environment(environment).agent(agent).build();
        int nofSteps = helper.runTrainedAgent(StatePole.newUprightAndStill());

        System.out.println("nofSteps = " + nofSteps);

        double valAll0=agent.getCriticOut(StatePole.newUprightAndStill());
        double valBigAngle=agent.getCriticOut(StatePole.builder().angle(0.2).build());

        System.out.println("valAll0 = " + valAll0);
        System.out.println("valBigAngle = " + valBigAngle);

        printMemories();

        assertTrue(valAll0>valBigAngle);
        assertTrue(nofSteps > 20);
    }

    private void printMemories() {
        for (int i = 0; i < 10 ; i++) {
            StatePole statePole = StatePole.newAllRandom(environment.getParameters());
            double valueCritic=agent.getCriticOut(statePole);

            agent.setState(statePole);
            var probs=agent.getActionProbabilities();
            System.out.println("state = "+statePole+", valueCritic = " + valueCritic+", probs = "+probs);
        }
    }

}
