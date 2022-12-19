package mcts_spacegame;

import lombok.SneakyThrows;
import mcts_spacegame.environment.EnvironmentShip;
import mcts_spacegame.helpers.NodeInfoHelper;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.model_mcts.MonteCarloSettings;
import mcts_spacegame.model_mcts.MonteCarloTreeCreator;
import mcts_spacegame.model_mcts.SimulationResults;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.StateShip;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Test_3times5grid_Simulations {

    private static final double DISCOUNT_FACTOR_SIMULATION_NORMAL = 1.0;
    private static final double DISCOUNT_FACTOR_SIMULATION_DEFENSIVE = 0.1;
    private static final int MAX_NOF_ITERATIONS = 50;
    private static final int NOF_SIMULATIONS_PER_NODE = 100;
    private static final int MAX_TREE_DEPTH = 3;
    private static final int COEFFICIENT_EXPLOITATION_EXPLORATION = 10;
    private static final int ALPHA_BACKUP_STEPS_NORMAL = 1;
    private static final double ALPHA_BACKUP_STEPS_DEFENSIVE = 0.1;

    MonteCarloTreeCreator monteCarloTreeCreator;
    EnvironmentShip environment;
    MonteCarloSettings settings;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new EnvironmentShip(spaceGrid);
        settings= MonteCarloSettings.builder()
                .alphaBackupNormal(ALPHA_BACKUP_STEPS_NORMAL)
                .alphaBackupDefensive(ALPHA_BACKUP_STEPS_DEFENSIVE)
                .coefficientMaxAverageReturn(0)  //max return
                .discountFactorSimulationNormal(DISCOUNT_FACTOR_SIMULATION_NORMAL)
                .discountFactorSimulationDefensive(DISCOUNT_FACTOR_SIMULATION_DEFENSIVE)
                .maxTreeDepth(MAX_TREE_DEPTH)
                .maxNofIterations(MAX_NOF_ITERATIONS)
                //.isBackupFromSteps(false)
                .nofSimulationsPerNode(NOF_SIMULATIONS_PER_NODE)
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)
                .build();
        monteCarloTreeCreator=MonteCarloTreeCreator.builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0,0))
                .monteCarloSettings(settings)
                .build();
    }

    @Test
    public void simulatingFromX5Y1NeverFails() {
        SimulationResults results=monteCarloTreeCreator.simulate(StateShip.newStateFromXY(5,1));
        List<Boolean> failList=results.getResults().stream().map(r -> r.isEndingInFail).collect(Collectors.toList());
        Assert.assertFalse(failList.contains(true));
    }

    @Test
    public void simulatingFromX5Y2SomeTimeFails() {
        SimulationResults results=monteCarloTreeCreator.simulate(StateShip.newStateFromXY(5,2));

        List<Boolean> failList=results.getResults().stream()
                .map(r -> r.isEndingInFail).collect(Collectors.toList());
        Assert.assertTrue(failList.contains(true));
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y0() {
        NodeInterface nodeRoot = monteCarloTreeCreator.runIterations();
        doPrinting(nodeRoot);
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot,settings);
        assertStateIsOnBestPath(tih,StateShip.newStateFromXY(1,1));
        assertStateIsOnBestPath(tih,StateShip.newStateFromXY(3,2));
    }

    private void assertStateIsOnBestPath(TreeInfoHelper tih, StateShip state) {
        Optional<NodeInterface> node= NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(), state);
        Assert.assertTrue(node.isPresent());
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y1() {
        monteCarloTreeCreator.setStartState(StateShip.newStateFromXY(0,1));
        NodeInterface nodeRoot = monteCarloTreeCreator.runIterations();

        doPrinting(nodeRoot);

        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot,settings);
        assertStateIsOnBestPath(tih,StateShip.newStateFromXY(1,2));
        assertStateIsOnBestPath(tih,StateShip.newStateFromXY(3,2));
    }


    private void doPrinting(NodeInterface nodeRoot) {
        TreeInfoHelper tih = new TreeInfoHelper(nodeRoot,settings);

        System.out.println("nofNodesInTree = " + tih.nofNodesInTree());
        nodeRoot.printTree();
        tih.getBestPath().forEach(System.out::println);
    }


}
