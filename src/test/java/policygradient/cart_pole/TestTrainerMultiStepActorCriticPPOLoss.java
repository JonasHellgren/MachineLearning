package policygradient.cart_pole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.cart_pole.*;
import policy_gradient_problems.helpers.NeuralActorUpdaterCrossEntropyLoss;
import policy_gradient_problems.helpers.NeuralActorUpdaterCrossPPOLoss;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TestTrainerMultiStepActorCriticPPOLoss {

    TrainerMultiStepActorCriticPole trainer;
    AgentNeuralActorNeuralCriticPolePPO agent;
    EnvironmentPole environment;
    ParametersPole parametersPole=ParametersPole.newDefault();

    @BeforeEach
    void init() {
        environment = EnvironmentPole.newDefault();
        agent = AgentNeuralActorNeuralCriticPolePPO.newDefault(StatePole.newUprightAndStill(parametersPole));
        createTrainer(environment, agent);
    }

    private void createTrainer(EnvironmentPole environment, AgentNeuralActorNeuralCriticPolePPO agent) {
        trainer = TrainerMultiStepActorCriticPole.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(100).nofStepsMax(50).gamma(0.99)  //0.95
                        .stepHorizon(10)
                        .build())
                .actorUpdater(new NeuralActorUpdaterCrossPPOLoss<>())
                .build();
    }

    @Test
        // @Disabled("Long time")
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
        StatePole uprightAndStill = StatePole.newUprightAndStill(parametersPole);
        double valAll0=agent.getCriticOut(uprightAndStill);
        double valBigAngleDot=agent.getCriticOut(uprightAndStill.copyWithAngleDot(0.2));
        System.out.println("valAll0 = " + valAll0+", valBigAngleDot = " + valBigAngleDot);
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
