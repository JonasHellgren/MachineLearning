package policygradient.cart_pole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.cart_pole.*;
import policy_gradient_problems.helpers.NeuralActorUpdaterCEMLoss;

import static org.junit.jupiter.api.Assertions.assertTrue;

 class TestTrainerMultiStepActorCriticPoleCEM {

    TrainerMultiStepActorCriticPole trainer;
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
        trainer = TrainerMultiStepActorCriticPole.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(100).nofStepsMax(100).gamma(0.95).stepHorizon(10)
                        .build())
                .actorUpdater(new NeuralActorUpdaterCEMLoss<>())
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
        double valAll0=agent.criticOut(StatePole.newUprightAndStill(parametersPole));
        double valBigAngle=agent.criticOut(StatePole.builder().angle(0.2).build());
        System.out.println("valAll0 = " + valAll0+", valBigAngle = " + valBigAngle);
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
