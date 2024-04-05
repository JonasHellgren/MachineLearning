package policygradient.two_armed_bandit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.twoArmedBandit.AgentBanditParamActor;
import policy_gradient_problems.environments.twoArmedBandit.EnvironmentBandit;
import policy_gradient_problems.environments.twoArmedBandit.TrainerBanditParamActor;

 class TestBanditTrainerRealVector {

    TrainerBanditParamActor trainer;
    AgentBanditParamActor agent;

    @BeforeEach
     void init() {
        agent = AgentBanditParamActor.newDefault();
        var environment= EnvironmentBandit.newWithProbabilities(0.5,1.0);
        createTrainer(environment);
    }

    private void createTrainer(EnvironmentBandit environment) {
        trainer = TrainerBanditParamActor.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(1000).nofStepsMax(1).gamma(1d).learningRateNonNeuralActor(0.2).build())
                .build();
    }

    @Test
     void givenEnvActionOneIsWellRewarded_whenTrained_thenCorrect() {
        trainer.train();
        printPolicy();
        Assertions.assertEquals(1, agent.chooseAction().asInt());
    }

    @Test
     void givenEnvActionZeroIsWellRewarded_whenTrained_thenCorrect() {
        var environment= EnvironmentBandit.newWithProbabilities(0.5,0.1);
        createTrainer(environment);
        trainer.train();
        printPolicy();
        Assertions.assertEquals(0, agent.chooseAction().asInt());
    }


    private void printPolicy() {
        System.out.println("agent.piTheta() = " + agent.actionProbabilitiesInPresentState());
    }



}
