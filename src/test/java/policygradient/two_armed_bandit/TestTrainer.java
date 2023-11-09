package policygradient.two_armed_bandit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.twoArmedBandit.Agent;
import policy_gradient_problems.twoArmedBandit.Environment;
import policy_gradient_problems.twoArmedBandit.Trainer;

public class TestTrainer {

    Trainer trainer;
    Agent agent;

    @BeforeEach
    public void init() {
        agent = Agent.newDefault();
        var environment=Environment.newWithProbabilities(0.5,1.0);
        createTrainer(environment);
    }

    private void createTrainer(Environment environment) {
        trainer = Trainer.builder()
                .environment(environment)
                .agent(agent)
                .nofEpisodes(1000).nofStepsMax(1).gamma(1d).learningRate(1e-1)
                .build();
    }

    @Test
    public void givenEnvActionOneIsWellRewarded_whenTrained_thenCorrect() {
        trainer.train();
        printPolicy();
        Assertions.assertEquals(1, agent.chooseAction());
    }

    @Test
    public void givenEnvActionZeroIsWellRewarded_whenTrained_thenCorrect() {
        var environment=Environment.newWithProbabilities(0.5,0.1);
        createTrainer(environment);
        trainer.train();
        printPolicy();
        Assertions.assertEquals(0, agent.chooseAction());
    }


    private void printPolicy() {
        System.out.println("agent.piTheta() = " + agent.actionProbabilities());
    }


}
