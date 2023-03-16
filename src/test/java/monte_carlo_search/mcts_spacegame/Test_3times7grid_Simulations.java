package monte_carlo_search.mcts_spacegame;

import lombok.SneakyThrows;
import monte_carlo_tree_search.create_tree.MonteCarloSimulator;
import monte_carlo_tree_search.classes.SimulationResults;
import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class Test_3times7grid_Simulations {

    private static final double DISCOUNT_FACTOR_SIMULATION_NORMAL = 1.0;
    private static final double DISCOUNT_FACTOR_SIMULATION_DEFENSIVE = 0.1;
    private static final int MAX_NOF_ITERATIONS = 50;
    private static final int NOF_SIMULATIONS_PER_NODE = 100;
    private static final int MAX_TREE_DEPTH = 3;
    private static final int COEFFICIENT_EXPLOITATION_EXPLORATION = 10;
    private static final int ALPHA_BACKUP_STEPS_NORMAL = 1;
    private static final double ALPHA_BACKUP_STEPS_DEFENSIVE = 0.1;

    MonteCarloTreeCreator<ShipVariables, ShipActionSet> monteCarloTreeCreator;
    EnvironmentGenericInterface<ShipVariables, ShipActionSet> environment;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;
    ActionInterface<ShipActionSet> actionTemplate;
    MonteCarloSimulator<ShipVariables, ShipActionSet> simulator;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new EnvironmentShip(spaceGrid);
        actionTemplate=new ActionShip(ShipActionSet.notApplicable); //whatever action

        settings= MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .actionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .alphaBackupNormal(ALPHA_BACKUP_STEPS_NORMAL)
                .alphaBackupDefensiveStep(ALPHA_BACKUP_STEPS_DEFENSIVE)
                .coefficientMaxAverageReturn(0)  //max return
                .discountFactorBackupSimulationNormal(DISCOUNT_FACTOR_SIMULATION_NORMAL)
                .discountFactorBackupSimulationDefensive(DISCOUNT_FACTOR_SIMULATION_DEFENSIVE)
                .maxTreeDepth(MAX_TREE_DEPTH)
                .maxNofIterations(MAX_NOF_ITERATIONS)
                .nofSimulationsPerNode(NOF_SIMULATIONS_PER_NODE)
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)
                .build();
        monteCarloTreeCreator=MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0,0))
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
        simulator=new MonteCarloSimulator<>(environment,settings);
    }

    @Test
    public void whenSimulatingFromX5Y1_thenNeverFails() {

        SimulationResults results=simulator.simulate(StateShip.newStateFromXY(5,1));
        List<Boolean> failList=results.getResults().stream().map(r -> r.isEndingInFail).collect(Collectors.toList());
        Assert.assertFalse(failList.contains(true));
    }

    @Test
    public void whenSimulatingFromX5Y2_thenSomeTimeFails() {
        SimulationResults results=simulator.simulate(StateShip.newStateFromXY(5,2));

        List<Boolean> failList=results.getResults().stream()
                .map(r -> r.isEndingInFail).collect(Collectors.toList());
        Assert.assertTrue(failList.contains(true));
    }

    @SneakyThrows
    @Test
    public void whenStartingFromX0Y0_then11And32IsOnBestPath() {
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        doPrinting(nodeRoot);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(1,1)));
        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(3,2)));
    }



    @SneakyThrows
    @Test
    public void whenStartingFromX0Y1_then12And32IsOnBestPath() {
        monteCarloTreeCreator.setStartState(StateShip.newStateFromXY(0,1));
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();

        doPrinting(nodeRoot);

        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(1,1)));
        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(3,2)));

    }


    private void doPrinting(NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot) {
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot,settings);

        System.out.println("nofNodesInTree = " + tih.nofNodes());
        nodeRoot.printTree();
        tih.getBestPath().forEach((n) -> System.out.println(n.getState().getVariables()));
    }


}
