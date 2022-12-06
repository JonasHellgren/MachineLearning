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

    MonteCarloTreeCreator monteCarloTreeCreator;
    Environment environment;
    MonteCarloSettings settings;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new Environment(spaceGrid);
        settings= MonteCarloSettings.builder()
                .maxTreeDepth(3)
                .maxNofIterations(10)
                .nofSimulationsPerNode(500)
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
        List<Boolean> failList=results.getResults().stream().map(r -> r.isEndingInFail).collect(Collectors.toList());
        Assert.assertTrue(failList.contains(true));
    }

    private void doPrinting(TreeInfoHelper tih,NodeInterface nodeRoot) {
        System.out.println("nofNodesInTree = " + tih.nofNodesInTree());
        nodeRoot.printTree();
        tih.getBestPath().forEach(System.out::println);
    }


}
