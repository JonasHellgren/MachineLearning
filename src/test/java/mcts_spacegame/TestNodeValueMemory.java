package mcts_spacegame;

import mcts_spacegame.model_mcts.NodeValueMemory;
import mcts_spacegame.models_space.StateShip;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestNodeValueMemory {

    private static final double DELTA = 0.1;
    private static final int VALUE = 10;
    NodeValueMemory nodeValueMemory;

    @Before
    public void init() {
        nodeValueMemory=new NodeValueMemory();
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
        Assert.assertEquals(VALUE,value, DELTA);
    }


}
