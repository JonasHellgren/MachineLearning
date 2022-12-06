package mcts_spacegame;

import mcts_spacegame.environment.Environment;
import mcts_spacegame.helpers.NodeInfoHelper;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.model_mcts.MonteCarloSettings;
import mcts_spacegame.model_mcts.MonteCarloTreeCreator;
import mcts_spacegame.model_mcts.SimulationResults;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.State;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TestMonteCarloTreeCreatorWithSimulations_3times5grid {

    private static final double DISCOUNT_FACTOR_SIMULATION_NORMAL = 1.0;
    private static final double DISCOUNT_FACTOR_SIMULATION_DEFENSIVE = 0.1;
    private static final double ALPHA_BACKUP_SIMULATION_DEFENSIVE = 0.1;
    private static final double ALPHA_BACKUP_SIMULATION_NORMAL = 1d;
    private static final double ALPHA_BACKUP_STEPS = 0.0d;
    private static final int MAX_NOF_ITERATIONS = 50;
    private static final int NOF_SIMULATIONS_PER_NODE = 100;
    private static final int MAX_TREE_DEPTH = 3;

    MonteCarloTreeCreator monteCarloTreeCreator;
    Environment environment;
    MonteCarloSettings settings;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new Environment(spaceGrid);
        settings= MonteCarloSettings.builder()
                .alphaBackupSteps(ALPHA_BACKUP_STEPS)
                .alphaBackupSimulationNormal(ALPHA_BACKUP_SIMULATION_NORMAL)
                .alphaBackupSimulationDefensive(ALPHA_BACKUP_SIMULATION_DEFENSIVE)
                .coefficientMaxAverageReturn(0)  //max return
                .discountFactorSimulationNormal(DISCOUNT_FACTOR_SIMULATION_NORMAL)
                .discountFactorSimulationDefensive(DISCOUNT_FACTOR_SIMULATION_DEFENSIVE)
                .maxTreeDepth(MAX_TREE_DEPTH)
                .maxNofIterations(MAX_NOF_ITERATIONS)
                .nofSimulationsPerNode(NOF_SIMULATIONS_PER_NODE)
                .build();
        monteCarloTreeCreator=MonteCarloTreeCreator.builder()
                .environment(environment)
                .startState(new State(0,0))
                .monteCarloSettings(settings)
                .build();
    }

    @Test
    public void simulatingFromX5Y1NeverFails() {
        monteCarloTreeCreator=MonteCarloTreeCreator.builder()
                .environment(environment)
                .startState(new State(5,1))
                .monteCarloSettings(settings)
                .build();
        NodeInterface nodeRoot=monteCarloTreeCreator.getNodeRoot();
        SimulationResults results=monteCarloTreeCreator.simulate(nodeRoot);
        List<Boolean> failList=results.getResults().stream().map(r -> r.isEndingInFail).collect(Collectors.toList());
       // failList.forEach(System.out::println);
        Assert.assertFalse(failList.contains(true));
    }

    @Test
    public void simulatingFromX5Y2SomeTimeFails() {
        monteCarloTreeCreator=MonteCarloTreeCreator.builder()
                .environment(environment)
                .startState(new State(5,2))
                .monteCarloSettings(settings)
                .build();
        NodeInterface nodeRoot=monteCarloTreeCreator.getNodeRoot();
        SimulationResults results=monteCarloTreeCreator.simulate(nodeRoot);
        List<Boolean> failList=results.getResults().stream()
                .map(r -> r.isEndingInFail).collect(Collectors.toList());
        Assert.assertTrue(failList.contains(true));
    }

    @Test
    public void iterateFromX0Y0() {
        NodeInterface nodeRoot = monteCarloTreeCreator.doMCTSIterations();
        doPrinting(nodeRoot);
    }


    private void doPrinting(NodeInterface nodeRoot) {
        TreeInfoHelper tih = new TreeInfoHelper(nodeRoot);

        System.out.println("nofNodesInTree = " + tih.nofNodesInTree());
        nodeRoot.printTree();
        tih.getBestPath().forEach(System.out::println);
    }


}
