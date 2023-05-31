package monte_carlo_search.mcts_spacegame;

import lombok.SneakyThrows;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.search_tree_node_models.NodeWithChildrenInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/***
 * These tests gives better understanding of settings.setWeightReturnsSteps, settings.setWeightReturnsSimulation
 * When setWeightReturnsSteps is 0 the root values will be "faulty", not considering move cost
 * Most adequate is probably to set them both as 1, correct root values and considering long term value
 */

public class Test_3times7grid_RootValuesFromSimulationsAndSteps {
    private static final double DISCOUNT_FACTOR_SIMULATION_NORMAL = 1.0;
    private static final double DISCOUNT_FACTOR_SIMULATION_DEFENSIVE = 0.1;
    private static final int MAX_NOF_ITERATIONS = 1_000; //10_000;
    private static final int NOF_SIMULATIONS_PER_NODE = 100;
    private static final int MAX_TREE_DEPTH = 10;
    private static final int COEFFICIENT_EXPLOITATION_EXPLORATION = 10;
    private static final int ALPHA_BACKUP_STEPS_NORMAL = 1;
    private static final double ALPHA_BACKUP_STEPS_DEFENSIVE = 0.1;
    private static final double DELTA = 0.75;
    private static final int RETURN_OF_TWO_MOVES = -2;

    MonteCarloTreeCreator<ShipVariables, ShipActionSet> monteCarloTreeCreator;
    EnvironmentGenericInterface<ShipVariables, ShipActionSet> environment;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;
    ActionInterface<ShipActionSet> actionTemplate;

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
                .discountFactorSteps(1.0)
                .discountFactorBackupSimulationNormal(DISCOUNT_FACTOR_SIMULATION_NORMAL)
                .discountFactorBackupSimulationDefensive(DISCOUNT_FACTOR_SIMULATION_DEFENSIVE)
                .maxTreeDepth(MAX_TREE_DEPTH)
                .maxNofIterations(MAX_NOF_ITERATIONS)
                .nofSimulationsPerNode(NOF_SIMULATIONS_PER_NODE)
                .weightReturnsSteps(0.5)
                .weightReturnsSimulation(0.5)
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)
                .build();
        monteCarloTreeCreator= MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0,0))
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

    @SneakyThrows
    @Test
    public void givenWeightSteps1WeightSim0_whenStartingFromX0Y0_then11And32IsOnBestPath() {
        settings.setWeightReturnsSteps(1.0);
        settings.setWeightReturnsSimulation(0.0);

        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        SpaceGameTestHelper.doRootNodePrinting(nodeRoot);
        assertBestPathIsCorrect(nodeRoot);
        Assert.assertEquals(RETURN_OF_TWO_MOVES,nodeRoot.getActionValue(ActionShip.newUp()), DELTA);
    }


    @SneakyThrows
    @Test
    public void givenWeightSteps0WeightSim1_whenStartingFromX0Y0_then11And32IsOnBestPath() {
        settings.setWeightReturnsSteps(0.0);
        settings.setWeightReturnsSimulation(1.0);

        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        SpaceGameTestHelper.doRootNodePrinting(nodeRoot);
        assertBestPathIsCorrect(nodeRoot);
        Assert.assertEquals(0,nodeRoot.getActionValue(ActionShip.newUp()), DELTA);
    }

    @SneakyThrows
    @Test
    public void givenWeightSteps1WeightSim1_whenStartingFromX0Y0_then11And32IsOnBestPath() {
        settings.setWeightReturnsSteps(1.0);
        settings.setWeightReturnsSimulation(1.0);

        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        SpaceGameTestHelper.doRootNodePrinting(nodeRoot);
        assertBestPathIsCorrect(nodeRoot);
        Assert.assertEquals(RETURN_OF_TWO_MOVES,nodeRoot.getActionValue(ActionShip.newUp()), DELTA);

    }


    private void assertBestPathIsCorrect(NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot) {
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot, settings);
        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(1, 1)));
        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(3, 2)));
    }





}
