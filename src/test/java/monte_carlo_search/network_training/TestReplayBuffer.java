package monte_carlo_search.network_training;

import common.list_arrays.ListUtils;
import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.domains.cart_pole.StateCartPole;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.stream.Collectors;

public class TestReplayBuffer {
    private static final int BUFFER_SIZE = 10;
    private static final double TOL = 0.1;
    ReplayBuffer<CartPoleVariables, Integer> buffer;
    @Before
    public void init() {
        StateCartPole allZeroState=StateCartPole.newAllStatesAsZero();
        buffer=new ReplayBuffer<>(BUFFER_SIZE);

        for (int i = 0; i < BUFFER_SIZE; i++) {
            CartPoleVariables vars=allZeroState.getVariables().copy();
            vars.theta=i;
            buffer.addExperience(Experience.<CartPoleVariables, Integer>builder()
                .stateVariables(vars)
                .build());
        }
    }

    @Test
    public void printBuffer() {
        System.out.println("buffer = " + buffer);
    }

    @Test public void givenBuffer_whenZeroStates_thenShallBePresent() {
        StateCartPole allZeroState=StateCartPole.newAllStatesAsZero();
        boolean isPresent=buffer.isExperienceWithStateVariablesPresentBeforeIndex(allZeroState.getVariables(),5);
        Assert.assertTrue(isPresent);
    }

    @Test public void givenBuffer_whenMaxValueStates_thenShallNotBePresent() {
        StateCartPole positiveMaxState=StateCartPole.newAllPositiveMax();
        boolean isPresent=buffer.isExperienceWithStateVariablesPresentBeforeIndex(positiveMaxState.getVariables(),5);
        Assert.assertFalse(isPresent);
    }

    @Test public void whenRemoveRandomWhenFull_thenRemoved() {
        StateCartPole positiveMaxState=StateCartPole.newAllPositiveMax();
        buffer.addExperience(Experience.<CartPoleVariables, Integer>builder()
                .stateVariables(positiveMaxState.getVariables().copy())
                .build());

        System.out.println("buffer = " + buffer);

        List<Double> doubleList0to9=ListUtils.createDoubleListStartEndStep(0,BUFFER_SIZE-1,1);
        List<Double> thetas=buffer.getBuffer().stream().map(e -> e.stateVariables.theta).collect(Collectors.toList());

        System.out.println("doubleList0to9 = " + doubleList0to9);
        System.out.println("thetas = " + thetas);

        Assert.assertNotEquals(doubleList0to9, thetas);
        Assert.assertFalse(ListUtils.isDoubleArraysEqual(ListUtils.toArray(doubleList0to9),ListUtils.toArray(thetas), TOL));
    }





}
