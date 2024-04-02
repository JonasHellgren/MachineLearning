package policygradient.two_armed_bandit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.twoArmedBandit.*;

 class TestBanditTrainerNeural {

    TrainerBanditNeuralActorCEM trainer;
    AgentBanditNeuralActor agent;

    @BeforeEach
     void init() {
        var environment= EnvironmentBandit.newWithProbabilities(0.0,1.0);
        createTrainer(environment);
        agent=trainer.getAgent();
    }

    private void createTrainer(EnvironmentBandit environment) {
        trainer = TrainerBanditNeuralActorCEM.builder()
                .environment(environment)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(100).nofStepsMax(1).gamma(1d).build())
                .build();
    }

    @Test
    //@Disabled("takes long time")
     void givenEnvActionOneIsWellRewarded_whenTrained_thenCorrect() {
        trainer.train();
        printPolicy();
        Assertions.assertEquals(1, agent.chooseAction().asInt());
    }

    @Test
    @Disabled("takes long time")
     void givenEnvActionZeroIsWellRewarded_whenTrained_thenCorrect() {
        var environment= EnvironmentBandit.newWithProbabilities(1.0,0.0);
        createTrainer(environment);
        trainer.train();
        printPolicy();
        Assertions.assertEquals(0, agent.chooseAction().asInt());  //can also use prob
    }

    private void printPolicy() {
        agent=trainer.getAgent();
        System.out.println("action probs() = " + agent.getActionProbabilities());
    }


}
