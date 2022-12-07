package mcts_spacegame;

import common.MathUtils;
import mcts_spacegame.enums.Action;
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
    private static final double DISCOUNT_FACTOR_SIMULATION_NORMAL = 1.0;
    private static final double DISCOUNT_FACTOR_SIMULATION_DEFENSIVE = 0.1;
    private static final double ALPHA_BACKUP_SIMULATION_DEFENSIVE = 0.1d;
    private static final double ALPHA_BACKUP_SIMULATION_NORMAL = 0.9d;
    private static final double ALPHA_BACKUP_STEPS = 0.9d;
    private static final int MAX_NOF_ITERATIONS = 1000;
    private static final int NOF_SIMULATIONS_PER_NODE = 100;
    private static final int MAX_TREE_DEPTH = 10;
    private static final int COEFFICIENT_EXPLOITATION_EXPLORATION = 1;

    MonteCarloTreeCreator monteCarloTreeCreator;
    Environment environment;
    NodeValueMemory memory;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new5times15Grid();
        environment = new Environment(spaceGrid);
        MonteCarloSettings settings= MonteCarloSettings.builder()
                .alphaBackupSteps(ALPHA_BACKUP_STEPS)
                .alphaBackupSimulationNormal(ALPHA_BACKUP_SIMULATION_NORMAL)
                .alphaBackupSimulationDefensive(ALPHA_BACKUP_SIMULATION_DEFENSIVE)
                .coefficientMaxAverageReturn(0)  //max return
                .discountFactorSimulationNormal(DISCOUNT_FACTOR_SIMULATION_NORMAL)
                .discountFactorSimulationDefensive(DISCOUNT_FACTOR_SIMULATION_DEFENSIVE)
                .maxTreeDepth(MAX_TREE_DEPTH)
                .maxNofIterations(MAX_NOF_ITERATIONS)
                .isBackupFromSteps(false)
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
        NodeInterface nodeMock= NodeInterface.newNotTerminal(State.newState(13,4), Action.still);
        SimulationResults simulationResults= monteCarloTreeCreator.simulate(nodeMock);
        boolean any6 = simulationResults.getResults().stream().map(r -> r.valueInTerminalState).anyMatch(v -> MathUtils.isZero(v-6));
        System.out.println("simulationResults = " + simulationResults);
        Assert.assertTrue(any6);
    }

    @Test public void moveFromX9Y4IntoGoalWithHighValue() {
        NodeInterface nodeMock= NodeInterface.newNotTerminal(State.newState(5,4), Action.still);
        SimulationResults simulationResults= monteCarloTreeCreator.simulate(nodeMock);
        boolean any6 = simulationResults.getResults().stream().map(r -> r.valueInTerminalState).anyMatch(v -> MathUtils.isZero(v-6));
        System.out.println("simulationResults = " + simulationResults);
        Assert.assertTrue(any6);
    }

    @Test
    public void iterateFromX0Y2() {
        NodeInterface nodeRoot = monteCarloTreeCreator.doMCTSIterations();
        doPrinting(nodeRoot);
        TreeInfoHelper tih=new TreeInfoHelper(nodeRoot);
        assertStateIsOnBestPath(tih,State.newState(1,3));
        assertStateIsOnBestPath(tih,State.newState(3,4));
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
