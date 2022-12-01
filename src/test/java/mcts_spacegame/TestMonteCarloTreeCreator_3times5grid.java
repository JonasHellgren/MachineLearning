package mcts_spacegame;

import mcts_spacegame.environment.Environment;
import mcts_spacegame.helpers.NodeInfoHelper;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.model_mcts.MonteCarloTreeCreator;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.State;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class TestMonteCarloTreeCreator_3times5grid {

    MonteCarloTreeCreator monteCarloTreeCreator;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new3times7Grid();
        Environment environment = new Environment(spaceGrid);
        monteCarloTreeCreator=MonteCarloTreeCreator.builder()
                .environment(environment)
                .startState(new State(0,0))
                .build();
    }

    @Test
    public void iterateFromX2Y0() {
        NodeInterface nodeRoot=monteCarloTreeCreator.doMCTSIterations();
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);

        doPrinting(tih,nodeRoot);

      //  Optional<NodeInterface> node12= NodeInfoHelper.findNodeMatchingState(tih.getBestPath(C_FOR_BEST_PATH), new State(2,0));
      //  Assert.assertTrue(node12.isPresent());
    }

    private void doPrinting(TreeInfoHelper tih,NodeInterface nodeRoot) {
        System.out.println("nofNodesInTree = " + tih.nofNodesInTree());
        nodeRoot.printTree();
        tih.getBestPath(0).forEach(System.out::println);
    }

}
