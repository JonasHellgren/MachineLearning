package mcts_energy_trading;

import monte_carlo_tree_search.classes.SimulationResults;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloSimulator;
import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.domains.energy_trading.*;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.MemoryTrainerInterface;
import monte_carlo_tree_search.interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestEnergyTraderMemory {
    private static final int NOF_SIMULATIONS = 100;
    private static final int MAX_SIMULATION_DEPTH = 10;
    private static final int BUFFER_SIZE = 1000;
    private static final int MINI_BATCH_SIZE = 20;
    private static final double MAX_ERROR = 1e-4;
    private static final int MAX_NOF_EPOCHS = 10_000;

    EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment;
    MonteCarloSimulator<VariablesEnergyTrading,Integer> simulator;
    MemoryTrainerInterface<VariablesEnergyTrading, Integer> trainer;
    NetworkMemoryInterface<VariablesEnergyTrading, Integer> memory;
    ReplayBuffer<VariablesEnergyTrading,Integer> buffer;

    @Before
    public void init() {
        environment= EnvironmentEnergyTrading.newDefault();
        MonteCarloSettings<VariablesEnergyTrading, Integer> settingsMemoryCreation= createSimulatorSettings();
        simulator= new MonteCarloSimulator<>(environment,settingsMemoryCreation);
        trainer=new EnergyTraderMemoryTrainer(MINI_BATCH_SIZE, BUFFER_SIZE, MAX_ERROR, MAX_NOF_EPOCHS);
        memory=new EnergyTraderValueMemory<>();
        buffer=trainer.createExperienceBuffer(simulator);
    }

    @Test
    public void whenSimulating_thenResults() {
        SimulationResults results=simulator.simulate(StateEnergyTrading.newDefault());
        System.out.println("results = " + results);
        Assert.assertEquals(NOF_SIMULATIONS,results.size());
    }

    @Test public void givenBuffer_thenCorrectSize() {
        System.out.println("buffer = " + buffer);
        Assert.assertEquals(BUFFER_SIZE,buffer.size());
        Assert.assertEquals(MINI_BATCH_SIZE,buffer.getMiniBatch(MINI_BATCH_SIZE).size());
    }

    @Test public void givenBuffer_whenTrainingMemory_thenCorrect() {

        trainer.trainMemory(memory,buffer);




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
