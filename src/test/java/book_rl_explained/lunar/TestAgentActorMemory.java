package book_rl_explained.lunar;

import book_rl_explained.lunar_lander.domain.agent.ActorMemoryLunar;
import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.agent.MeanAndStd;
import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TestAgentActorMemory {

    private ActorMemoryLunar actorMemoryLunar;
    private LunarProperties lunarProperties;
    private AgentParameters agentParameters;

    @BeforeEach
    void setup() {
        lunarProperties = LunarProperties.defaultProps();
        agentParameters = AgentParameters.defaultProps(lunarProperties);
        actorMemoryLunar = ActorMemoryLunar.zeroWeights(agentParameters, lunarProperties);
    }

    @Test
    void testZeroWeights() {
        assertNotNull(actorMemoryLunar.getMemoryMean());
        assertNotNull(actorMemoryLunar.getMemoryLogStd());
        assertNotNull(actorMemoryLunar.getUpdaterMean());
        assertNotNull(actorMemoryLunar.getUpdaterLogStd());
    }


    @Test
    void testActorMeanAndStd() {
        var state = StateLunar.randomPosAndSpeed(lunarProperties);
        var meanAndStd = actorMemoryLunar.actorMeanAndStd(state);
        assertNotNull(meanAndStd);
        assertEquals(0.0, meanAndStd.mean());
        assertEquals(1.0, meanAndStd.std());  //e^0 = 1
    }


    @Test
    void testFit() {
        StateLunar state = StateLunar.randomPosAndSpeed(lunarProperties);
        double adv = 1.0;
        var grad = MeanAndStd.of(1.0, 2.0);
        var expStd0 = actorMemoryLunar.actorMeanAndStd(state);
        actorMemoryLunar.fit(state, adv, grad);
        var expStdNew = actorMemoryLunar.actorMeanAndStd(state);
        Assertions.assertTrue(expStdNew.mean() > expStd0.mean());
        Assertions.assertTrue(expStdNew.std() > expStd0.std());
    }


    @Test
    void testCreateClipped_MeanAboveUpperBound() {
        MeanAndStd meanAndStd = new MeanAndStd(20.0, 5.0);
        double gradMeanMax = 10.0;
        AgentParameters p = agentParameters.withGradMeanMax(gradMeanMax);
        MeanAndStd clipped = meanAndStd.createClipped(p);
        assertEquals(gradMeanMax, clipped.mean(), 0.01);
    }

    @Test
    void testCreateClipped_StdWithinBounds() {
        double mean = 5.0;
        var meanAndStd = new MeanAndStd(mean, 5.0);
        double gradMeanMax = 10.0;
        var p = agentParameters.withGradMeanMax(gradMeanMax);
        var clipped = meanAndStd.createClipped(p);
        assertEquals(mean, clipped.mean(), 0.01);
    }

    @Test
    void testCreateClipped_StdBelowLowerBound() {
        var meanAndStd = new MeanAndStd(10.0, -20.0);
        double gradStdMax = 10.0;
        var p = agentParameters.withGradStdMax(gradStdMax);
        var clipped = meanAndStd.createClipped(p);
        assertEquals(-gradStdMax, clipped.std(), 0.01);
    }

}
