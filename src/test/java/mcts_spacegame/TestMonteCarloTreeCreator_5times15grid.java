package mcts_spacegame;

import common.MathUtils;
import lombok.SneakyThrows;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.helpers.NodeInfoHelper;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.model_mcts.MonteCarloSettings;
import mcts_spacegame.model_mcts.MonteCarloTreeCreator;
import mcts_spacegame.model_mcts.NodeValueMemory;
import mcts_spacegame.model_mcts.SimulationResults;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.State;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class TestMonteCarloTreeCreator_5times15grid {
    private static final int MAX_NOF_ITERATIONS = 500;
    private static final int NOF_SIMULATIONS_PER_NODE = 100;  //important
    private static final int MAX_TREE_DEPTH = 10;
    private static final int COEFFICIENT_EXPLOITATION_EXPLORATION = 2;

    MonteCarloTreeCreator monteCarloTreeCreator;
    Environment environment;
    NodeValueMemory memory;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new5times15Grid();
        environment = new Environment(spaceGrid);
        MonteCarloSettings settings= MonteCarloSettings.builder()
                .coefficientMaxAverageReturn(1) //only max
                .maxTreeDepth(MAX_TREE_DEPTH)
                .maxNofIterations(MAX_NOF_ITERATIONS)
                .nofSimulationsPerNode(NOF_SIMULATIONS_PER_NODE)
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)
                .build();
        memory=NodeValueMemory.newEmpty();
        memory.write(State.newState(14,0),0);
        memory.write(State.newState(14,2),3);
        memory.write(State.newState(14,4),6);

        monteCarloTreeCreator=MonteCarloTreeCreator.builder()
                .environment(environment)
                .startState(State.newState(0,2))
                .monteCarloSettings(settings)
                .memory(memory)
                .build();
    }

    @Test
    public void printEnvironment() {
        System.out.println("environment = " + environment);
        System.out.println("memory = " + memory);
    }

    @Test public void moveFromX13Y4IntoGoalWithHighValue() {
        SimulationResults simulationResults= monteCarloTreeCreator.simulate(State.newState(13,4));
        boolean any6 = simulationResults.getResults().stream().map(r -> r.valueInTerminalState).anyMatch(v -> MathUtils.isZero(v-6));
        System.out.println("simulationResults = " + simulationResults);
        Assert.assertTrue(any6);
    }

    @Test public void moveFromX9Y4IntoGoalWithHighValue() {
        SimulationResults simulationResults= monteCarloTreeCreator.simulate(State.newState(5,4));
        boolean any6 = simulationResults.getResults().stream().map(r -> r.valueInTerminalState).anyMatch(v -> MathUtils.isZero(v-6));
        System.out.println("simulationResults = " + simulationResults);
        Assert.assertTrue(any6);
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y2() {
        NodeInterface nodeRoot = monteCarloTreeCreator.runIterations();
        doPrinting(nodeRoot);
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        assertStateIsOnBestPath(tih,State.newState(4,4));
        assertStateIsOnBestPath(tih,State.newState(5,4));
    }

    @SneakyThrows
    @Test
    public void iterateFromX0Y2ManyTimes() {
        for (int i = 0; i < 10 ; i++) {
        NodeInterface nodeRoot = monteCarloTreeCreator.runIterations();
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        assertStateIsOnBestPath(tih,State.newState(4,4));
        assertStateIsOnBestPath(tih,State.newState(5,4));
        }

    }

    private void assertStateIsOnBestPath(TreeInfoHelper tih, State state) {
        Optional<NodeInterface> node= NodeInfoHelper.findNodeMatchingState(tih.getBestPath(), state);
        Assert.assertTrue(node.isPresent());
    }

    private void doPrinting(NodeInterface nodeRoot) {
        TreeInfoHelper tih = new TreeInfoHelper(nodeRoot);

        System.out.println("nofNodesInTree = " + tih.nofNodesInTree());
        nodeRoot.printTree();
        tih.getBestPath().forEach(System.out::println);
    }


}
