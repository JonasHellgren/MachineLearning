package book_rl_explained.lunar;

import book_rl_explained.lunar_lander.domain.agent.ActorMemoryLunar;
import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.agent.GradientMeanStd;
import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TestAgentActorMemory {

    private ActorMemoryLunar actorMemoryLunar, actorMemoryLunarOneForStd;
    private LunarProperties lunarProperties;
    private AgentParameters agentParameters;

    @BeforeEach
    void setup() {
        lunarProperties = LunarProperties.defaultProps();
        agentParameters = AgentParameters.defaultParams(lunarProperties);
        actorMemoryLunar = ActorMemoryLunar.create(agentParameters, lunarProperties);
        actorMemoryLunarOneForStd = ActorMemoryLunar.create(agentParameters.withInitWeightLogStd(1), lunarProperties);
    }

    @Test
    void testZeroWeights() {
        assertNotNull(actorMemoryLunar.getMemoryMean());
        assertNotNull(actorMemoryLunar.getMemoryLogStd());
    }


    @Test
    void testActorMeanAndStd() {
        for (int i = 0; i < 100; i++) {
            var state = StateLunar.randomPosAndSpeed(lunarProperties);
            var meanAndStd = actorMemoryLunar.actorMeanAndStd(state);
            assertNotNull(meanAndStd);
            assertEquals(0.0, meanAndStd.mean());
            assertEquals(1.0, meanAndStd.std());  //e^0 = 1
        }
    }

    @Test
    void testActorMeanAndStd_oneForStd() {
        for (int i = 0; i < 100; i++) {
            var state = StateLunar.randomPosAndSpeed(lunarProperties);
            var meanAndStd = actorMemoryLunarOneForStd.actorMeanAndStd(state);
            assertNotNull(meanAndStd);
            assertEquals(0.0, meanAndStd.mean());
            assertEquals(Math.exp(1), meanAndStd.std(),0.01);  //e^0 = 1
        }
    }


    @Test
    void testFit() {
        StateLunar state = StateLunar.randomPosAndSpeed(lunarProperties);
        double adv = 1.0;
        var grad = GradientMeanStd.of(1.0, 2.0);
        var expStd0 = actorMemoryLunar.actorMeanAndStd(state);
        actorMemoryLunar.fit(state, adv, grad);
        var expStdNew = actorMemoryLunar.actorMeanAndStd(state);
        Assertions.assertTrue(expStdNew.mean() > expStd0.mean());
        Assertions.assertTrue(expStdNew.std() > expStd0.std());
    }


    @Test
    void testCreateClipped_MeanAboveUpperBound() {
        GradientMeanStd meanAndStd = GradientMeanStd.of(20.0, 5.0);
        double gradMeanMax = 10.0;
        AgentParameters p = agentParameters.withGradMeanMax(gradMeanMax);
        var clipped = meanAndStd.clip(p.gradMeanMax(), p.gradStdMax());
        assertEquals(gradMeanMax, clipped.mean(), 0.01);
    }

    @Test
    void testCreateClipped_StdWithinBounds() {
        double mean = 5.0;
        var meanAndStd = GradientMeanStd.of(mean, 5.0);
        double gradMeanMax = 10.0;
        var p = agentParameters.withGradMeanMax(gradMeanMax);
        var clipped = meanAndStd.clip(p.gradMeanMax(), p.gradStdMax());
        assertEquals(mean, clipped.mean(), 0.01);
    }

    @Test
    void testCreateClipped_StdBelowLowerBound() {
        var meanAndStd = GradientMeanStd.of(10.0, -20.0);
        double gradStdMax = 10.0;
        var p = agentParameters.withGradStdMax(gradStdMax);
        var clipped = meanAndStd.clip(p.gradMeanMax(), p.gradStdMax());
        assertEquals(-gradStdMax, clipped.std(), 0.01);
    }

}
