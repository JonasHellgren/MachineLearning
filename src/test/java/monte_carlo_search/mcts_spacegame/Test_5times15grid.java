package monte_carlo_search.mcts_spacegame;

import lombok.SneakyThrows;
import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.search_tree_node_models.NodeWithChildrenInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class Test_5times15grid {

    private static final int MAX_NOF_ITERATIONS = 500;
    private static final int NOF_SIMULATIONS_PER_NODE = 10;  //important
    private static final int MAX_TREE_DEPTH = 5;
    private static final int COEFFICIENT_EXPLOITATION_EXPLORATION = 100;


    MonteCarloTreeCreator<ShipVariables, ShipActionSet> monteCarloTreeCreator;
    EnvironmentShip environment;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;
    ActionInterface<ShipActionSet> actionTemplate;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new5times15Grid();
        environment = new EnvironmentShip(spaceGrid);
        actionTemplate = new ActionShip(ShipActionSet.notApplicable); //whatever action
        settings = MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .actionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .coefficientMaxAverageReturn(1) //only max
                .maxTreeDepth(MAX_TREE_DEPTH)
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .maxNofIterations(MAX_NOF_ITERATIONS)
                .nofSimulationsPerNode(NOF_SIMULATIONS_PER_NODE)
                .weightReturnsSteps(0)
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)
                .build();
        actionTemplate = new ActionShip(ShipActionSet.notApplicable); //whatever action

        createCreator(StateShip.newStateFromXY(0, 0));
    }


    @Test
    public void printEnvironment() {
        System.out.println("environment = " + environment);
    }

    @SneakyThrows
    @Test
    public void whenStartingFromX0Y0_then54IsVisited() {
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        SpaceGameTestHelper.doPrinting(nodeRoot,settings,monteCarloTreeCreator);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot, settings);

        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(5,4)));
    }

    @SneakyThrows
    @Test
    public void givenNoSimFewIterations_whenStartingFromX0Y2_then54IsVisited() {
        settings = MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .actionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .maxTreeDepth(14)
                .maxNofIterations(10)
                .nofSimulationsPerNode(0)
                .weightReturnsSteps(1)
                .coefficientExploitationExploration(100)
                .build();
        createCreator(StateShip.newStateFromXY(0, 2));
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        SpaceGameTestHelper.doPrinting(nodeRoot,settings,monteCarloTreeCreator);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot, settings);
        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(2,2)));
    }

    @SneakyThrows
    @Test
    //  @Ignore
    public void givenStartX3Y3NoSimulationsAndRestrictedActionSetAfterDepth3_then44isOnBestPath() {
        settings = MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .actionSelectionPolicy(ShipPolicies.newOnlyStillAfterDepth3(actionTemplate))
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .maxTreeDepth(20)
                .maxNofIterations(40)  //100k
                .nofSimulationsPerNode(0)
                .weightReturnsSteps(1)
                .coefficientExploitationExploration(100)
                .build();
        createCreator(StateShip.newStateFromXY(3, 3));

        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        SpaceGameTestHelper.doPrinting(nodeRoot,settings,monteCarloTreeCreator);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot, settings);
        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(4,4)));
    }

    @SneakyThrows
    @Test
    public void givenStartX10Y2WithNoSimulationDefensiveBackup_thenX13Y4OnBestPath() {
        settings = MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .actionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .maxTreeDepth(5)
                .maxNofIterations(100)
                .nofSimulationsPerNode(0)
                .weightReturnsSteps(1)
                .isDefensiveBackup(true)
                .alphaBackupDefensiveStep(0.01)  //important
                .discountFactorDefensiveSteps(0.1)
                .build();
        createCreator(StateShip.newStateFromXY(10, 2));

        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        SpaceGameTestHelper.doPrinting(nodeRoot,settings,monteCarloTreeCreator);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot, settings);
        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(13,4)));
    }

    @SneakyThrows
    @Test
    public void givenStartX5Y2Simulations_thenX13Y4OnBestPath() {
        settings = MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .actionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .weightReturnsSteps(0)
                .maxTreeDepth(5)
                .maxNofIterations(100)
                .nofSimulationsPerNode(100)
                .build();
        createCreator(StateShip.newStateFromXY(5, 2));

        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        SpaceGameTestHelper.doPrinting(nodeRoot,settings,monteCarloTreeCreator);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot, settings);
        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(10,4)) ||
                tih.isOnBestPath(StateShip.newStateFromXY(9,4)));
    }

    @SneakyThrows
    @Test
    public void givenStartX0Y2Simulations_thenX4Y4OnBestPath() {
        settings = MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .actionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .weightReturnsSteps(0)
                .maxTreeDepth(5)
                .maxNofIterations(100)
                .nofSimulationsPerNode(100)
                .build();
        createCreator(StateShip.newStateFromXY(0, 2));

        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        SpaceGameTestHelper.doPrinting(nodeRoot,settings,monteCarloTreeCreator);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot, settings);
        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(4,4)));
    }

    private void createCreator(StateShip state) {
        monteCarloTreeCreator = MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(state)
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }




}
