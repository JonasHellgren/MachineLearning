package monte_carlo_search.mcts_classes;

import common.CpuTimer;
import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.create_tree.MonteCarloSearchStatistics;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.search_tree_node_models.NodeInterface;
import monte_carlo_tree_search.search_tree_node_models.NodeWithChildrenInterface;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

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
        settings=MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .actionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .build();

        monteCarloTreeCreator=MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0,0))
                .monteCarloSettings(settings)
                .actionTemplate(ActionShip.newNA())
                .build();
    }


    @Test public void whenThreeNodesStanding_thenBranchingOne() {
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = create3nodesTree();
        MonteCarloSearchStatistics<ShipVariables, ShipActionSet> statistics=new MonteCarloSearchStatistics<>(
                nodeRoot,monteCarloTreeCreator,CpuTimer.newTimer(0),settings);

        nodeRoot.printTree();
        System.out.println("statistics = " + statistics);
        assertAll(
                () -> assertEquals(3,statistics.getNofNodes(), DELTA),
                () -> assertEquals(0,statistics.getNofNodesFail(), DELTA),
                () -> assertEquals(2,statistics.getNofNodesWithChildren(), DELTA),
                () -> assertEquals(2,statistics.getMaxDepth(), DELTA),
                () -> assertTrue(statistics.getNofNodesPerDepthLevel().containsAll(Arrays.asList(1,1,1))),
                () -> assertEquals(1,statistics.getAverageNofChildrenPerNode(), DELTA)
        );
    }

    @Test public void whenTwoDeep1FourDeep2_thenBranchingTwo() {
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = createTwoDeep1FourDeep2();
        MonteCarloSearchStatistics<ShipVariables, ShipActionSet> statistics=new MonteCarloSearchStatistics<>(
                nodeRoot,monteCarloTreeCreator,CpuTimer.newTimer(0),settings);

        nodeRoot.printTree();
        System.out.println("statistics = " + statistics);
        assertAll(
                () -> assertEquals(7,statistics.getNofNodes(), DELTA),
                () -> assertEquals(0,statistics.getNofNodesFail(), DELTA),
                () -> assertEquals(3,statistics.getNofNodesWithChildren(), DELTA),
                () -> assertEquals(2,statistics.getMaxDepth(), DELTA),
                () -> assertTrue(statistics.getNofNodesPerDepthLevel().containsAll(Arrays.asList(1,2,4))),
                () -> assertEquals(2,statistics.getAverageNofChildrenPerNode(), DELTA)
        );
    }

    private NodeWithChildrenInterface<ShipVariables, ShipActionSet> create3nodesTree() {
        NodeWithChildrenInterface<ShipVariables, ShipActionSet>  nodeRoot =
                NodeInterface.newNotTerminal(StateShip.newStateFromXY(0,0),ActionShip.newNA());
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> ch1 =
                NodeInterface.newNotTerminal(StateShip.newStateFromXY(1,0),ActionShip.newStill());
        NodeInterface<ShipVariables, ShipActionSet> ch2 =
                NodeInterface.newNotTerminal(StateShip.newStateFromXY(2,0),ActionShip.newStill());
        nodeRoot.addChildNode(ch1);
        ch1.addChildNode(ch2);
        return nodeRoot;
    }

    private NodeWithChildrenInterface<ShipVariables, ShipActionSet> createTwoDeep1FourDeep2() {
        NodeWithChildrenInterface<ShipVariables, ShipActionSet>  nodeRoot =
                NodeInterface.newNotTerminal(StateShip.newStateFromXY(0,0),ActionShip.newNA());
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> ch1Deep1 =
                NodeInterface.newNotTerminal(StateShip.newStateFromXY(1,1),ActionShip.newStill());
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> ch2Deep1 =
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
