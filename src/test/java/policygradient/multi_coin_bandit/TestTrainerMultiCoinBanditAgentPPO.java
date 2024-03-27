package policygradient.multi_coin_bandit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.multicoin_bandit.EnvironmentMultiCoinBandit;
import policy_gradient_problems.environments.multicoin_bandit.MultiCoinBanditAgentPPO;
import policy_gradient_problems.environments.multicoin_bandit.TrainerMultiCoinBanditAgentPPO;

class TestTrainerMultiCoinBanditAgentPPO {

    TrainerMultiCoinBanditAgentPPO trainer;
    MultiCoinBanditAgentPPO agent;

    @BeforeEach
    void init() {
        var environment= EnvironmentMultiCoinBandit.newWithProbabilities(0.0,1.0);
        createTrainer(environment);
        agent=trainer.getAgent();
    }

    private void createTrainer(EnvironmentMultiCoinBandit environment) {
        trainer = TrainerMultiCoinBanditAgentPPO.builder()
                .environment(environment)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(1000).nofStepsMax(1).gamma(1d).build())
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
    //@Disabled("takes long time")
    void givenEnvActionZeroIsWellRewarded_whenTrained_thenCorrect() {
        var environment= EnvironmentMultiCoinBandit.newWithProbabilities(1.0,0.0);
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
