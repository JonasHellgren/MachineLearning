package mcts_elevator;

import lombok.SneakyThrows;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.elevator.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/***
 * Insights:
 * If action set is restricted and the only action(s) gives fail then actionInSelected is empty -> tree not expanded
 * maxSimulationDepth not allowed to be to large
 * coefficientExploitationExploration needs to be very large
 * small ratio nofNodes/nodIterations
 * few branches (averageNofChildrenPerNode is small) -> few iterations needed
 *
 *  *   Nodeselector considers firstActionSelectionPolicy
 */

public class TestSearchActionRestrictionSimulation {

    private static final int SOE_FULL = 1;
    private static final int POS_FLOOR_0 = 0;
    private static final int POS_FLOOR_1 = 10;
    private static final int POS_FLOOR_2 = 20;
    private static final int NSTEPS_BETWEEN = 50;

    EnvironmentGenericInterface<VariablesElevator, Integer> environment;
    MonteCarloTreeCreator<VariablesElevator, Integer> monteCarloTreeCreator;
    MonteCarloSettings<VariablesElevator, Integer> settings;

    @Before
    public void init() {
        environment = EnvironmentElevator.newFromStepBetweenAddingNofWaiting
                (Arrays.asList(NSTEPS_BETWEEN,NSTEPS_BETWEEN,NSTEPS_BETWEEN));
        StateInterface<VariablesElevator> startStateDummy = StateElevator.newFromVariables(VariablesElevator.builder().build());
        monteCarloTreeCreator=createTreeCreator(startStateDummy);
    }

    @SneakyThrows
    @Test
    public void whenAtBottomFloorAndPersonsWaitingFloor1_thenMoveToFloor1() {
        StateInterface<VariablesElevator> startState = StateElevator.newFromVariables(VariablesElevator.builder()
                .speed(0).SoE(SOE_FULL).pos(POS_FLOOR_0).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(1, 0, 0))
                .build());
        monteCarloTreeCreator.setStartState(startState);
        ElevatorTestHelper helper = runSearchAndGetElevatorTestHelper();
        helper.somePrinting();
        List<Integer> posList= helper.getVisitedPositions();
        Assert.assertTrue(posList.contains(POS_FLOOR_1));
        Assert.assertEquals(1, (int) monteCarloTreeCreator.getFirstAction().getValue());
    }

    @SneakyThrows
    @Test
    public void whenAtFloor2WaitingFloor1_thenManageMoveToFloor1() {
        StateInterface<VariablesElevator> startState = StateElevator.newFromVariables(VariablesElevator.builder()
                .speed(-1).SoE(SOE_FULL).pos(POS_FLOOR_2).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(5, 0, 0))
                .build());
        monteCarloTreeCreator.setStartState(startState);
        ElevatorTestHelper helper = runSearchAndGetElevatorTestHelper();
        helper.somePrinting();
        List<Integer> posList= helper.getVisitedPositions();
        Assert.assertTrue(posList.contains(POS_FLOOR_1));      //long horizon to handle
    }

    @SneakyThrows
    @Test
    public void whenAtFloor1AndBadSoE_thenDoNotMoveUp() {
        StateInterface<VariablesElevator> startState = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(0.21).pos(POS_FLOOR_1).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(0, 0, 0))
                .build());
        monteCarloTreeCreator.setStartState(startState);
        ElevatorTestHelper helper = runSearchAndGetElevatorTestHelper();
        helper.somePrinting();
        List<Integer> posList= helper.getVisitedPositions();
        Assert.assertFalse(posList.contains(POS_FLOOR_1+1));
    }

    @SneakyThrows
    @Test
    public void whenAtBottomFloorAndBadSoEAndWaitingFloor1_thenChargeAndMoveUp() {
        StateInterface<VariablesElevator> startState = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(0.35).pos(POS_FLOOR_0).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(1, 0, 0))
                .build());
        monteCarloTreeCreator.setStartState(startState);
        ElevatorTestHelper helper = runSearchAndGetElevatorTestHelper();
        helper.somePrinting();
        List<Integer> posList= helper.getVisitedPositions();
        Assert.assertTrue(posList.contains(POS_FLOOR_1));
    }

    @SneakyThrows
    @Test
    public void whenAtPos8AndWaitingFloor1_thenPickUp() {
        StateInterface<VariablesElevator> startState = StateElevator.newFromVariables(VariablesElevator.builder()
                .speed(1).SoE(SOE_FULL).pos(8).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(1, 0, 0))
                .build());
        monteCarloTreeCreator.setStartState(startState);
        ElevatorTestHelper helper = runSearchAndGetElevatorTestHelper();
        helper.somePrinting();

        List<Integer> nPers= helper.getnPersWaiting();
        Assert.assertTrue(nPers.contains(0));
    }

    private ElevatorTestHelper runSearchAndGetElevatorTestHelper() throws monte_carlo_tree_search.exceptions.StartStateIsTrapException {
        NodeWithChildrenInterface<VariablesElevator, Integer> nodeRoot = monteCarloTreeCreator.run();
       // nodeRoot.printTree();
        return new ElevatorTestHelper(nodeRoot, monteCarloTreeCreator, settings);
    }

    public  MonteCarloTreeCreator<VariablesElevator, Integer> createTreeCreator(StateInterface<VariablesElevator> startState) {
        environment = EnvironmentElevator.newDefault();
        ActionInterface<Integer> actionTemplate=  ActionElevator.newValueDefaultRange(0);

        Function<VariablesElevator,Integer> nofActionsFunction  =
                (a) -> EnvironmentElevator.isAtFloor.or(EnvironmentElevator.isBottomFloor).test(a.speed,a.pos)
                ?actionTemplate.applicableActions().size()
                :1;

        settings= MonteCarloSettings.<VariablesElevator, Integer>builder()
                .maxNofTestedActionsForBeingLeafFunction(nofActionsFunction)
                .firstActionSelectionPolicy(ElevatorPolicies.newRandomDirectionAfterStopping())
                .simulationPolicy(ElevatorPolicies.newRandomDirectionAfterStopping())
                .isDefensiveBackup(true)
                .alphaBackupDefensiveStep(0.9)  //0.1
                .alphaBackupDefensiveSimulation(0.9)
                .alphaBackupNormal(1.0)
                .weightReturnsSteps(0.0)
                .discountFactorSteps(1.0)
                .weightReturnsSimulation(1.0)
                .discountFactorSimulation(1.0)
                .discountFactorSimulationDefensive(0.5)
                .coefficientMaxAverageReturn(0) //0 <=> average, 1<=>max
                .maxTreeDepth(30)  //30
                .maxNofIterations(10_000)  //100_000
                .timeBudgetMilliSeconds(1_000)
                .nofSimulationsPerNode(5)
                .maxSimulationDepth(20)   //20
                .coefficientExploitationExploration(1e6)  //1e6
                .isCreatePlotData(false)
                .build();

        return MonteCarloTreeCreator.<VariablesElevator, Integer>builder()
                .environment(environment)
                .startState(startState)
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

}
