package policygradient.two_armed_bandit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.twoArmedBandit.AgentBanditRealVector;
import policy_gradient_problems.the_problems.twoArmedBandit.EnvironmentBandit;
import policy_gradient_problems.the_problems.twoArmedBandit.TrainerBanditRealVector;

public class TestBanditTrainerRealVector {

    TrainerBanditRealVector trainer;
    AgentBanditRealVector agent;

    @BeforeEach
    public void init() {
        agent = AgentBanditRealVector.newDefault();
        var environment= EnvironmentBandit.newWithProbabilities(0.5,1.0);
        createTrainer(environment);
    }

    private void createTrainer(EnvironmentBandit environment) {
        trainer = TrainerBanditRealVector.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(1000).nofStepsMax(1).gamma(1d).learningRateActor(0.2).build())
                .build();
    }

    @Test
    public void givenEnvActionOneIsWellRewarded_whenTrained_thenCorrect() {
        trainer.train();
        printPolicy();
        Assertions.assertEquals(1, agent.chooseAction().asInt());
    }

    @Test
    public void givenEnvActionZeroIsWellRewarded_whenTrained_thenCorrect() {
        var environment= EnvironmentBandit.newWithProbabilities(0.5,0.1);
        createTrainer(environment);
        trainer.train();
        printPolicy();
        Assertions.assertEquals(0, agent.chooseAction().asInt());
    }


    private void printPolicy() {
        System.out.println("agent.piTheta() = " + agent.getActionProbabilities());
    }



}
