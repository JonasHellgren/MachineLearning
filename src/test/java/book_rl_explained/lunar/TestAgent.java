package book_rl_explained.lunar;

import book_rl_explained.lunar_lander.domain.agent.AgentI;
import book_rl_explained.lunar_lander.domain.agent.AgentLunar;
import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.agent.MeanAndStd;
import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestAgent {

    public static final int ADV = 1;
    AgentI agent;
    LunarProperties lunarProperties;
    StateLunar stateRandomPosAndSpeed, state0and0;

    @BeforeEach
    void init() {
        lunarProperties = LunarProperties.defaultProps();
        var agentParameters = AgentParameters.defaultProps(lunarProperties);
        agent = AgentLunar.zeroWeights(agentParameters, lunarProperties);
        stateRandomPosAndSpeed = StateLunar.randomPosAndSpeed(lunarProperties);
        state0and0 = StateLunar.of(0, 0);
    }


    @Test
    void testReadCritic() {
        double value = agent.readCritic(stateRandomPosAndSpeed);
        assertEquals(0.0, value, 0.0);
    }

    @Test
    void givenSameActionAsActorMean_thenCorrectGradientMeanAndLogStd() {
        double action = agent.readActor(stateRandomPosAndSpeed).mean();
        MeanAndStd grad = agent.gradientMeanAndLogStd(stateRandomPosAndSpeed, action);
        Assertions.assertEquals(0.0, grad.mean(), 0.0);
        Assertions.assertEquals(-ADV, grad.std(), 0.0);
    }

    @Test
    void givenLargerActionAsActorMean_thenCorrectGradientMeanAndLogStd() {
        double action = agent.readActor(stateRandomPosAndSpeed).mean() + 5;
        MeanAndStd grad = agent.gradientMeanAndLogStd(stateRandomPosAndSpeed, action);
        Assertions.assertTrue(grad.mean() > 0.0);
        Assertions.assertTrue(grad.std() > 0.0);
    }

    @Test
    void givenSmallerActionAsActorMean_thenCorrectGradientMeanAndLogStd() {
        double action = agent.readActor(stateRandomPosAndSpeed).mean() - 5;
        MeanAndStd grad = agent.gradientMeanAndLogStd(stateRandomPosAndSpeed, action);
        Assertions.assertTrue(grad.mean() < 0.0);
        Assertions.assertTrue(grad.std() > 0.0);
    }

    @Test
    void whenChooseAction_thenActionCloseToZero() {
        double action = agent.chooseAction(stateRandomPosAndSpeed);
        double std = agent.readActor(stateRandomPosAndSpeed).std();
        assertEquals(0.0, action, std * 3);
    }


    @Test
    void testFitActorWithNegativeAction() {
        double action0 = agent.readActor(state0and0).mean();
        int actionApplied = -1;
        agent.fitActor(state0and0, actionApplied, ADV);
        double action = agent.readActor(state0and0).mean();
        Assertions.assertTrue(action < action0);
    }


    @Test
    void testFitActorWithPositiveAction() {
        double action0 = agent.readActor(state0and0).mean();
        int actionApplied = 1;
        agent.fitActor(state0and0, actionApplied, ADV);
        double action = agent.readActor(state0and0).mean();
        Assertions.assertTrue(action > action0);
    }


    @Test
    void testFitCritic() {
        double value0 = agent.readCritic(state0and0);
        int valTarget = 10;
        agent.fitCritic(state0and0, valTarget);
        double value = agent.readCritic(state0and0);
        Assertions.assertTrue(value > value0);
    }

    @Test
    void fitCriticOneState_thenValueOtherStateNotAffected() {
        double value0 = agent.readCritic(state0and0);
        int valTarget = 10;
        agent.fitCritic(state0and0, valTarget);
        double value = agent.readCritic(state0and0);
        double valueOther = agent.readCritic(StateLunar.of(10, 0));
        Assertions.assertTrue(value > value0);
        Assertions.assertEquals(value0, valueOther, 1e-4);
    }


}
