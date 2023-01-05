package mcts_cart_pole;

import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.domains.cart_pole.StateCartPole;
import org.junit.Assert;
import org.junit.Test;

public class TestCartPoleVariables {


    @Test
    public void shallBeEqual() {
        StateCartPole s1=StateCartPole.newAllStatesAsZero();
        CartPoleVariables v1= s1.getVariables().copy();
        CartPoleVariables v2= s1.getVariables().copy();

        Assert.assertTrue(v1.equals(v2));
    }

    @Test
    public void shallNoBeEqual() {
        StateCartPole s1=StateCartPole.newAllStatesAsZero();
        CartPoleVariables v1= s1.getVariables().copy();
        CartPoleVariables v2= s1.getVariables().copy();
        v2.theta= v2.theta+0.1;
        System.out.println("v1 = " + v1);
        System.out.println("v2 = " + v2);

        Assert.assertFalse(v1.equals(v2));
    }
}
