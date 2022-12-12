package mcts_spacegame;

import lombok.SneakyThrows;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.helpers.NodeInfoHelper;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.model_mcts.MonteCarloSettings;
import mcts_spacegame.model_mcts.MonteCarloTreeCreator;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.State;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class Test_3times7gridWithCorridor {

    MonteCarloTreeCreator monteCarloTreeCreator;
    Environment environment;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new3times7GridWithTrapCorridor();
        environment = new Environment(spaceGrid);
        MonteCarloSettings settings = MonteCarloSettings.builder()
                .coefficientMaxAverageReturn(1) //only max
                .maxNofIterations(50)
                .nofSimulationsPerNode(10)
                .build();
        monteCarloTreeCreator = MonteCarloTreeCreator.builder()
                .environment(environment)
                .startState(new State(0, 0))
                .monteCarloSettings(settings)
                .build();
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y0() {
        NodeInterface nodeRoot = monteCarloTreeCreator.runIterations();
        TreeInfoHelper tih = new TreeInfoHelper(nodeRoot);
        tih.getNodesOnPathForActions(monteCarloTreeCreator.getActionsToSelected()).orElseThrow().forEach(System.out::println);
        doPrinting(tih, nodeRoot);

        Optional<NodeInterface> node52 = NodeInfoHelper.findNodeMatchingState(tih.getBestPath(), new State(4, 2));
        Assert.assertTrue(node52.isPresent());
    }

    @Test(expected = InterruptedException.class)
    public void iterateFromX2Y0() throws InterruptedException {
        monteCarloTreeCreator.setStartState(new State(2, 0));
        NodeInterface nodeRoot = monteCarloTreeCreator.runIterations();
        TreeInfoHelper tih = new TreeInfoHelper(nodeRoot);
        Assert.assertTrue(tih.isStateInAnyNode(new State(3, 0)));
        Assert.assertFalse(tih.isStateInAnyNode(new State(4, 0)));

        doPrinting(tih, nodeRoot);
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y0NoSimulations() {

        MonteCarloSettings settings = MonteCarloSettings.builder()
                .coefficientMaxAverageReturn(1) //only max
                .maxNofIterations(50)
                .nofSimulationsPerNode(0)
                .build();
        monteCarloTreeCreator = MonteCarloTreeCreator.builder()
                .environment(environment)
                .startState(new State(0, 0))
                .monteCarloSettings(settings)
                .build();

        NodeInterface nodeRoot = monteCarloTreeCreator.runIterations();
        TreeInfoHelper tih = new TreeInfoHelper(nodeRoot);
        tih.getNodesOnPathForActions(monteCarloTreeCreator.getActionsToSelected()).orElseThrow().forEach(System.out::println);
        doPrinting(tih, nodeRoot);

        Optional<NodeInterface> node52 = NodeInfoHelper.findNodeMatchingState(tih.getBestPath(), new State(4, 2));
        Assert.assertTrue(node52.isPresent());
    }

    private void doPrinting(TreeInfoHelper tih, NodeInterface nodeRoot) {
        System.out.println("nofNodesInTree = " + tih.nofNodesInTree());
        nodeRoot.printTree();
        tih.getBestPath().forEach(System.out::println);
    }

}
