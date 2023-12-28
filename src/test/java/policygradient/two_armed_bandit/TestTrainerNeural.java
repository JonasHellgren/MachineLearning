package policygradient.two_armed_bandit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.twoArmedBandit.*;

public class TestTrainerNeural {

    TrainerBanditNeural trainer;
    AgentBanditNeural agent;

    @BeforeEach
    public void init() {
        var environment= EnvironmentBandit.newWithProbabilities(0.0,1.0);
        createTrainer(environment);
        agent=trainer.getAgent();
    }

    private void createTrainer(EnvironmentBandit environment) {
        trainer = TrainerBanditNeural.builder()
                .environment(environment)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(100).nofStepsMax(1).gamma(1d).learningRateActor(1e-1).build())
                .build();
    }

    @Test
    public void givenEnvActionOneIsWellRewarded_whenTrained_thenCorrect() {
        trainer.train();
        printPolicy();
        Assertions.assertEquals(1, agent.chooseActionOld());
    }

    @Test
    public void givenEnvActionZeroIsWellRewarded_whenTrained_thenCorrect() {
        var environment= EnvironmentBandit.newWithProbabilities(1.0,0.0);
        createTrainer(environment);
        trainer.train();
        printPolicy();
        Assertions.assertEquals(0, agent.chooseActionOld());
    }

    private void printPolicy() {
        agent=trainer.getAgent();
        System.out.println("action probs() = " + agent.getActionProbabilities());
    }


}
