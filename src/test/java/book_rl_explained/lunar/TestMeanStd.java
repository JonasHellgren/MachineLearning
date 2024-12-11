package book_rl_explained.lunar;

import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.agent.GradientMeanStd;
import book_rl_explained.lunar_lander.domain.agent.MeanStd;
import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestMeanStd {

    public static final int GRAD_MEAN_MAX = 1;
    public static final int GRAD_STD_MAX = 1;
    LunarProperties lunarProperties;
    AgentParameters agentParameters;

     @BeforeEach
      void init() {
        lunarProperties=LunarProperties.defaultProps();
        agentParameters = AgentParameters.defaultParams(lunarProperties)
                .withGradMeanMax(GRAD_MEAN_MAX).withGradStdMax(GRAD_STD_MAX);
      }

    @Test
    void testOfMethod() {
        double mean = 10.0;
        double std = 5.0;
        MeanStd meanStd = MeanStd.of(mean, std);
        assertEquals(mean, meanStd.mean());
        assertEquals(std, meanStd.std());
    }

    @Test
    void testEquality() {
        double mean = 10.0;
        double std = 5.0;
        MeanStd meanStd1 = new MeanStd(mean, std);
        MeanStd meanStd2 = new MeanStd(mean, std);
        assertEquals(meanStd1, meanStd2);
    }

    @Test
    void testCreateClipped() {
        GradientMeanStd gradMeanAndStd = GradientMeanStd.of(10.0, 20.0);
        GradientMeanStd clipped = gradMeanAndStd.clip(
                agentParameters.gradMeanMax(), agentParameters.gradStdMax());
        assertEquals(GRAD_MEAN_MAX, clipped.mean(), 0.0);
        assertEquals(GRAD_STD_MAX, clipped.std(), 0.0);
    }


}
