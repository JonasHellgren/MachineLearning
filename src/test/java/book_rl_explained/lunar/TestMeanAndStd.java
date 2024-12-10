package book_rl_explained.lunar;

import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.agent.MeanAndStd;
import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import com.google.common.collect.Range;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestMeanAndStd {

    LunarProperties lunarProperties;
    AgentParameters agentParameters;

     @BeforeEach
      void init() {
        lunarProperties=LunarProperties.defaultProps();
        agentParameters = AgentParameters.defaultProps(lunarProperties)
                .withGradMeanMax(1).withGradStdMax(1)
                .withRangeMean(Range.closed(-1d,1d))
                .withRangeLogStd(Range.closed(0d,1d));
      }

    @Test
    void testCreateClipped() {
        MeanAndStd gradMeanAndStd = new MeanAndStd(10.0, 20.0);
        MeanAndStd clipped = gradMeanAndStd.createClipped(agentParameters);
        assertEquals(1.0, clipped.mean(), 0.0);
        assertEquals(1.0, clipped.std(), 0.0);
    }

    @Test
    void testZeroGradIfValueNotInRange() {
        MeanAndStd grad = new MeanAndStd(10.0, 20.0);
        MeanAndStd meanAndStd = new MeanAndStd(5.0, 5.0);
        MeanAndStd result = meanAndStd.zeroGradIfValueNotInRange(grad, meanAndStd, agentParameters);
        assertEquals(0.0, result.mean(), 0.0);
        assertEquals(0.0, result.std(), 0.0);
    }

    @Test
    void testZeroGradIfValueInRange() {
        MeanAndStd grad = new MeanAndStd(1.0, 1.0);
        MeanAndStd meanAndStd = new MeanAndStd(1.0, 1.0);
        MeanAndStd result = meanAndStd.zeroGradIfValueNotInRange(grad, meanAndStd, agentParameters);
        assertEquals(1.0, result.mean(), 0.0);
        assertEquals(1.0, result.std(), 0.0);
    }

}
