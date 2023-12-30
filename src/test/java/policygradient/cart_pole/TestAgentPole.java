package policygradient.cart_pole;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.the_problems.cart_pole.AgentParamActorPole;
import policy_gradient_problems.the_problems.cart_pole.EnvironmentPole;
import policy_gradient_problems.the_problems.cart_pole.StatePole;
import policy_gradient_problems.the_problems.cart_pole.VariablesPole;

public class TestAgentPole {

    public static final double TOL = 1e-5;
    EnvironmentPole environment;
    AgentParamActorPole agent;

    @BeforeEach
    public void init() {
        environment = EnvironmentPole.newDefault();
        agent = AgentParamActorPole.newAllZeroStateDefaultThetas();
    }

    @Test
    public void whenAllStatesZero_thenCorrectActionProbs() {
        var actionProbs=agent.getActionProbabilities();

        System.out.println("actionProbs = " + actionProbs);

        Assertions.assertEquals(0.5,actionProbs.get(0));
        Assertions.assertEquals(0.5,actionProbs.get(1));

    }

    @Test
    public void whenAllStatesZero_thenCorrectGradLog() {
        var gradLogVector=agent.calcGradLogVector(agent.getState(), Action.ofInteger(0));

        System.out.println("gradLogVector = " + gradLogVector);

        Assertions.assertEquals(0d,gradLogVector.toArray()[0]);
        Assertions.assertEquals(0d,gradLogVector.toArray()[1]);

    }

    @Test
    public void whenAllStatesZeroExceptXIsOne_thenCorrectProbs() {
        agent.setState(StatePole.newFromVariables(VariablesPole.builder().angle(0).angleDot(0).x(1).xDot(0).build()));
        var actionProbs=agent.getActionProbabilities();

        System.out.println("actionProbs = " + actionProbs);

        Assertions.assertEquals(actionProbs.get(0), Math.exp(1) / (1 + Math.exp(1)), TOL);

    }

}
