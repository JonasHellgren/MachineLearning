package freeze;

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

public class Test_3times7gridWithCorridor {

    private static final int MAX_NOF_ITERATIONS = 500;
    MonteCarloTreeCreator monteCarloTreeCreator;
    EnvironmentShip environment;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new3times7GridWithTrapCorridor();
        environment = new EnvironmentShip(spaceGrid);
        MonteCarloSettings settings = MonteCarloSettings.builder()
                .coefficientMaxAverageReturn(1) //only max
                .maxNofIterations(MAX_NOF_ITERATIONS)
                .nofSimulationsPerNode(10)
                .build();
        monteCarloTreeCreator = MonteCarloTreeCreator.builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0, 0))
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

        Optional<NodeInterface> node52 = NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(), StateShip.newStateFromXY(4, 2));
        Assert.assertTrue(node52.isPresent());
    }

    @Test(expected = StartStateIsTrapException.class)
    public void iterateFromX2Y0() throws StartStateIsTrapException {
        monteCarloTreeCreator.setStartState(StateShip.newStateFromXY(2, 0));
        NodeInterface nodeRoot = monteCarloTreeCreator.runIterations();
        TreeInfoHelper tih = new TreeInfoHelper(nodeRoot);
        Assert.assertTrue(tih.isStateInAnyNode(StateShip.newStateFromXY(3, 0)));
        Assert.assertFalse(tih.isStateInAnyNode(StateShip.newStateFromXY(4, 0)));

        doPrinting(tih, nodeRoot);
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y0NoSimulations() {

        MonteCarloSettings settings = MonteCarloSettings.builder()
                .coefficientMaxAverageReturn(1) //only max
                .maxNofIterations(MAX_NOF_ITERATIONS)
                .nofSimulationsPerNode(0)
                .build();
        monteCarloTreeCreator = MonteCarloTreeCreator.builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0, 0))
                .monteCarloSettings(settings)
                .build();

        NodeInterface nodeRoot = monteCarloTreeCreator.runIterations();
        TreeInfoHelper tih = new TreeInfoHelper(nodeRoot);
        tih.getNodesOnPathForActions(monteCarloTreeCreator.getActionsToSelected()).orElseThrow().forEach(System.out::println);
        doPrinting(tih, nodeRoot);

        Optional<NodeInterface> node52 = NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(), StateShip.newStateFromXY(4, 2));
        Assert.assertTrue(node52.isPresent());
    }

    private void doPrinting(TreeInfoHelper tih, NodeInterface nodeRoot) {
        System.out.println("nofNodesInTree = " + tih.nofNodesInTree());
        nodeRoot.printTree();
        tih.getBestPath().forEach(System.out::println);
    }

}
