package policygradient.cart_pole;

import common.ListUtils;
import common.RandUtils;
import common_dl4j.Dl4JUtil;
import org.junit.jupiter.api.*;
import policy_gradient_problems.the_problems.cart_pole.*;
import java.util.List;

public class TestNeuralActorMemoryPole {

    static NeuralActorMemoryPole actor;
    static  ParametersPole parametersPole;

    @BeforeAll
    public static void init() {
        parametersPole=ParametersPole.newDefault();
        actor= NeuralActorMemoryPole.newDefault(parametersPole);
        trainActor();
    }

    @Test
    public void whenAnyStates_thenSumProbabilitiesIsOne() {
        var probs=actor.getOutValue(StatePole.newAllRandom(parametersPole).asList());
        System.out.println("probs = " + probs);
        Assertions.assertEquals(1,ListUtils.sumList(probs),1e-3);
    }

    @Test
    public void whenTrained_thenHigherProbAction1IfPosAngle() {
        List<Double> probs = getActionProbabilitesForAngle(parametersPole.angleMax());
        System.out.println("probs pos a = " + probs);
        Assertions.assertTrue(probs.get(1)>probs.get(0));
    }

    @Test
    public void whenTrained_thenHigherProbAction0IfNegAngle() {
        List<Double> probs = getActionProbabilitesForAngle(-parametersPole.angleMax());
        System.out.println("probs neg a = " + probs);
        Assertions.assertTrue(probs.get(0)>probs.get(1));
    }


    private static List<Double> getActionProbabilitesForAngle(double angle) {
        StatePole state = StatePole.newAllRandom(parametersPole).newWithAngle(angle);
        return actor.getOutValue(state.asList());
    }


    static void trainActor() {
        for (int ei = 0; ei < 1000 ; ei++) {
            var state= StatePole.newAllRandom(parametersPole);
            List<Double> in = state.asList();
            int action=(state.angle()<0) ? 0 : 1;  //neg angle -> push left and vice vera
            double nofSteps= RandUtils.getRandomDouble(0,parametersPole.maxNofSteps()/10);
            List<Double> oneHotOut = Dl4JUtil.createListWithOneHotWithValue(EnvironmentPole.NOF_ACTIONS,action,nofSteps);
            actor.fit(in, oneHotOut);
        }
    }

}