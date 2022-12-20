package mcts_classes;

import lombok.SneakyThrows;
import mcts_spacegame.domains.models_space.EnvironmentShip;
import mcts_spacegame.generic_interfaces.EnvironmentGenericInterface;
import mcts_spacegame.classes.MonteCarloSearchStatistics;
import mcts_spacegame.classes.MonteCarloSettings;
import mcts_spacegame.classes.MonteCarloTreeCreator;
import mcts_spacegame.nodes.NodeInterface;
import mcts_spacegame.domains.models_space.*;
import mcts_spacegame.domains.models_space.ShipPolicies;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestMonteCarloSearchStatistics {

    private static final int MAX_TREE_DEPTH = 3;
    MonteCarloTreeCreator<ShipVariables, ShipActionSet> monteCarloTreeCreator;
    EnvironmentGenericInterface<ShipVariables, ShipActionSet> environment;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new EnvironmentShip(spaceGrid);
        monteCarloTreeCreator=MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0,0))
                .monteCarloSettings(MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                        .maxNofTestedActionsForBeingLeafFunction((a) -> ShipActionSet.applicableActions().size())
                        .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                        .simulationPolicy(ShipPolicies.newMostlyStill())
                        .maxTreeDepth(MAX_TREE_DEPTH)
                        .coefficientExploitationExploration(1)
                        .maxNofIterations(500)
                                .build())
                .actionTemplate(ActionShip.newNA())
                .build();
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y0() {
        NodeInterface<ShipVariables, ShipActionSet>  nodeRoot=monteCarloTreeCreator.runIterations();
        MonteCarloSearchStatistics statistics= monteCarloTreeCreator.getStatistics();

        System.out.println("statistics = " + statistics);
        nodeRoot.printTree();

        Assert.assertEquals(MAX_TREE_DEPTH, statistics.getMaxDepth());
        Assert.assertTrue(statistics.getAverageNofChildrenPerNode()<=3);
        Assert.assertTrue(statistics.getUsedTimeInMilliSeconds()>0);

    }

}
