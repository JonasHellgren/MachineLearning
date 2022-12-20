package mcts_spacegame;

import lombok.SneakyThrows;
import mcts_spacegame.environment.EnvironmentShip;
import mcts_spacegame.exceptions.StartStateIsTrapException;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.generic_interfaces.EnvironmentGenericInterface;
import mcts_spacegame.helpers.NodeInfoHelper;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.model_mcts.MonteCarloSettings;
import mcts_spacegame.model_mcts.MonteCarloTreeCreator;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.*;
import mcts_spacegame.policies_action.SimulationPolicyInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class Test_3times7grid {
    MonteCarloTreeCreator<ShipVariables, ShipActionSet> monteCarloTreeCreator;
    EnvironmentGenericInterface<ShipVariables, ShipActionSet> environment;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;
    ActionInterface<ShipActionSet> actionTemplate;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new EnvironmentShip(spaceGrid);
        settings=MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> ShipActionSet.applicableActions().size())
                .firstActionSelectionPolicy(SimulationPolicyInterface.newAlwaysStill())
                .simulationPolicy(SimulationPolicyInterface.newMostlyStill())
                .build();
        actionTemplate=new ActionShip(ShipActionSet.notApplicable); //whatever action
        monteCarloTreeCreator=MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0,0))
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y0() {
        NodeInterface<ShipVariables, ShipActionSet> nodeRoot=monteCarloTreeCreator.runIterations();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);

        System.out.println("monteCarloTreeCreator.getActionsToSelected() = " + monteCarloTreeCreator.getActionsToSelected());

        System.out.println("tih.getNodesOnPathForActions(monteCarloTreeCreator.getActionsToSelected()).orElseThrow() =");
        tih.getNodesOnPathForActions(monteCarloTreeCreator.getActionsToSelected()).orElseThrow().forEach(System.out::println);

        doPrinting(tih,nodeRoot);

        Optional<NodeInterface<ShipVariables, ShipActionSet>> node11= NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(),StateShip.newStateFromXY(1,1));
        Assert.assertTrue(node11.isPresent());
        Optional<NodeInterface<ShipVariables, ShipActionSet>> node52= NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(),StateShip.newStateFromXY(5,2));
        Assert.assertTrue(node52.isPresent());

    }

    @SneakyThrows
    @Test(expected = StartStateIsTrapException.class)
    public void iterateFromX2Y0() {
        monteCarloTreeCreator.setStartState(StateShip.newStateFromXY(2,0));
        NodeInterface<ShipVariables, ShipActionSet> nodeRoot=monteCarloTreeCreator.runIterations();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);

        doPrinting(tih,nodeRoot);

        Optional<NodeInterface<ShipVariables, ShipActionSet>> node11= NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(),StateShip.newStateFromXY(1,1));
        Assert.assertFalse(node11.isPresent());
        Optional<NodeInterface<ShipVariables, ShipActionSet>> node52= NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(),StateShip.newStateFromXY(5,2));
        Assert.assertFalse(node52.isPresent());
    }

    @SneakyThrows
    @Test
    public void iterateFromX1Y1() {
        monteCarloTreeCreator.setStartState(StateShip.newStateFromXY(1,1));
        NodeInterface<ShipVariables, ShipActionSet> nodeRoot=monteCarloTreeCreator.runIterations();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);

        doPrinting(tih,nodeRoot);

        Assert.assertTrue(tih.isStateInAnyNode(StateShip.newStateFromXY(2,0)));
    }

    @SneakyThrows
    @Test public void maxTreeDepth() {
        settings.setMaxTreeDepth(3);

        monteCarloTreeCreator=MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0,0))
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
        NodeInterface<ShipVariables, ShipActionSet> nodeRoot=monteCarloTreeCreator.runIterations();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);

        doPrinting(tih,nodeRoot);
        System.out.println("tih.maxDepth() = " + tih.maxDepth());

        Assert.assertEquals(3,tih.maxDepth());
    }


    private void doPrinting(TreeInfoHelper<ShipVariables, ShipActionSet> tih,NodeInterface<ShipVariables, ShipActionSet> nodeRoot) {
        System.out.println("nofNodesInTree = " + tih.nofNodesInTree());
        nodeRoot.printTree();
        tih.getBestPath().forEach(System.out::println);
    }

}
