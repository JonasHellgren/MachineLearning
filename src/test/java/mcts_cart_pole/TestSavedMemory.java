package mcts_cart_pole;

import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.domains.cart_pole.StateCartPole;
import monte_carlo_tree_search.interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import monte_carlo_tree_search.domains.cart_pole.CartPoleStateValueMemory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestSavedMemory {
    private static final String FILE = "networks/cartPoleStateValue.nnet";
    private static final int DELTA = 5;
    private static final int EXPECTED_AT_ALL_ZERO = 100;
    private static final int EXPECTED_ALL_MAX_POS = 0;

    NetworkMemoryInterface<CartPoleVariables,Integer> memory;

    @Before
    public void init() {
        memory= new CartPoleStateValueMemory<>();
        memory.load(FILE);
    }

    @Test
    public void allZeroGivesValueApprox100() {
        StateInterface<CartPoleVariables> stateZero=StateCartPole.newAllStatesAsZero();
        double valueAtAllZero=memory.read(stateZero);
        System.out.println("valueAtAllZero = " + valueAtAllZero);
        Assert.assertEquals(EXPECTED_AT_ALL_ZERO,valueAtAllZero, DELTA);
    }


    @Test
    public void allMaxPositiveGivesValueApprox0() {
        StateInterface<CartPoleVariables> allMaxPositive=StateCartPole.newAllPositiveMax();
        double valueAllMaxPositive=memory.read(allMaxPositive);
        System.out.println("valueAllMaxPositive = " + valueAllMaxPositive);
        Assert.assertEquals(EXPECTED_ALL_MAX_POS,valueAllMaxPositive, DELTA);
    }

}
