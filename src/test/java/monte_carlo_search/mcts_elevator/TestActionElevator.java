package monte_carlo_search.mcts_elevator;

import monte_carlo_tree_search.domains.elevator.ActionElevator;
import monte_carlo_tree_search.interfaces.ActionInterface;
import org.junit.Assert;
import org.junit.Test;

public class TestActionElevator {


    @Test
    public void givenAction0DefaultRange_thenFine() {
        ActionInterface<Integer> actionElevator=ActionElevator.newValueDefaultRange(0);
        System.out.println("actionElevator = " + actionElevator);
        Assert.assertEquals(0,actionElevator.getValue().intValue());
    }

    @Test
    public void givenAction0SpecificRange_thenFine() {
        ActionInterface<Integer> actionElevator=ActionElevator.newValueSpecificRange(0,0,1);
        System.out.println("actionElevator = " + actionElevator);
        Assert.assertEquals(0,actionElevator.getValue().intValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenAction10SpecificRange_thenThrows() {
        ActionElevator.newValueSpecificRange(10,0,1);
    }

}
