package mcts_spacegame;

import lombok.SneakyThrows;
import mcts_spacegame.environment.EnvironmentShip;
import mcts_spacegame.exceptions.StartStateIsTrapException;
import mcts_spacegame.helpers.NodeInfoHelper;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.model_mcts.MonteCarloSettings;
import mcts_spacegame.model_mcts.MonteCarloTreeCreator;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.StateShip;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class Test_3times7grid {
    MonteCarloTreeCreator monteCarloTreeCreator;
    EnvironmentShip environment;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new EnvironmentShip(spaceGrid);
        monteCarloTreeCreator=MonteCarloTreeCreator.builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0,0))
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

        Optional<NodeInterface> node11= NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(),StateShip.newStateFromXY(1,1));
        Assert.assertTrue(node11.isPresent());
        Optional<NodeInterface> node52= NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(),StateShip.newStateFromXY(5,2));
        Assert.assertTrue(node52.isPresent());

    }

    @SneakyThrows
    @Test(expected = StartStateIsTrapException.class)
    public void iterateFromX2Y0() {
        monteCarloTreeCreator.setStartState(StateShip.newStateFromXY(2,0));
        NodeInterface nodeRoot=monteCarloTreeCreator.runIterations();
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);

        doPrinting(tih,nodeRoot);

        Optional<NodeInterface> node11= NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(),StateShip.newStateFromXY(1,1));
        Assert.assertFalse(node11.isPresent());
        Optional<NodeInterface> node52= NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(),StateShip.newStateFromXY(5,2));
        Assert.assertFalse(node52.isPresent());
    }

    @SneakyThrows
    @Test
    public void iterateFromX1Y1() {
        monteCarloTreeCreator.setStartState(StateShip.newStateFromXY(1,1));
        NodeInterface nodeRoot=monteCarloTreeCreator.runIterations();
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);

        doPrinting(tih,nodeRoot);

     //   Optional<NodeInterface> node11= NodeInfoHelper.findNodeMatchingState(tih.getBestPath(), new State(5,2));
      //  Assert.assertTrue(node11.isPresent());
        Assert.assertTrue(tih.isStateInAnyNode(StateShip.newStateFromXY(2,0)));
    }

    @SneakyThrows
    @Test public void maxTreeDepth() {
        MonteCarloSettings settings= MonteCarloSettings.builder().maxTreeDepth(3).build();

        monteCarloTreeCreator=MonteCarloTreeCreator.builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0,0))
                .monteCarloSettings(settings)
                .build();
        NodeInterface nodeRoot=monteCarloTreeCreator.runIterations();
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);

        doPrinting(tih,nodeRoot);
        System.out.println("tih.maxDepth() = " + tih.maxDepth());

        Assert.assertEquals(3,tih.maxDepth());
    }


    private void doPrinting(TreeInfoHelper tih,NodeInterface nodeRoot) {
        System.out.println("nofNodesInTree = " + tih.nofNodesInTree());
        nodeRoot.printTree();
        tih.getBestPath().forEach(System.out::println);
    }

}
