package mcts_spacegame;

import common.MathUtils;
import lombok.SneakyThrows;
import monte_carlo_tree_search.classes.*;
import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.helpers.NodeInfoHelper;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

public class Test_5times15gridCorridor {
    private static final int MAX_NOF_ITERATIONS = 1500;
    private static final int NOF_SIMULATIONS_PER_NODE = 1000;  //important
    private static final int MAX_TREE_DEPTH = 15;
    private static final int COEFFICIENT_EXPLOITATION_EXPLORATION = 100;
    private static final double DELTA = 0.5;
    private static final double BONUS_6 = 6;
    private static final double BONUS_3 = 3;

    MonteCarloTreeCreator<ShipVariables, ShipActionSet> monteCarloTreeCreator;
    EnvironmentGenericInterface<ShipVariables, ShipActionSet> environment;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;
    ActionInterface<ShipActionSet> actionTemplate;
    NodeValueMemoryHashMap<ShipVariables> memory;
    MonteCarloSimulator<ShipVariables, ShipActionSet> simulator;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new5times15GridCorridor();
        environment = new EnvironmentShip(spaceGrid);
        actionTemplate=new ActionShip(ShipActionSet.notApplicable); //whatever action
        settings=settingsForSimulations();
        monteCarloTreeCreator=MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0,2))
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
        simulator=new MonteCarloSimulator<>(environment,settings);
    }

    @Test
    public void printEnvironment() {
        System.out.println("environment = " + environment);
        System.out.println("memory = " + memory);
    }

    @Test public void whenMoveFromX13Y4IntoGoal_thenReturnWithHighValue() {
        settings=settingsForSimulations();
        SimulationResults simulationResults= simulator.simulate(StateShip.newStateFromXY(13,4));
        boolean any6 = anySimulationHasReturn6(simulationResults);
        System.out.println("simulationResults = " + simulationResults);
        Assert.assertTrue(any6);
    }

    private boolean anySimulationHasReturn6(SimulationResults simulationResults) {
        return simulationResults.getResults().stream().map(r -> r.singleReturn).anyMatch(v -> MathUtils.isZero(v - BONUS_6));
    }

    @Test public void whenMoveFromX5Y4IntoGoal_thenReturnWithHighValue() {
        settings=settingsForSimulations();
        SimulationResults simulationResults= simulator.simulate(StateShip.newStateFromXY(5,4));
        boolean any6 = anySimulationHasReturn6(simulationResults);
        System.out.println("simulationResults = " + simulationResults);
        Assert.assertTrue(any6);
    }

    @SneakyThrows
    @Test
    public void whenMoveFromX0Y2_thenMovesNorth() {
        settings=settingsForSimulations();
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        SpaceGameTestHelper.doPrinting(nodeRoot,settings,monteCarloTreeCreator);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(4,4)));
        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(5,4)));
    }

    @SneakyThrows
    @Test
    @Ignore
    public void whenMoveFromX0Y2ManyTimes_thenAlwaysMovesNorth() {
        for (int i = 0; i < 10 ; i++) {
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);

        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(4,4)));
        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(5,4)));
        }
    }

    @SneakyThrows
    @Test
    public void givenHighBonusInX14Y0_thenSouthRoute() {
        settings=settingsForSimulations();
        EnvironmentShip env=(EnvironmentShip) monteCarloTreeCreator.getEnvironment();
        SpaceGrid spaceGrid=env.getSpaceGrid();
        SpaceCell cell=spaceGrid.getCell(14,0).orElseThrow();
        cell.bonus=10;

        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        SpaceGameTestHelper.doPrinting(nodeRoot,settings,monteCarloTreeCreator);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(4,0)));
        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(5,0)));

    }

    @SneakyThrows
    @Test
    public void whenMoveFromX10Y4_thenBonus6InEndAndX13Y4OnBestPath() {

        settings=settingsForNoSimulations();
        monteCarloTreeCreator=treeCreator(StateShip.newStateFromXY(10,4));

        NodeWithChildrenInterface<ShipVariables, ShipActionSet>  nodeRoot = monteCarloTreeCreator.run();
        SpaceGameTestHelper.doPrinting(nodeRoot,settings,monteCarloTreeCreator);
        TreeInfoHelper<ShipVariables, ShipActionSet>  tih=new TreeInfoHelper<>(nodeRoot,settings);
        assertStateIsOnBestPath(tih, StateShip.newStateFromXY(11,4));

        Optional<NodeInterface<ShipVariables, ShipActionSet>> node=
                tih.getNodeReachedForActions(Collections.singletonList(ActionShip.newStill()));
        System.out.println("node = " + node);

        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(13,4)));
        Assert.assertEquals(BONUS_6,node.orElseThrow().getActionValue(ActionShip.newStill()), DELTA);
    }

    @SneakyThrows
    @Test
    public void whenMoveFromX0Y2_thenX13Y4OnBestPath() {

        settings=settingsForNoSimulations();
        monteCarloTreeCreator=treeCreator(StateShip.newStateFromXY(0,2));

        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        SpaceGameTestHelper.doPrinting(nodeRoot,settings,monteCarloTreeCreator);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);

        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(11,4)));
        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(13,4)));
    }


    @SneakyThrows
    @Test
    public void whenFromX0Y2WithNoSimulationsAndLowExploration_thenSubOptimalPath() {

        settings=settingsForNoSimulations();
        settings.setCoefficientExploitationExploration(1);
        monteCarloTreeCreator=treeCreator(StateShip.newStateFromXY(0,2));

        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot = monteCarloTreeCreator.run();
        SpaceGameTestHelper.doPrinting(nodeRoot,settings,monteCarloTreeCreator);
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);

        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(11,2)));
        Assert.assertTrue(tih.isOnBestPath(StateShip.newStateFromXY(13,2)));

        SpaceGameTestHelper.doPrinting(nodeRoot,settings,monteCarloTreeCreator);
    }

    private MonteCarloTreeCreator<ShipVariables, ShipActionSet> treeCreator(StateShip state) {
        return MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(state)
                .monteCarloSettings(settings)
                .memory(memory)
                .actionTemplate(actionTemplate)
                .build();
    }

    private MonteCarloSettings<ShipVariables, ShipActionSet> settingsForSimulations() {
        return MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .actionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .coefficientMaxAverageReturn(1) //only max
                .maxTreeDepth(MAX_TREE_DEPTH)
                .maxNofIterations(MAX_NOF_ITERATIONS)
                .nofSimulationsPerNode(NOF_SIMULATIONS_PER_NODE)
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)
                .weightReturnsSteps(0)  //important
                .build();
    }

    private MonteCarloSettings<ShipVariables, ShipActionSet> settingsForNoSimulations() {
        return MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .actionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .coefficientMaxAverageReturn(1) //only max
                .maxTreeDepth(MAX_TREE_DEPTH)
                .maxNofIterations(2_000)  //important
                .nofSimulationsPerNode(0)
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)
        //        .isDefensiveBackup(true)
        //        .alphaBackupDefensiveStep(0.99)
        //        .discountFactorDefensiveSteps(0.99)
                .build();
    }

    private void assertStateIsOnBestPath(TreeInfoHelper<ShipVariables, ShipActionSet> tih, StateShip state) {
        Optional<NodeInterface<ShipVariables, ShipActionSet>> node=
                NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(), state);
        Assert.assertTrue(node.isPresent());
    }




}
