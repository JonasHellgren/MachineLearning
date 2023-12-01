package policygradient.sink_the_ship;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import policy_gradient_problems.the_problems.sink_the_ship.AgentShip;
import policy_gradient_problems.the_problems.sink_the_ship.EnvironmentShip;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestAgentShip {

    public static final int PROB_DIRECT_TO_TERMINAL = 0;
    public static final double DELTA = 0.1;
    public static final double MEAN_S0 = 0.3, MEAN_S1 = 0.5, THETA_STD0 = -2,THETA_STD1 = 0;
    public static final int NOF_SAMPLES = 1000;
    EnvironmentShip environment;
    AgentShip agent;

    @BeforeEach
    public void init() {
        environment = new EnvironmentShip();
        agent = AgentShip.newWithRandomStartStateAndGivenThetas(new double[]{MEAN_S0, THETA_STD0, MEAN_S1, THETA_STD1});
    }

    @ParameterizedTest   //state, mean, std
    @CsvSource({
            "0, 0.3,0.1", "1, 0.5,1.0"
    })
    public void whenState_thenCorrectMeanAndStd(ArgumentsAccessor arguments) {
        int s = arguments.getInteger(0);
        double mean = arguments.getDouble(1);
        double std = arguments.getDouble(2);

        var msPair = agent.getMeanAndStdFromThetaVector(s);

        System.out.println("msPair = " + msPair);

        assertEquals(msPair.getFirst(), mean, DELTA);
        assertEquals(msPair.getSecond(), std, DELTA);
    }

    @Test
    public void givenState0_whenManySamples_thenCorrect() {
        DescriptiveStatistics ds = getDescriptiveStatisticsForState(0);
        assertEquals(ds.getMean(), MEAN_S0, DELTA);
        assertEquals(ds.getStandardDeviation(), Math.exp(THETA_STD0), DELTA);
    }

    @Test
    public void givenState1_whenManySamples_thenCorrect() {
        DescriptiveStatistics ds = getDescriptiveStatisticsForState(1);
        assertEquals(ds.getMean(), MEAN_S1, DELTA);
        assertEquals(ds.getStandardDeviation(), Math.exp(THETA_STD1), DELTA);
    }

    @ParameterizedTest   //action, derMeanDes, derStdDes  (sign(derX) is used)
    @CsvSource({
            "0, 0,-1",
            "0.1, 1,-1",
            "-0.1, -1,-1",
            "2, 1,1"
    })
    public void givenState0_whenGradLogForActionMeanS1_thenCorrect(ArgumentsAccessor arguments) {
        int state = 0;

        double deltaAction = arguments.getDouble(0);
        double action=MEAN_S0+deltaAction;
        double derMeanDes = arguments.getDouble(1);
        double derStdDes = arguments.getDouble(2);

        var gradLog0=agent.calcGradLogVector(state,action);
        double derMean=gradLog0.getEntry(0);
        double derStd=gradLog0.getEntry(1);

        assertEquals(Math.signum(derMeanDes), Math.signum(derMean));
        assertEquals(Math.signum(derStdDes), Math.signum(derStd));
    }



    private DescriptiveStatistics getDescriptiveStatisticsForState(int state) {
        List<Double> actions=new ArrayList<>();
        for (int i = 0; i < NOF_SAMPLES; i++) {
            actions.add(agent.chooseAction(state));
        }
        DescriptiveStatistics ds=new DescriptiveStatistics();
        actions.forEach(ds::addValue);
        return ds;
    }




}
