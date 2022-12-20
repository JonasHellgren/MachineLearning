package mcts_classes;

import mcts_spacegame.classes.NodeValueMemory;
import mcts_spacegame.domains.models_space.ShipVariables;
import mcts_spacegame.domains.models_space.StateShip;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestNodeValueMemory {

    private static final double DELTA = 0.1;
    private static final int VALUE = 10;
    NodeValueMemory<ShipVariables> nodeValueMemory;

    @Before
    public void init() {
        nodeValueMemory= new NodeValueMemory<>();
    }

    @Test
    public void readNonDefinedState() {
        double value= nodeValueMemory.read(StateShip.newStateFromXY(0,0));
        Assert.assertEquals(NodeValueMemory.DEFAULT_VALUE,value, DELTA);
    }

    @Test
    public void readDefinedState() {
        nodeValueMemory.write(StateShip.newStateFromXY(0,0), VALUE);
        double value= nodeValueMemory.read(StateShip.newStateFromXY(0,0));
        System.out.println("value = " + value);
        System.out.println("nodeValueMemory = " + nodeValueMemory);
        Assert.assertEquals(VALUE,value, DELTA);
    }


}
