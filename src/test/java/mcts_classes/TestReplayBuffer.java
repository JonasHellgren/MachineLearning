package mcts_classes;

import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.domains.cart_pole.StateCartPole;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestReplayBuffer {
    private static final int BUFFER_SIZE = 10;
    ReplayBuffer<CartPoleVariables, Integer> buffer;
    @Before
    public void init() {
        StateCartPole allZeroState=StateCartPole.newAllStatesAsZero();
        buffer=new ReplayBuffer<>(BUFFER_SIZE);

        for (int i = 0; i < BUFFER_SIZE; i++) {
            buffer.addExperience(Experience.<CartPoleVariables, Integer>builder()
                .stateVariables(allZeroState.getVariables())
                .build());
        }
    }

    @Test
    public void printBuffer() {
        System.out.println("buffer = " + buffer);
    }

    @Test public void shallBePresent() {
        StateCartPole allZeroState=StateCartPole.newAllStatesAsZero();
        boolean isPresent=buffer.isExperienceWithStateVariablesPresentBeforeIndex(allZeroState.getVariables(),5);
        Assert.assertTrue(isPresent);
    }

    @Test public void shallNoBePresent() {
        StateCartPole positiveMaxState=StateCartPole.newAllPositiveMax();
        boolean isPresent=buffer.isExperienceWithStateVariablesPresentBeforeIndex(positiveMaxState.getVariables(),5);
        Assert.assertFalse(isPresent);
    }

}
