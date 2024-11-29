package mcts_runners.cart_pole;

import common.plotters.PlotterMultiplePanelsTrajectory;
import lombok.SneakyThrows;



import java.util.Arrays;

/**
 * small alphaBackupDefensive seems to give worse convergence
 */

/***
public class RunCartPolePretrainedMemory {
    private static final int BUFFER_SIZE = 1000;
    private static final int BATCH_SIZE = 30;
    private static final double MAX_ERROR = 5e-5;
    private static final int MAX_EPOCHS = 50_000;
    private static final int NOF_STEPS = 1000;
    private static final int TIME_BUDGET_MILLI_SECONDS = 20;


    @SneakyThrows
    public static void main(String[] args) {
        MonteCarloSettings<CartPoleVariables, Integer> settings= createTreeCreatorForTraining();
        CartPoleMemoryTrainer memoryTrainerHelper=new CartPoleMemoryTrainer(BATCH_SIZE, MAX_ERROR, MAX_EPOCHS);
        MonteCarloSimulator<CartPoleVariables, Integer> simulator=
                new MonteCarloSimulator<>(EnvironmentCartPole.newDefault(),settings);
        ReplayBuffer<CartPoleVariables,Integer> buffer=memoryTrainerHelper.createExperienceBuffer(simulator,BUFFER_SIZE);
        CartPoleValueMemoryNetwork<CartPoleVariables,Integer> memory=new CartPoleValueMemoryNetwork<>();
        memoryTrainerHelper.trainMemory(memory, buffer);

        MonteCarloTreeCreator<CartPoleVariables, Integer> mcForSearch= createTreeCreatorForSearch(memory);
        StateInterface<CartPoleVariables> state = getStartState();
        PlotterMultiplePanelsTrajectory plotter=new PlotterMultiplePanelsTrajectory(
                Arrays.asList("root value","nofNodes","maxDepth"),"Iteration");
        CartPoleRunner cpr=new CartPoleRunner(mcForSearch,memory,NOF_STEPS,plotter);
        cpr.run(state);
    }

    @NotNull
    private static StateInterface<CartPoleVariables> getStartState() {
        StateInterface<CartPoleVariables> state= StateCartPole.newAllStatesAsZero();
        state.getVariables().theta=EnvironmentCartPole.THETA_THRESHOLD_RADIANS/2;
        state.getVariables().x=EnvironmentCartPole.X_TRESHOLD*3/4;
        return state;
    }

    public static MonteCarloTreeCreator<CartPoleVariables, Integer> createTreeCreatorForSearch(
            CartPoleValueMemoryNetwork<CartPoleVariables,Integer> memory)
    {
        EnvironmentGenericInterface<CartPoleVariables, Integer> environment = EnvironmentCartPole.newDefault();

        ActionInterface<Integer> actionTemplate=  ActionCartPole.newRandom();
        MonteCarloSettings<CartPoleVariables, Integer> settings= MonteCarloSettings.<CartPoleVariables, Integer>builder()
                .actionSelectionPolicy(CartPolePolicies.newEqualProbability())
                .simulationPolicy(CartPolePolicies.newEqualProbability())
                .isDefensiveBackup(false)
                .maxTreeDepth(50)
                .alphaBackupNormal(1)
                .alphaBackupDefensiveStep(0.99)
                .timeBudgetMilliSeconds(TIME_BUDGET_MILLI_SECONDS)
                .weightReturnsSteps(0)
                .weightMemoryValue(1)
                .weightReturnsSimulation(0)
                .nofSimulationsPerNode(0)
                .coefficientExploitationExploration(0.9)
                .isCreatePlotData(true)
                .build();

        return MonteCarloTreeCreator.<CartPoleVariables, Integer>builder()
                .environment(environment)
                .startState(StateCartPole.newAllStatesAsZero())
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .memory(memory)
                .build();
    }

    public static MonteCarloSettings<CartPoleVariables, Integer> createTreeCreatorForTraining() {
        final int NOF_SIMULATIONS_PER_NODE = 100;
        final int TIME_BUDGET_MILLI_SECONDS = 100;
        return MonteCarloSettings.<CartPoleVariables, Integer>builder()
                .actionSelectionPolicy(CartPolePolicies.newEqualProbability())
                .simulationPolicy(CartPolePolicies.newEqualProbability())
                .isDefensiveBackup(false)
                .coefficientMaxAverageReturn(0.0) //0 <=> average, 1 <=> max
                .maxTreeDepth(100)
                .timeBudgetMilliSeconds(TIME_BUDGET_MILLI_SECONDS)
                .weightReturnsSteps(0)
                .nofSimulationsPerNode(NOF_SIMULATIONS_PER_NODE)
                .build();

    }
}

*/