package testPolicyGradientUpOrDown;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_upOrDown.domain.Agent;
import policy_gradient_upOrDown.domain.Environment;
import policy_gradient_upOrDown.domain.Trainer;

public class TestTrainer {


    Trainer trainer;
    Agent agent;

    @BeforeEach
    public void init() {
        Environment environment=Environment.newDefault();
        agent=Agent.newDefault();

        trainer= Trainer.builder()
                .environment(environment).agent(agent)
                .nofEpisodes(100).nofStepsMax(1).gamma(1d).learningRate(1e-2)
                .build();
    }

    @Test
    public void whenTrained_thenThetaCorrect() {

        trainer.train();

        System.out.println("theta = "+agent.getTheta());


    }

}
