package mcts_elevator;

import lombok.SneakyThrows;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.elevator.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 *   Insights:
 *   If weightReturnsSteps is zero root node values will go to zero, future step simulations give zero value.
 *   Setting of discountFactorSteps is critical, if not smaller than one, no good solution.
 *   One can also have a small weightReturnsSteps.
 *   The explanation is probably that simulations here is a more reliable signal.
 *   So backups from steps is not really needed but gives "better"/more expected root node values
 *
 *   maxSimulationDepth must be restricted, probably because high values puts the system in fail state, i.e.
 *   backup of future big negative numbers.
 */

public class TestSearchNoActionRestrictionSimulation {

    private static final int SOE_FULL = 1;
    private static final int POS_FLOOR_0 = 0;
    private static final int POS_FLOOR_1 = 10;
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
                .SoE(SOE_FULL).pos(POS_FLOOR_0).nPersonsInElevator(0)
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
    public void whenAtFloor3WaitingFloor1_thenDoesNotManageMoveToFloor1() {
        StateInterface<VariablesElevator> startState = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(SOE_FULL).pos(POS_FLOOR_3).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(1, 0, 0))
                .build());
        monteCarloTreeCreator.setStartState(startState);
        ElevatorTestHelper helper = runSearchAndGetElevatorTestHelper();
        helper.somePrinting();
        List<Integer> posList= helper.getVisitedPositions();
        Assert.assertFalse(posList.contains(POS_FLOOR_1));      //to long horizon to handle
    }

    private ElevatorTestHelper runSearchAndGetElevatorTestHelper() throws monte_carlo_tree_search.exceptions.StartStateIsTrapException {
        NodeWithChildrenInterface<VariablesElevator, Integer> nodeRoot = monteCarloTreeCreator.run();
        return new ElevatorTestHelper(nodeRoot, monteCarloTreeCreator, settings);
    }

    public  MonteCarloTreeCreator<VariablesElevator, Integer> createTreeCreator(StateInterface<VariablesElevator> startState) {
        environment = EnvironmentElevator.newDefault();
        ActionInterface<Integer> actionTemplate=  ActionElevator.newValueDefaultRange(0);
        settings= MonteCarloSettings.<VariablesElevator, Integer>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(ElevatorPolicies.newRandomDirectionAfterStopping())
                .simulationPolicy(ElevatorPolicies.newRandomDirectionAfterStopping())
             //   .isDefensiveBackup(true)  //not crtical
             //   .alphaBackupDefensive(0.9)  //not crtical
                .alphaBackupNormal(1.0)
                .weightReturnsSteps(1.0)
                .discountFactorSteps(0.95)
                .weightReturnsSimulation(1.0)
                .discountFactorSimulation(1.0)
                .coefficientMaxAverageReturn(0) //0 <=> average, 1<=>max
                .maxTreeDepth(20)
                .maxNofIterations(1000)
                .timeBudgetMilliSeconds(500)
                .nofSimulationsPerNode(1)
                .maxSimulationDepth(10)
                .coefficientExploitationExploration(0.1)
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