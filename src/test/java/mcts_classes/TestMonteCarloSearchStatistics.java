package mcts_classes;

import lombok.SneakyThrows;
import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.classes.MonteCarloSearchStatistics;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.node_models.NodeInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestMonteCarloSearchStatistics {
    private static final double DELTA = 0.1;
    MonteCarloTreeCreator<ShipVariables, ShipActionSet> monteCarloTreeCreator;
    EnvironmentGenericInterface<ShipVariables, ShipActionSet> environment;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new EnvironmentShip(spaceGrid);
        ActionInterface<ShipActionSet> actionTemplate=new ActionShip(ShipActionSet.notApplicable); //whatever action
        settings=MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .build();

        monteCarloTreeCreator=MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0,0))
                .monteCarloSettings(settings)
                .actionTemplate(ActionShip.newNA())
                .build();
    }


    @Test public void threeNodesStandingGivesBranchingOne() {
        NodeInterface<ShipVariables, ShipActionSet> nodeRoot = create3nodesTree();
        MonteCarloSearchStatistics<ShipVariables, ShipActionSet> statistics=new MonteCarloSearchStatistics<>(
                nodeRoot,monteCarloTreeCreator,settings);

        nodeRoot.printTree();
        System.out.println("statistics = " + statistics);
        assertAll(
                () -> assertEquals(3,statistics.getNofNodes(), DELTA),
                () -> assertEquals(0,statistics.getNofNodesFail(), DELTA),
                () -> assertEquals(2,statistics.getNofNodesWithChildren(), DELTA),
                () -> assertEquals(2,statistics.getMaxDepth(), DELTA),
                () -> assertEquals(1,statistics.getAverageNofChildrenPerNode(), DELTA)
        );
    }

    @Test public void twoDeep1FourDeep2GivesBranchingTwo() {
        NodeInterface<ShipVariables, ShipActionSet> nodeRoot = createTwoDeep1FourDeep2();
        MonteCarloSearchStatistics<ShipVariables, ShipActionSet> statistics=new MonteCarloSearchStatistics<>(
                nodeRoot,monteCarloTreeCreator,settings);

        nodeRoot.printTree();
        System.out.println("statistics = " + statistics);
        assertAll(
                () -> assertEquals(7,statistics.getNofNodes(), DELTA),
                () -> assertEquals(0,statistics.getNofNodesFail(), DELTA),
                () -> assertEquals(3,statistics.getNofNodesWithChildren(), DELTA),
                () -> assertEquals(2,statistics.getMaxDepth(), DELTA),
                () -> assertEquals(2,statistics.getAverageNofChildrenPerNode(), DELTA)
        );
    }

    private NodeInterface<ShipVariables, ShipActionSet> create3nodesTree() {
        NodeInterface<ShipVariables, ShipActionSet>  nodeRoot =
                NodeInterface.newNotTerminal(StateShip.newStateFromXY(0,0),ActionShip.newNA());
        NodeInterface<ShipVariables, ShipActionSet> ch1 =
                NodeInterface.newNotTerminal(StateShip.newStateFromXY(1,0),ActionShip.newStill());
        NodeInterface<ShipVariables, ShipActionSet> ch2 =
                NodeInterface.newNotTerminal(StateShip.newStateFromXY(2,0),ActionShip.newStill());
        nodeRoot.addChildNode(ch1);
        ch1.addChildNode(ch2);
        return nodeRoot;
    }

    private NodeInterface<ShipVariables, ShipActionSet> createTwoDeep1FourDeep2() {
        NodeInterface<ShipVariables, ShipActionSet>  nodeRoot =
                NodeInterface.newNotTerminal(StateShip.newStateFromXY(0,0),ActionShip.newNA());
        NodeInterface<ShipVariables, ShipActionSet> ch1Deep1 =
                NodeInterface.newNotTerminal(StateShip.newStateFromXY(1,1),ActionShip.newStill());
        NodeInterface<ShipVariables, ShipActionSet> ch2Deep1 =
                NodeInterface.newNotTerminal(StateShip.newStateFromXY(1,0),ActionShip.newStill());
        nodeRoot.addChildNode(ch1Deep1);
        nodeRoot.addChildNode(ch2Deep1);

        NodeInterface<ShipVariables, ShipActionSet> ch1Deep2 =
                NodeInterface.newNotTerminal(StateShip.newStateFromXY(2,0),ActionShip.newStill());
        NodeInterface<ShipVariables, ShipActionSet> ch2Deep2 =
                NodeInterface.newNotTerminal(StateShip.newStateFromXY(2,1),ActionShip.newStill());
        NodeInterface<ShipVariables, ShipActionSet> ch3Deep2 =
                NodeInterface.newNotTerminal(StateShip.newStateFromXY(2,2),ActionShip.newStill());
        NodeInterface<ShipVariables, ShipActionSet> ch4Deep2 =
                NodeInterface.newNotTerminal(StateShip.newStateFromXY(2,3),ActionShip.newStill());

        ch1Deep1.addChildNode(ch1Deep2);
        ch1Deep1.addChildNode(ch2Deep2);
        ch2Deep1.addChildNode(ch3Deep2);
        ch2Deep1.addChildNode(ch4Deep2);
        return nodeRoot;
    }

}
