package monte_carlo_search.mcts_energy_trading;

import monte_carlo_tree_search.models_and_support_classes.SimulationResults;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloSimulator;
import monte_carlo_tree_search.domains.energy_trading.EnvironmentEnergyTrading;
import monte_carlo_tree_search.domains.energy_trading.PoliciesEnergyTrading;
import monte_carlo_tree_search.domains.energy_trading.StateEnergyTrading;
import monte_carlo_tree_search.domains.energy_trading.VariablesEnergyTrading;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestSimulate {
    private static final int NOF_SIMULATIONS = 100;
    private static final int MAX_SIMULATION_DEPTH = 10;
    EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment;
    StateInterface<VariablesEnergyTrading> state;
    MonteCarloSimulator<VariablesEnergyTrading,Integer> simulator;

    @Before
    public void init() {
        environment= EnvironmentEnergyTrading.newDefault();
        state= StateEnergyTrading.newDefault();
        MonteCarloSettings<VariablesEnergyTrading, Integer> settingsMemoryCreation= createSimulatorSettings();
        simulator= new MonteCarloSimulator<>(environment,settingsMemoryCreation);
    }

    @Test
    public void whenSimulating_thenResults() {
        SimulationResults results=simulator.simulate(state);
        System.out.println("results = " + results);
        Assert.assertEquals(NOF_SIMULATIONS,results.size());
    }


    @Test
    public void whenSimulatingFromGoodAndBadState_thenGoodGivesBetterAverageReturnOfNonFailEpisodes() {

        StateInterface<VariablesEnergyTrading> stateGood=StateEnergyTrading.newFromTimeAndSoE(5,0.7);
        StateInterface<VariablesEnergyTrading> stateBad=StateEnergyTrading.newFromTimeAndSoE(5,0.3);
        SimulationResults resultsGood=simulator.simulate(stateGood);
        SimulationResults resultsBad=simulator.simulate(stateBad);

        double avgGood=resultsGood.averageReturnFromNonFailing().orElseThrow();
        double avgBad=resultsBad.averageReturnFromNonFailing().orElseThrow();

        System.out.println("resultsGood = " + resultsGood);
        System.out.println("resultsBad = " + resultsBad);

        System.out.println("avgGood = " + avgGood);
        System.out.println("avgBad = " + avgBad);

        Assert.assertTrue(avgGood>avgBad);

    }


    public static MonteCarloSettings<VariablesEnergyTrading, Integer> createSimulatorSettings() {

        return MonteCarloSettings.<VariablesEnergyTrading, Integer>builder()
                .actionSelectionPolicy(PoliciesEnergyTrading.newRandom())
                .simulationPolicy(PoliciesEnergyTrading.newRandom())
                .discountFactorSimulation(1.0)
                .nofSimulationsPerNode(NOF_SIMULATIONS)
                .maxSimulationDepth(MAX_SIMULATION_DEPTH)
                .build();
    }


}
