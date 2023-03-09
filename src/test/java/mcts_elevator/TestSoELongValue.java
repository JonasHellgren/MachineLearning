package mcts_elevator;

import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.classes.SimulationResults;
import monte_carlo_tree_search.domains.elevator.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import org.jcodec.common.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mapdb.Atomic;

import java.util.Arrays;

public class TestSoELongValue {
    private static final int START_DEPTH = 0;
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
    StateInterface<VariablesElevator> stateOneWaitingAtEachFloor;
    StateInterface<VariablesElevator> stateRandom;

    @Before
    public void init() {
        environment = EnvironmentElevator.newDefault();
        StateInterface<VariablesElevator> startStateDummy = StateElevator.newFromVariables(VariablesElevator.builder().build());
        monteCarloTreeCreator=createTreeCreator(startStateDummy);
        stateOneWaitingAtEachFloor= StateElevator.newFromVariables(
                VariablesElevator.builder().nPersonsWaiting(Arrays.asList(1,1,1)).build());
    }

    @Test
    public void givenStartAtBottomWaitingEveryFloor_whenSimulatingLowAndHighSoE_thenHighSoEShallGiveHigherValue() {
        double valueHighSoE = getValueOfSoE(1.0);
        double valueLowSoE = getValueOfSoE(0.5);
        System.out.println("valueHighSoE = " + valueHighSoE+", valueLowSoE = " + valueLowSoE );
        Assert.assertTrue(valueLowSoE<valueHighSoE);
    }

    @Test
    public void givenRandomStartState_whenSimulatingLowAndHighSoE_thenHighSoEShallGiveHigherValue() {
        double valueHighSoE = getValueOfSoERandomStartState(1.0);
        double valueLowSoE = getValueOfSoERandomStartState(0.5);
        System.out.println("valueHighSoE = " + valueHighSoE+", valueLowSoE = " + valueLowSoE );
        Assert.assertTrue(valueLowSoE<valueHighSoE);
    }

    private double getValueOfSoE(double SoE) {
        stateOneWaitingAtEachFloor.getVariables().SoE=SoE;
        SimulationResults simulationResults=monteCarloTreeCreator.simulate(stateOneWaitingAtEachFloor, START_DEPTH);
        double valueSoE=simulationResults.averageReturnFromAll().orElseThrow();
        System.out.println("simulationResults = " + simulationResults);
        return valueSoE;
    }

    private double getValueOfSoERandomStartState(double SoE) {

        stateRandom= StateElevator.newFromVariables(
                VariablesElevator.builder().nPersonsWaiting(Arrays.asList(1,1,1)).build());

        stateRandom.getVariables().SoE=SoE;
        SimulationResults simulationResults=monteCarloTreeCreator.simulate(stateRandom, START_DEPTH);
        double valueSoE=simulationResults.averageReturnFromAll().orElseThrow();
        System.out.println("simulationResults = " + simulationResults);
        return valueSoE;
    }


    public  MonteCarloTreeCreator<VariablesElevator, Integer> createTreeCreator(StateInterface<VariablesElevator> startState) {
        environment = EnvironmentElevator.newDefault();
        ActionInterface<Integer> actionTemplate=  ActionElevator.newValueDefaultRange(0);

        settings= MonteCarloSettings.<VariablesElevator, Integer>builder()
                .actionSelectionPolicy(ElevatorPolicies.newNotUpIfLowSoE())
                .simulationPolicy(ElevatorPolicies.newNotUpIfLowSoE())
                .discountFactorSimulation(1.0)
                .nofSimulationsPerNode(10)
                .maxSimulationDepth(1000)   //20
                .build();

        return MonteCarloTreeCreator.<VariablesElevator, Integer>builder()
                .environment(environment)
                .startState(startState)
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }


}
