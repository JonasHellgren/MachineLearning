package monte_carlo_search.mcts_classes;

import monte_carlo_tree_search.interfaces.MapMemoryInterface;
import monte_carlo_tree_search.models_and_support_classes.ValueMemoryHashMap;
import monte_carlo_tree_search.domains.models_space.ShipVariables;
import monte_carlo_tree_search.domains.models_space.StateShip;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestNodeValueMemory {

    private static final double DELTA = 0.1;
    private static final int VALUE = 10;
    MapMemoryInterface<ShipVariables> nodeValueMemory;

    @Before
    public void init() {
        nodeValueMemory= new ValueMemoryHashMap<>();
    }

    @Test
    public void whenReadNonDefinedState_thenDefault() {
        double value= nodeValueMemory.read(StateShip.newStateFromXY(0,0));
        Assert.assertEquals(ValueMemoryHashMap.DEFAULT_VALUE,value, DELTA);
    }

    @Test
    public void whenReadDefinedState_thenCorrect() {
        nodeValueMemory.write(StateShip.newStateFromXY(0,0), VALUE);
        double value= nodeValueMemory.read(StateShip.newStateFromXY(0,0));
        System.out.println("value = " + value);
        System.out.println("nodeValueMemory = " + nodeValueMemory);
        Assert.assertEquals(VALUE,value, DELTA);
    }


}
