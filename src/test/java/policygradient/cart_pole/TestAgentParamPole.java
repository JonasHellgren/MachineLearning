package policygradient.cart_pole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.environments.cart_pole.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestAgentParamPole {

    static final double TOL = 1e-5;
    EnvironmentPole environment;
    AgentParamActorPole agent;
    ParametersPole parametersPole=ParametersPole.newDefault();


    @BeforeEach
     void init() {
        environment = EnvironmentPole.newDefault();
        agent = AgentParamActorPole.newAllZeroStateDefaultThetas(parametersPole);
    }

    @Test
     void whenAllStatesZero_thenCorrectActionProbs() {
        var actionProbs=agent.getActionProbabilities();
        System.out.println("actionProbs = " + actionProbs);
        assertEquals(0.5,actionProbs.get(0));
        assertEquals(0.5,actionProbs.get(1));

    }

    @Test
     void whenAllStatesZero_thenCorrectGradLog() {
        var gradLogVector=agent.calcGradLogVector(agent.getState(), Action.ofInteger(0));
        System.out.println("gradLogVector = " + gradLogVector);
        assertEquals(0d,gradLogVector.toArray()[0]);
        assertEquals(0d,gradLogVector.toArray()[1]);
    }

    @Test
     void whenAllStatesZeroExceptXIsOne_thenCorrectProbs() {
        agent.setState(StatePole.newFromVariables(
                VariablesPole.builder().angle(0).angleDot(0).x(1).xDot(0).build(),
                parametersPole));
        var actionProbs=agent.getActionProbabilities();
        System.out.println("actionProbs = " + actionProbs);
        assertEquals(actionProbs.get(0), Math.exp(1) / (1 + Math.exp(1)), TOL);
    }

}
