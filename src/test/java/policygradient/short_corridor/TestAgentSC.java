package policygradient.short_corridor;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import policy_gradient_problems.short_corridor.AgentSC;
import policy_gradient_problems.short_corridor.EnvironmentSC;

import java.util.ArrayList;
import java.util.List;

import static common.ArrayUtil.isDoubleArraysEqual;
import static common.ListUtils.toArray;
import static org.junit.jupiter.api.Assertions.*;

public class TestAgentSC {

    public static final int PROB_DIRECT_TO_TERMINAL = 0;
    public static final double DELTA_PROB = 0.1;
    EnvironmentSC environment;
    AgentSC agent;

    @BeforeEach
    public void init() {
        environment = new EnvironmentSC(PROB_DIRECT_TO_TERMINAL);
        agent = AgentSC.newWithRandomStartStateAndGivenThetas(new double[]{-10, 10, 0.5, 0.5, 10, -10,});
    }

    @ParameterizedTest   //obs state, p0, p1
    @CsvSource({
            "0, 0,1", "1, 0.5,0.5", "2, 1.0,0.0"
    })
    public void whenActionProbs_thenCorrect(ArgumentsAccessor arguments) {
        int s = arguments.getInteger(0);
        double p0 = arguments.getDouble(1);
        double p1 = arguments.getDouble(2);

        agent.setState(s);
        List<Double> actionProbs = agent.calcActionProbabilitiesInState(s);

        assertEquals(p0, actionProbs.get(0), DELTA_PROB);
        assertEquals(p1, actionProbs.get(1), DELTA_PROB);
    }


    @ParameterizedTest   //obs state,action, g0,g1,g2,g3,g4,g5
    @CsvSource({
            "0,0, 0.5,-0.5,0,0,0,0",
            "0,1, -0.5,0.5,0,0,0,0",
            "1,0, 0,0,0.5,-0.5,0,0",
            "2,0, 0,0,0,0,0.5,-0.5"

    })
    public void whenGradLog_thenCorrect(ArgumentsAccessor arguments) {
        int s = arguments.getInteger(0);
        int a = arguments.getInteger(1);
        List<Double> gradThetaDesired = getGradThetaDesired(arguments);
        agent = AgentSC.newRandomStartStateDefaultThetas();
        agent.setState(s);
        ArrayRealVector gradLogVector = agent.calcGradLogVector(s, a);
        assertTrue(isDoubleArraysEqual(toArray(gradThetaDesired), gradLogVector.toArray(), 0.1));
    }

    @ParameterizedTest   //obs state, action
    @CsvSource({
            "0, 1",
            "1, 12",
            "2, 0",
    })
    public void whenChooseAction_thenCorrect(ArgumentsAccessor arguments) {
        int s = arguments.getInteger(0);
        int a = arguments.getInteger(1);
        agent.setState(s);

        int choosenAction = agent.chooseAction(s);
        if (s == 1) {
            assertTrue(choosenAction == 0 || choosenAction == 1);
        } else {
            assertEquals(a, choosenAction);
        }
    }


    @NotNull
    private static List<Double> getGradThetaDesired(ArgumentsAccessor arguments) {
        List<Double> gradThetaDesired = new ArrayList<>();
        for (int i = 0; i < AgentSC.getThetaLength(); i++) {
            gradThetaDesired.add(arguments.getDouble(i + 2));
        }
        return gradThetaDesired;
    }

}
