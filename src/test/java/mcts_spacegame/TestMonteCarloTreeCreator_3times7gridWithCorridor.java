package mcts_spacegame;

import lombok.SneakyThrows;
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

public class TestMonteCarloTreeCreator_3times7gridWithCorridor {

    MonteCarloTreeCreator monteCarloTreeCreator;
    Environment environment;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new3times7GridWithTrapCorridor();
        environment = new Environment(spaceGrid);
        monteCarloTreeCreator=MonteCarloTreeCreator.builder()
                .environment(environment)
                .startState(new State(0,0))
                .build();
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y0() {
        NodeInterface nodeRoot=monteCarloTreeCreator.runIterations();
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);

        System.out.println("monteCarloTreeCreator.getActionsToSelected() = " + monteCarloTreeCreator.getActionsToSelected());

        System.out.println("tih.getNodesOnPathForActions(monteCarloTreeCreator.getActionsToSelected()).orElseThrow() =");
        tih.getNodesOnPathForActions(monteCarloTreeCreator.getActionsToSelected()).orElseThrow().forEach(System.out::println);

        doPrinting(tih,nodeRoot);

        Optional<NodeInterface> node11= NodeInfoHelper.findNodeMatchingState(tih.getBestPath(), new State(1,1));
        Assert.assertTrue(node11.isPresent());
        Optional<NodeInterface> node52= NodeInfoHelper.findNodeMatchingState(tih.getBestPath(), new State(5,2));
        Assert.assertTrue(node52.isPresent());
    }

    private void doPrinting(TreeInfoHelper tih,NodeInterface nodeRoot) {
        System.out.println("nofNodesInTree = " + tih.nofNodesInTree());
        nodeRoot.printTree();
        tih.getBestPath().forEach(System.out::println);
    }

}
