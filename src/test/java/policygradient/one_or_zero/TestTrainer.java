package policygradient.one_or_zero;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.zeroOrOne.domain.Agent;
import policy_gradient_problems.zeroOrOne.domain.Environment;
import policy_gradient_problems.zeroOrOne.domain.Trainer;

public class TestTrainer {
    public static final double DELTA_PROB = 0.4;
    public static final Environment DUMMY_ENV = Environment.newActionZeroIsGood();
    Trainer trainer;
    Agent agent;

    @BeforeEach
    public void init() {
        agent = Agent.newDefault();
        trainer = Trainer.builder()
                .environment(DUMMY_ENV)
                .agent(agent)
                .nofEpisodes(1000).nofStepsMax(10).gamma(1d).learningRate(1e-2)
                .build();
    }

    @Test
    public void givenEnvActionZeroIsWellRewarded_whenTrained_thenCorrectProbabilityActionOneAndAction() {
        trainer.setEnvironment(Environment.newActionZeroIsGood());
        trainer.train();
        Assertions.assertEquals(0, agent.getProbOne(), DELTA_PROB);
        Assertions.assertEquals(0, agent.chooseAction());
    }

    @Test
    public void givenEnvActionOneIsWellRewarded_whenTrained_thenCorrectProbabilityActionOneAndAction() {
        trainer.setEnvironment(Environment.newActionOneIsGood());
        trainer.train();
        Assertions.assertEquals(1, agent.getProbOne(), DELTA_PROB);
        Assertions.assertEquals(1, agent.chooseAction());
    }
}
