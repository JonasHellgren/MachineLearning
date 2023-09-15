package testPolicyGradientUpOrDown;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_upOrDown.domain.Agent;
import policy_gradient_upOrDown.domain.Environment;
import policy_gradient_upOrDown.domain.Trainer;

import java.util.function.Supplier;

public class TestTrainer {


    public static final double DELTA_PROB = 0.4;
    Trainer trainer;
    Agent agent;

    @BeforeEach
    public void init() {
        Environment environment = Environment.newDefault();
        agent = Agent.newDefault();

        trainer = Trainer.builder()
                .environment(environment).agent(agent)
                .nofEpisodes(1000).nofStepsMax(1).gamma(1d).learningRate(1e-2)
                .build();
    }

    @Test
    public void whenTrained_thenThetaCorrect() {
        Environment environment = Environment.builder()
                .rewardActionZero(() -> 1d).rewardActionOne(() -> -1d).build();
        trainer.setEnvironment(environment);

        trainer.train();

        double probOne=agent.getProbOne();
        int action = agent.chooseAction();

        System.out.println("agent.getTheta() = " + agent.getTheta());
        System.out.println("probOne = " + probOne);
        Assertions.assertEquals(0, probOne, DELTA_PROB);

        Assertions.assertEquals(0, action);
    }

    @Test
    public void givenOneIsGoodRewarded_whenTrained_thenThetaCorrect() {
        Environment environment = Environment.builder()
                .rewardActionZero(() -> -1d).rewardActionOne(() -> 1d).build();
        trainer.setEnvironment(environment);

        trainer.train();

        double probOne=agent.getProbOne();
        int action = agent.chooseAction();

        System.out.println("agent.getTheta() = " + agent.getTheta());
        System.out.println("probOne = " + probOne);
        Assertions.assertEquals(1, probOne, DELTA_PROB);
        Assertions.assertEquals(1, action);
    }

}
