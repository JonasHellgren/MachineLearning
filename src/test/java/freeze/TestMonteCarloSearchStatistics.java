package freeze;

import lombok.SneakyThrows;
import mcts_spacegame.environment.EnvironmentShip;
import mcts_spacegame.model_mcts.MonteCarloSearchStatistics;
import mcts_spacegame.model_mcts.MonteCarloSettings;
import mcts_spacegame.model_mcts.MonteCarloTreeCreator;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.StateShip;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestMonteCarloSearchStatistics {

    private static final int MAX_TREE_DEPTH = 3;
    MonteCarloTreeCreator monteCarloTreeCreator;
    EnvironmentShip environment;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new EnvironmentShip(spaceGrid);
        monteCarloTreeCreator=MonteCarloTreeCreator.builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0,0))
                .monteCarloSettings(MonteCarloSettings.builder()
                        .maxTreeDepth(MAX_TREE_DEPTH)
                        .coefficientExploitationExploration(1)
                        .maxNofIterations(500).build())
                .build();
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y0() {
        NodeInterface nodeRoot=monteCarloTreeCreator.runIterations();
        MonteCarloSearchStatistics statistics= monteCarloTreeCreator.getStatistics();

        System.out.println("statistics = " + statistics);
        nodeRoot.printTree();

        Assert.assertEquals(MAX_TREE_DEPTH, statistics.getMaxDepth());
        Assert.assertTrue(statistics.getAverageNofChildrenPerNode()<=3);
        Assert.assertTrue(statistics.getUsedTimeInMilliSeconds()>0);

    }

}
