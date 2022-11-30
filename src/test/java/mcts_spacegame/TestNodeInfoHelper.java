package mcts_spacegame;

import mcts_spacegame.enums.Action;
import mcts_spacegame.helpers.NodeInfoHelper;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.State;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestNodeInfoHelper {

    List<NodeInterface> nodes;

    @Before
    public void init() {
        nodes=new ArrayList<>();

        nodes.add(NodeInterface.newNotTerminal(new State(0,0), Action.notApplicable));
        nodes.add(NodeInterface.newNotTerminal(new State(1,0), Action.still));
        nodes.add(NodeInterface.newNotTerminal(new State(2,0), Action.still));
        nodes.add(NodeInterface.newNotTerminal(new State(3,0), Action.still));

    }

    @Test
    public void nodeWithX0Y0IsPresent() {
        Assert.assertTrue(NodeInfoHelper.findNodeMatchingState(nodes,new State(0,0)).isPresent());
    }


    @Test
    public void nodeWithX10Y0IsNotPresent() {
        Assert.assertFalse(NodeInfoHelper.findNodeMatchingState(nodes,new State(10,0)).isPresent());
    }

}
