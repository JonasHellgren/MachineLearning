package policygradient.cart_pole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.cart_pole.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTrainerMultiStepNeuralActorNeuralCriticPole {

    TrainerMultiStepNeuralActorNeuralCriticPole trainer;
    AgentNeuralActorNeuralCriticPole agent;
    EnvironmentPole environment;

    @BeforeEach
    public void init() {
        environment = EnvironmentPole.newDefault();
        agent = AgentNeuralActorNeuralCriticPole.newDefault(StatePole.newUprightAndStill());
        createTrainer(environment, agent);
    }

    private void createTrainer(EnvironmentPole environment, AgentNeuralActorNeuralCriticPole agent) {
        trainer = TrainerMultiStepNeuralActorNeuralCriticPole.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(100).nofStepsMax(100).gamma(0.95)
                        .stepHorizon(10)
                        //.relativeNofFitsPerBatch(0.5)
                        .build())
                .build();
    }

    @Test
    //@Disabled()
    public void whenTrained_thenManySteps() {
        printMemories();
        trainer.train();
        int nofSteps = getNofSteps();
        somePrinting(nofSteps);
        printMemories();
        assertTrue(nofSteps > 20);
    }

    private void somePrinting(int nofSteps) {
        System.out.println("nofSteps = " + nofSteps);
        double valAll0=agent.getCriticOut(StatePole.newUprightAndStill());
        double valBigAngle=agent.getCriticOut(StatePole.builder().angle(0.2).build());
        System.out.println("valAll0 = " + valAll0+", valBigAngle = " + valBigAngle);
    }

    private int getNofSteps() {
        PoleAgentOneEpisodeRunner helper = PoleAgentOneEpisodeRunner.builder().environment(environment).agent(agent).build();
        return helper.runTrainedAgent(StatePole.newUprightAndStill());
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
