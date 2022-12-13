package mcts_spacegame;

import lombok.SneakyThrows;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.helpers.NodeInfoHelper;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.model_mcts.MonteCarloSettings;
import mcts_spacegame.model_mcts.MonteCarloTreeCreator;
import mcts_spacegame.model_mcts.NodeValueMemory;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.State;
import mcts_spacegame.policies_action.SimulationPolicyInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class Test_5times15grid {

    private static final int MAX_NOF_ITERATIONS = 500;
    private static final int NOF_SIMULATIONS_PER_NODE = 10;  //important
    private static final int MAX_TREE_DEPTH = 5;
    private static final int COEFFICIENT_EXPLOITATION_EXPLORATION = 100;
    private static final double DELTA = 0.5;
    private static final double VALUE_6 = 6;
    private static final double VALUE_3 = 3;
    private static final double VALUE_0 = 0;

    MonteCarloTreeCreator monteCarloTreeCreator;
    Environment environment;
    MonteCarloSettings settings;
    NodeValueMemory memory;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new5times15Grid();
        environment = new Environment(spaceGrid);
        settings= MonteCarloSettings.builder()
                .coefficientMaxAverageReturn(1) //only max
                .maxTreeDepth(MAX_TREE_DEPTH)
                .simulationPolicy(SimulationPolicyInterface.newMostlyStill())
                .maxNofIterations(MAX_NOF_ITERATIONS)
                .nofSimulationsPerNode(NOF_SIMULATIONS_PER_NODE)
                .weightReturnsSteps(0)
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)
                .build();
        memory=NodeValueMemory.newEmpty();
        memory.write(State.newState(14,0),VALUE_0);
        memory.write(State.newState(14,2),VALUE_3);
        memory.write(State.newState(14,4),VALUE_6);

        createCreator(State.newState(0, 0));
    }



    @Test
    public void printEnvironment() {
        System.out.println("environment = " + environment);
        System.out.println("memory = " + memory);
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y2() {
        NodeInterface nodeRoot = monteCarloTreeCreator.runIterations();
        doPrinting(nodeRoot);
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        assertStateIsOnBestPath(tih,State.newState(4,4));
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y2NoSimulations() {
        settings = MonteCarloSettings.builder()
                .maxTreeDepth(14)
                .simulationPolicy(SimulationPolicyInterface.newMostlyStill())
                .maxNofIterations(10)
                .nofSimulationsPerNode(0)
                .weightReturnsSteps(1)
                .coefficientExploitationExploration(10)
                .build();
        System.out.println("memory = " + memory);
        createCreator(State.newState(0, 2));
       // createCreator(State.newState(14, 4));

        NodeInterface nodeRoot = monteCarloTreeCreator.runIterations();
        doPrinting(nodeRoot);
      //  TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
      //  assertStateIsOnBestPath(tih,State.newState(13,4));
    }

    private void createCreator(State state) {
        monteCarloTreeCreator = MonteCarloTreeCreator.builder()
                .environment(environment)
                .startState(state)
                .monteCarloSettings(settings)
                .memory(memory)
                .build();
    }


    private void assertStateIsOnBestPath(TreeInfoHelper tih, State state) {
        Optional<NodeInterface> node= NodeInfoHelper.findNodeMatchingState(tih.getBestPath(), state);
        Assert.assertTrue(node.isPresent());
    }

    private void doPrinting(NodeInterface nodeRoot) {
        TreeInfoHelper tih = new TreeInfoHelper(nodeRoot);

        System.out.println("nofNodesInTree = " + tih.nofNodesInTree());
        System.out.println("monteCarloTreeCreator.getStatistics() = " + monteCarloTreeCreator.getStatistics());
        nodeRoot.printTree();
        tih.getBestPath().forEach(System.out::println);
    }

}
