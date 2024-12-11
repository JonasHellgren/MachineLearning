package book_rl_explained.lunar;

import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.agent.GradientMeanStd;
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
    void testOfMethod() {
        double mean = 10.0;
        double std = 5.0;
        MeanAndStd meanAndStd = MeanAndStd.of(mean, std);
        assertEquals(mean, meanAndStd.mean());
        assertEquals(std, meanAndStd.std());
    }

    @Test
    void testEquality() {
        double mean = 10.0;
        double std = 5.0;
        MeanAndStd meanAndStd1 = new MeanAndStd(mean, std);
        MeanAndStd meanAndStd2 = new MeanAndStd(mean, std);
        assertEquals(meanAndStd1, meanAndStd2);
    }

    @Test
    void testCreateClipped() {
        GradientMeanStd gradMeanAndStd = GradientMeanStd.of(10.0, 20.0);
        GradientMeanStd clipped = gradMeanAndStd.clip(
                agentParameters.gradMeanMax(), agentParameters.gradStdMax());
        assertEquals(1.0, clipped.mean(), 0.0);
        assertEquals(1.0, clipped.std(), 0.0);
    }


}
