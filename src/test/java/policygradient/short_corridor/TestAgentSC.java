package policygradient.short_corridor;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import policy_gradient_problems.environments.short_corridor.AgentParamActorSCHelper;
import policy_gradient_problems.environments.short_corridor.AgentParamActorTabCriticSC;
import policy_gradient_problems.environments.short_corridor.EnvironmentSC;
import policy_gradient_problems.environments.short_corridor.StateSC;

import java.util.ArrayList;
import java.util.List;

import static common.list_arrays.ArrayUtil.isDoubleArraysEqual;
import static common.list_arrays.ListUtils.toArray;
import static org.junit.jupiter.api.Assertions.*;

 class TestAgentSC {

     static final int PROB_DIRECT_TO_TERMINAL = 0;
     static final double DELTA_PROB = 0.1;
    EnvironmentSC environment;
    AgentParamActorTabCriticSC agent;

    @BeforeEach
     void init() {
        environment = new EnvironmentSC(PROB_DIRECT_TO_TERMINAL);
        agent = AgentParamActorTabCriticSC.newWithRandomStartStateAndGivenThetas(new double[]{-10, 10, 0.5, 0.5, 10, -10,});
    }

    @ParameterizedTest   //obs stateNew, p0, p1
    @CsvSource({
            "0, 0,1", "1, 0.5,0.5", "2, 1.0,0.0"
    })
     void whenActionProbs_thenCorrect(ArgumentsAccessor arguments) {
        int os = arguments.getInteger(0);
        double p0 = arguments.getDouble(1);
        double p1 = arguments.getDouble(2);

        List<Double> actionProbs = agent.getHelper().calcActionProbsInObsState(os);

        assertEquals(p0, actionProbs.get(0), DELTA_PROB);
        assertEquals(p1, actionProbs.get(1), DELTA_PROB);
    }


    @ParameterizedTest   //obs stateNew,action, g0,g1,g2,g3,g4,g5
    @CsvSource({
            "0,0, 0.5,-0.5,0,0,0,0",
            "0,1, -0.5,0.5,0,0,0,0",
            "1,0, 0,0,0.5,-0.5,0,0",
            "2,0, 0,0,0,0,0.5,-0.5"

    })
     void whenGradLog_thenCorrect(ArgumentsAccessor arguments) {
        int os = arguments.getInteger(0);
        int a = arguments.getInteger(1);
        List<Double> gradThetaDesired = getGradThetaDesired(arguments);
        agent = AgentParamActorTabCriticSC.newRandomStartStateDefaultThetas();
        ArrayRealVector gradLogVector = agent.getHelper().calcGradLogVector(os, a);
        assertTrue(isDoubleArraysEqual(toArray(gradThetaDesired), gradLogVector.toArray(), 0.1));
    }

    @ParameterizedTest   //real stateNew, action
    @CsvSource({
            "2, 1",
            "3, 1",
            "6, 0",

    })
     void whenChooseAction_thenCorrect(ArgumentsAccessor arguments) {
        int s = arguments.getInteger(0);
        int a = arguments.getInteger(1);
        agent.setState(StateSC.newFromRealPos(s));

        var choosenAction = agent.chooseAction();
        if (s == 3) {  //obs stateNew of 3 is 1, so best action is 0 or 1
            assertTrue(choosenAction.asInt() == 0 || choosenAction.asInt() == 1);
        } else {
            assertEquals(a, choosenAction.asInt());
        }
    }


    @NotNull
    private static List<Double> getGradThetaDesired(ArgumentsAccessor arguments) {
        List<Double> gradThetaDesired = new ArrayList<>();
        for (int i = 0; i < AgentParamActorSCHelper.getThetaLength(); i++) {
            gradThetaDesired.add(arguments.getDouble(i + 2));
        }
        return gradThetaDesired;
    }

}
