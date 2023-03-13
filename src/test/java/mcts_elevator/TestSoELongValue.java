package mcts_elevator;

import common.ListUtils;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloSimulator;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.classes.SimulationResults;
import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.domains.elevator.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import org.jcodec.common.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestSoELongValue {
    private static final int START_DEPTH = 0;
    private static final int MAX_N_PERSONS_IN_ELEVATOR = 0;
    private static final int MAX_N_PERSONS_WAITING_TOTAL = 1;
    private static final int NOF_RANDOM_STATES = 100;
    private static final int NOF_SIMULATIONS_PER_NODE = 10;
    private static final double SOE_LOW = 0.5;
    private static final double SOE_HIGH = 1.0;
    private static final int MAX_SIMULATION_DEPTH = 1000;

    EnvironmentGenericInterface<VariablesElevator, Integer> environment;
    MonteCarloTreeCreator<VariablesElevator, Integer> monteCarloTreeCreator;
    MonteCarloSettings<VariablesElevator, Integer> settings;
    StateInterface<VariablesElevator> stateOneWaitingAtEachFloor;
    StateInterface<VariablesElevator> stateRandom;
    MonteCarloSimulator<VariablesElevator, Integer> simulator;

    @Before
    public void init() {
        environment = EnvironmentElevator.newDefault();
        StateInterface<VariablesElevator> startStateDummy = StateElevator.newFromVariables(VariablesElevator.builder().build());
        monteCarloTreeCreator = createTreeCreator(startStateDummy);
        stateOneWaitingAtEachFloor = StateElevator.newFromVariables(
                VariablesElevator.builder().nPersonsWaiting(Arrays.asList(1, 1, 1)).build());
        simulator=new MonteCarloSimulator<>(environment,settings);
    }

    @Test
    public void givenStartAtBottomWaitingEveryFloor_whenSimulatingLowAndHighSoE_thenHighSoEShallGiveHigherValue() {
        double valueHighSoE = getValueOfSoE(SOE_HIGH);
        double valueLowSoE = getValueOfSoE(SOE_LOW);
        System.out.println("valueHighSoE = " + valueHighSoE + ", valueLowSoE = " + valueLowSoE);
        Assert.assertTrue(valueLowSoE < valueHighSoE);
    }

    @Test
    public void givenMultipleRandomStartState_whenSimulatingLowAndHighSoE_thenHighSoEShallGiveHigherValue() {
        List<Double> valueSoEHighList = new ArrayList<>();
        List<Double> valueSoELowList = new ArrayList<>();

        for (int i = 0; i < NOF_RANDOM_STATES; i++) {
            stateRandom = StateElevator.newFromVariables(
                    VariablesElevator.newRandom(MAX_N_PERSONS_IN_ELEVATOR, MAX_N_PERSONS_WAITING_TOTAL));
            double valueHighSoE = getValueOfSoERandomStartStates(SOE_HIGH, stateRandom);
            double valueLowSoE = getValueOfSoERandomStartStates(SOE_LOW, stateRandom);
            valueSoEHighList.add(valueHighSoE);
            valueSoELowList.add(valueLowSoE);
        }

        double highSoEAvg = ListUtils.findAverage(valueSoEHighList).orElseThrow();
        double lowSoEAvg = ListUtils.findAverage(valueSoELowList).orElseThrow();

        System.out.println("highSoEAvg = " + highSoEAvg + ", lowSoEAvg = " + lowSoEAvg);
        Assert.assertTrue(lowSoEAvg < highSoEAvg);
    }


    private double getValueOfSoE(double SoE) {
        stateOneWaitingAtEachFloor.getVariables().SoE = SoE;
        SimulationResults simulationResults = simulator.simulate(stateOneWaitingAtEachFloor, START_DEPTH);
        double valueSoE = simulationResults.averageReturnFromAll().orElseThrow();
        System.out.println("simulationResults = " + simulationResults);
        return valueSoE;
    }

    private double getValueOfSoERandomStartStates(double SoE, StateInterface<VariablesElevator> stateRandom) {
        stateRandom.getVariables().SoE = SoE;
        SimulationResults simulationResults = simulator.simulate(stateRandom, START_DEPTH);
        //  System.out.println("simulationResults = " + simulationResults);
        return simulationResults.averageReturnFromAll().orElseThrow();

    }


    public MonteCarloTreeCreator<VariablesElevator, Integer> createTreeCreator(StateInterface<VariablesElevator> startState) {
        environment = EnvironmentElevator.newDefault();
        ActionInterface<Integer> actionTemplate = ActionElevator.newValueDefaultRange(0);

        settings = MonteCarloSettings.<VariablesElevator, Integer>builder()
                .actionSelectionPolicy(ElevatorPolicies.newNotUpIfLowSoE())
                .simulationPolicy(ElevatorPolicies.newNotUpIfLowSoE())
                .discountFactorSimulation(1.0)
                .nofSimulationsPerNode(NOF_SIMULATIONS_PER_NODE)
                .maxSimulationDepth(MAX_SIMULATION_DEPTH)   //20
                .build();

        return MonteCarloTreeCreator.<VariablesElevator, Integer>builder()
                .environment(environment)
                .startState(startState)
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }


}
