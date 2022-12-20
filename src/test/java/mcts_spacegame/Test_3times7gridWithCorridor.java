package mcts_spacegame;

import lombok.SneakyThrows;
import mcts_spacegame.domains.models_space.EnvironmentShip;
import mcts_spacegame.exceptions.StartStateIsTrapException;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.generic_interfaces.EnvironmentGenericInterface;
import mcts_spacegame.helpers.NodeInfoHelper;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.classes.MonteCarloSettings;
import mcts_spacegame.classes.MonteCarloTreeCreator;
import mcts_spacegame.nodes.NodeInterface;
import mcts_spacegame.domains.models_space.*;
import mcts_spacegame.domains.models_space.ShipPolicies;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class Test_3times7gridWithCorridor {

    private static final int MAX_NOF_ITERATIONS = 500;
    MonteCarloTreeCreator<ShipVariables, ShipActionSet> monteCarloTreeCreator;
    EnvironmentGenericInterface<ShipVariables, ShipActionSet> environment;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;
    ActionInterface<ShipActionSet> actionTemplate;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new3times7GridWithTrapCorridor();
        environment = new EnvironmentShip(spaceGrid);
        settings = MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> ShipActionSet.applicableActions().size())
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .coefficientMaxAverageReturn(1) //only max
                .maxNofIterations(MAX_NOF_ITERATIONS)
                .nofSimulationsPerNode(10)
                .build();
        actionTemplate=new ActionShip(ShipActionSet.notApplicable); //whatever action
        monteCarloTreeCreator = MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0, 0))
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y0() {
        NodeInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.runIterations();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot,settings);
        tih.getNodesOnPathForActions(monteCarloTreeCreator.getActionsToSelected()).orElseThrow().forEach(System.out::println);
        doPrinting(tih, nodeRoot);

        Optional<NodeInterface<ShipVariables, ShipActionSet>> node52 = NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(), StateShip.newStateFromXY(4, 2));
        Assert.assertTrue(node52.isPresent());
    }

    @Test(expected = StartStateIsTrapException.class)
    public void iterateFromX2Y0() throws StartStateIsTrapException {
        monteCarloTreeCreator.setStartState(StateShip.newStateFromXY(2, 0));
        NodeInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.runIterations();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot,settings);
        Assert.assertTrue(tih.isStateInAnyNode(StateShip.newStateFromXY(3, 0)));
        Assert.assertFalse(tih.isStateInAnyNode(StateShip.newStateFromXY(4, 0)));

        doPrinting(tih, nodeRoot);
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y0NoSimulations() {

        settings.setNofSimulationsPerNode(0);
        monteCarloTreeCreator = MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0, 0))
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();

        NodeInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.runIterations();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot,settings);
        tih.getNodesOnPathForActions(monteCarloTreeCreator.getActionsToSelected()).orElseThrow().forEach(System.out::println);
        doPrinting(tih, nodeRoot);

        Optional<NodeInterface<ShipVariables, ShipActionSet>> node52 =
                NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(), StateShip.newStateFromXY(4, 2));
        Assert.assertTrue(node52.isPresent());
    }

    private void doPrinting(TreeInfoHelper<ShipVariables, ShipActionSet> tih,
                            NodeInterface<ShipVariables, ShipActionSet> nodeRoot) {
        System.out.println("nofNodesInTree = " + tih.nofNodesInTree());
        nodeRoot.printTree();
        tih.getBestPath().forEach(System.out::println);
    }

}
