package policygradient.cart_pole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.cart_pole.*;
import policy_gradient_problems.helpers.NeuralActorUpdaterCrossEntropyLoss;

import static org.junit.jupiter.api.Assertions.assertTrue;

 class TestTrainerMultiStepActorCriticPoleEntropyLoss {

    TrainerMultiStepActorCriticPole trainer;
    AgentNeuralActorNeuralCriticPoleEntropyLoss agent;
    EnvironmentPole environment;
    ParametersPole parametersPole=ParametersPole.newDefault();


    @BeforeEach
     void init() {
        environment = EnvironmentPole.newDefault();
        agent = AgentNeuralActorNeuralCriticPoleEntropyLoss.newDefault(StatePole.newUprightAndStill(parametersPole));
        createTrainer(environment, agent);
    }

    private void createTrainer(EnvironmentPole environment, AgentNeuralActorNeuralCriticPoleEntropyLoss agent) {
        trainer = TrainerMultiStepActorCriticPole.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(100).nofStepsMax(100).gamma(0.95)
                        .stepHorizon(10)
                        //.relativeNofFitsPerBatch(0.5)
                        .build())
                .actorUpdater(new NeuralActorUpdaterCrossEntropyLoss<>())
                .build();
    }

    @Test
    @Disabled("Long time")
     void whenTrained_thenManySteps() {
        printMemories();
        trainer.train();
        int nofSteps = getNofSteps();
        somePrinting(nofSteps);
        printMemories();
         System.out.println("nofSteps = " + nofSteps);
         assertTrue(nofSteps > 20);
    }

    private void somePrinting(int nofSteps) {
        System.out.println("nofSteps = " + nofSteps);
        double valAll0=agent.getCriticOut(StatePole.newUprightAndStill(parametersPole));
        double valBigAngle=agent.getCriticOut(StatePole.builder().angle(0.2).build());
        System.out.println("valAll0 = " + valAll0+", valBigAngle = " + valBigAngle);
    }

    private int getNofSteps() {
        PoleAgentOneEpisodeRunner helper = PoleAgentOneEpisodeRunner.builder().environment(environment).agent(agent).build();
        return helper.runTrainedAgent(StatePole.newUprightAndStill(parametersPole));
    }

    private void printMemories() {
        for (int i = 0; i < 5 ; i++) {
            StatePole statePole = StatePole.newAllRandom(environment.getParameters());
            double valueCritic=agent.getCriticOut(statePole);

            agent.setState(statePole);
            var probs=agent.getActionProbabilities();
            System.out.println("state = "+statePole+", valueCritic = " + valueCritic+", probs = "+probs);
        }
    }


}
