package policygradient.cart_pole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.cart_pole.*;
import policy_gradient_problems.helpers.NeuralActorUpdaterPPOLoss;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TestTrainerMultiStepActorCriticLossPPO {

    TrainerMultiStepActorCriticPole trainer;
    AgentActorICriticPolePPO agent;
    EnvironmentPole environment;
    ParametersPole parametersPole=ParametersPole.newDefault();

    @BeforeEach
    void init() {
        environment = EnvironmentPole.newDefault();
        agent = AgentActorICriticPolePPO.newDefault(StatePole.newUprightAndStill(parametersPole));
        createTrainer(environment, agent);
    }

    private void createTrainer(EnvironmentPole environment, AgentActorICriticPolePPO agent) {
        trainer = TrainerMultiStepActorCriticPole.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(100).nofStepsMax(50).gamma(0.99)  //0.95
                        .stepHorizon(10)
                        .build())
                .actorUpdater(new NeuralActorUpdaterPPOLoss<>())
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
        StatePole uprightAndStill = StatePole.newUprightAndStill(parametersPole);
        double valAll0=agent.criticOut(uprightAndStill);
        double valBigAngleDot=agent.criticOut(uprightAndStill.copyWithAngleDot(0.2));
        System.out.println("valAll0 = " + valAll0+", valBigAngleDot = " + valBigAngleDot);
    }

    private int getNofSteps() {
        PoleAgentOneEpisodeRunner helper = PoleAgentOneEpisodeRunner.builder().environment(environment).agent(agent).build();
        return helper.runTrainedAgent(StatePole.newUprightAndStill(parametersPole));
    }

    private void printMemories() {
        for (int i = 0; i < 5 ; i++) {
            StatePole statePole = StatePole.newAllRandom(environment.getParameters());
            double valueCritic=agent.criticOut(statePole);

            agent.setState(statePole);
            var probs=agent.getActionProbabilities();
            System.out.println("state = "+statePole+", valueCritic = " + valueCritic+", probs = "+probs);
        }
    }

}
