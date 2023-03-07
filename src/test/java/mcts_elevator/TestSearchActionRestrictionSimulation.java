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
 * Idea:
 * By using the function nofActionsFunction, a node is regarded as leaf (expandable) only under the condition that the
 * elevator is positioned at a floor. At a floor <=> can test all actions. Not at a floor <=> can test only one action '
 * given by policy.
 *
 * Insights:
 * If action set is restricted and the only action(s) gives fail then actionInSelected is empty -> tree not expanded
 * hence, chooseTestedActionAndBackPropagate will be executed
 * The faction nofNodes/nofIterations gives hint, if small no good tree build
 * maxSimulationDepth must be large enough
 * few branches (averageNofChildrenPerNode is small) -> few iterations needed
 * Nodeselector considers firstActionSelectionPolicy
 * discountFactorSimulationDefensive must not be to small - probably to reject dead end action sequences
 *
 *
 * whenAtBottomFloorAndBadSoEAndWaitingFloor1_thenChargeAndMoveUp needs small coefficientExploitationExploration
 *
 */

public class TestSearchActionRestrictionSimulation {

    private static final double SOE_FULL = 1;
    private static final double SOE_HALF = 0.5;

    private static final int POS_FLOOR_0 = 0;
    private static final int POS_FLOOR_1 = 10;
    private static final int POS_FLOOR_2 = 20;
    private static final int POS_FLOOR_3 = 30;
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
                .speed(0).SoE(SOE_FULL).pos(POS_FLOOR_0).nPersonsInElevator(0).nPersonsWaiting(Arrays.asList(1, 0, 0))
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
                .speed(-1).SoE(SOE_FULL).pos(POS_FLOOR_2).nPersonsInElevator(0).nPersonsWaiting(Arrays.asList(1, 0, 0))
                .build());
        monteCarloTreeCreator.setStartState(startState);
        ElevatorTestHelper helper = runSearchAndGetElevatorTestHelper();
        helper.somePrinting();
        List<Integer> posList= helper.getVisitedPositions();
        Assert.assertTrue(posList.contains(POS_FLOOR_1));      //long horizon to handle
    }

    @SneakyThrows
    @Test
    public void whenAtFloor3WaitingFloor1_thenManageMoveToFloor1() {
        StateInterface<VariablesElevator> startState = StateElevator.newFromVariables(VariablesElevator.builder()
                .speed(-1).SoE(SOE_FULL).pos(POS_FLOOR_3).nPersonsInElevator(0).nPersonsWaiting(Arrays.asList(1, 0, 0))
                .build());
        monteCarloTreeCreator.setStartState(startState);
        ElevatorTestHelper helper = runSearchAndGetElevatorTestHelper();
        helper.somePrinting();
        List<Integer> posList= helper.getVisitedPositions();
        Assert.assertTrue(posList.contains(POS_FLOOR_1));      //long horizon to handle
    }

    @SneakyThrows
    @Test
    public void whenAtFloor1AndBadSoE_thenDoNotMoveUpInitially() {
        StateInterface<VariablesElevator> startState = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(0.21).pos(POS_FLOOR_1).nPersonsInElevator(0).nPersonsWaiting(Arrays.asList(0, 0, 0))
                .build());
        monteCarloTreeCreator.setStartState(startState);
        ElevatorTestHelper helper = runSearchAndGetElevatorTestHelper();
        helper.somePrinting();
        Assert.assertNotEquals(1, (int) monteCarloTreeCreator.getFirstAction().getValue());
    }

    @SneakyThrows
    @Test
    public void whenAtBottomFloorAndBadSoEAndWaitingFloor1_thenChargeAndMoveUp() {
        StateInterface<VariablesElevator> startState = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(0.3).pos(POS_FLOOR_0).nPersonsInElevator(0).nPersonsWaiting(Arrays.asList(1, 0, 0))
                .build());
        settings.setCoefficientExploitationExploration(1);
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
                .speed(1).SoE(SOE_FULL).pos(8).nPersonsInElevator(0).nPersonsWaiting(Arrays.asList(1, 0, 0))
                .build());
        monteCarloTreeCreator.setStartState(startState);
        ElevatorTestHelper helper = runSearchAndGetElevatorTestHelper();
        helper.somePrinting();

        List<Integer> nPers= helper.getnPersWaiting();
        Assert.assertTrue(nPers.contains(0));
    }

    @SneakyThrows
    @Test
    public void whenAtPos22ndPersonInElevatorAndWaitingFloor2And3_thenPickUpBoth() {
        StateInterface<VariablesElevator> startState = StateElevator.newFromVariables(VariablesElevator.builder()
                .speed(1).SoE(SOE_FULL).pos(22).nPersonsInElevator(1).nPersonsWaiting(Arrays.asList(0, 1, 1))
                .build());
        monteCarloTreeCreator.setStartState(startState);
        ElevatorTestHelper helper = runSearchAndGetElevatorTestHelper();
        helper.somePrinting();

        List<Integer> nPers= helper.getnPersWaiting();
        Assert.assertTrue(nPers.contains(0));
    }

    @SneakyThrows
    @Test
    public void whenAtPos8AndWaitingFloor3AndHalfSoE_thenNoPickUp() {
        StateInterface<VariablesElevator> startState = StateElevator.newFromVariables(VariablesElevator.builder()
                .speed(1).SoE(SOE_HALF).pos(8).nPersonsInElevator(1).nPersonsWaiting(Arrays.asList(0, 0, 1))
                .build());
        monteCarloTreeCreator.setStartState(startState);
        ElevatorTestHelper helper = runSearchAndGetElevatorTestHelper();
        helper.somePrinting();

        List<Integer> nPers= helper.getnPersWaiting();
        Assert.assertFalse(nPers.contains(0));
    }


    @SneakyThrows
    @Test
    public void whenAtPos8AndWaitingFloor3AndFullSoE_thenPickUp() {
        StateInterface<VariablesElevator> startState = StateElevator.newFromVariables(VariablesElevator.builder()
                .speed(1).SoE(SOE_FULL).pos(8).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(0, 0, 1))
                .build());
        monteCarloTreeCreator.setStartState(startState);
        ElevatorTestHelper helper = runSearchAndGetElevatorTestHelper();
        helper.somePrinting();

        List<Integer> nPers= helper.getnPersWaiting();
        Assert.assertTrue(nPers.contains(0));
    }

    @SneakyThrows
    @Test
    public void whenAtFloor1AndPersonInElevatorAndNoWaiting_thenLeaveAtBottomFloor() {
        StateInterface<VariablesElevator> startState = StateElevator.newFromVariables(VariablesElevator.builder()
                .speed(0).SoE(SOE_HALF).pos(POS_FLOOR_1).nPersonsInElevator(1).nPersonsWaiting(Arrays.asList(0, 0, 0))
                .build());
        monteCarloTreeCreator.setStartState(startState);
        ElevatorTestHelper helper = runSearchAndGetElevatorTestHelper();
        helper.somePrinting();

        List<Integer> nPers= helper.getnPersWaiting();
        List<Integer> posList= helper.getVisitedPositions();
        Assert.assertTrue(posList.contains(POS_FLOOR_0));
        Assert.assertTrue(nPers.contains(0));
    }

    private ElevatorTestHelper runSearchAndGetElevatorTestHelper() throws monte_carlo_tree_search.exceptions.StartStateIsTrapException {
        NodeWithChildrenInterface<VariablesElevator, Integer> nodeRoot = monteCarloTreeCreator.run();
      //  nodeRoot.printTree();
        return new ElevatorTestHelper(nodeRoot, monteCarloTreeCreator, settings);
    }

    public  MonteCarloTreeCreator<VariablesElevator, Integer> createTreeCreator(StateInterface<VariablesElevator> startState) {
        environment = EnvironmentElevator.newDefault();
        ActionInterface<Integer> actionTemplate=  ActionElevator.newValueDefaultRange(0);

        Function<VariablesElevator,Integer> nofActionsFunction  =
                (a) -> EnvironmentElevator.isAtFloor.test(a.speed,a.pos)
                ?actionTemplate.applicableActions().size()
                :1;

        settings= MonteCarloSettings.<VariablesElevator, Integer>builder()
                .firstActionSelectionPolicy(ElevatorPolicies.newRandomDirectionAfterStopping())
                .simulationPolicy(ElevatorPolicies.newRandomDirectionAfterStopping())
                .discountFactorSteps(0.9)
                .discountFactorSimulationDefensive(0.9)
                .maxTreeDepth(30)
                .maxNofIterations(10_000)
                .timeBudgetMilliSeconds(200)
                .nofSimulationsPerNode(5)
                .maxSimulationDepth(50)   //20
                .coefficientExploitationExploration(100)  //1e6
                .build();

        return MonteCarloTreeCreator.<VariablesElevator, Integer>builder()
                .environment(environment)
                .startState(startState)
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

}
