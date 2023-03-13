package mcts_cart_pole_runner;

import common.MultiplePanelsPlotter;
import lombok.SneakyThrows;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.cart_pole.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.domains.cart_pole.CartPoleStateValueMemory;
import monte_carlo_tree_search.domains.cart_pole.CartPoleMemoryTrainerHelper;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class RunCartPolePretrainedMemoryMinusOneRewardAtFail {
    private static final int FAIL_REWARD = -10;
    private static final int NON_FAIL_REWARD = 0;
    private static final double DISCOUNT_FACTOR = 0.95;  //0.95

    private static final int BUFFER_SIZE = 1000;
    private static final int BATCH_SIZE = 30;
    private static final double MAX_ERROR = 3e-4;
    private static final int MAX_EPOCHS = 50_000;
    private static final int NOF_STEPS = 10_000;
    private static final int TIME_BUDGET_MILLI_SECONDS = 20;


    @SneakyThrows
    public static void main(String[] args) {
        MonteCarloTreeCreator<CartPoleVariables, Integer> mcForTraining= createTreeCreatorForTraining();
        CartPoleMemoryTrainerHelper memoryTrainerHelper=new CartPoleMemoryTrainerHelper(BATCH_SIZE,BUFFER_SIZE, MAX_ERROR, MAX_EPOCHS);
        ReplayBuffer<CartPoleVariables,Integer> buffer=memoryTrainerHelper.createExperienceBuffer(mcForTraining);
        CartPoleStateValueMemory<CartPoleVariables> memory=new CartPoleStateValueMemory<>(FAIL_REWARD,NON_FAIL_REWARD);
        memoryTrainerHelper.trainMemory(memory, buffer);

        MonteCarloTreeCreator<CartPoleVariables, Integer> mcForSearch= createTreeCreatorForSearch(memory);
        StateInterface<CartPoleVariables> state = getStartState();
        MultiplePanelsPlotter plotter=new MultiplePanelsPlotter(
                Arrays.asList("root value","nofNodes","maxDepth"),"Iteration");
        CartPoleRunner cpr=new CartPoleRunner(mcForSearch,memory,NOF_STEPS,plotter);
        cpr.run(state);

    }

    @NotNull
    private static StateInterface<CartPoleVariables> getStartState() {
        StateInterface<CartPoleVariables> state= StateCartPole.newAllStatesAsZero();
        state.getVariables().theta=EnvironmentCartPole.THETA_THRESHOLD_RADIANS/200;
        state.getVariables().x=EnvironmentCartPole.X_TRESHOLD*3/4;
        return state;
    }

    public static MonteCarloTreeCreator<CartPoleVariables, Integer> createTreeCreatorForSearch(
            CartPoleStateValueMemory<CartPoleVariables> memory)
    {
        EnvironmentGenericInterface<CartPoleVariables, Integer> environment = createEnvironment();

        ActionInterface<Integer> actionTemplate=  ActionCartPole.newRandom();
        MonteCarloSettings<CartPoleVariables, Integer> settings= MonteCarloSettings.<CartPoleVariables, Integer>builder()
                .actionSelectionPolicy(CartPolePolicies.newEqualProbability())
                .simulationPolicy(CartPolePolicies.newEqualProbability())
                .isDefensiveBackup(false)
                .maxTreeDepth(30)
                .alphaBackupNormal(1)
                .alphaBackupDefensiveStep(0.9)
                .timeBudgetMilliSeconds(TIME_BUDGET_MILLI_SECONDS)
                .weightReturnsSteps(0)
                .weightMemoryValue(1)
                .weightReturnsSimulation(0)
                .nofSimulationsPerNode(0)
                .discountFactorBackupSimulationNormal(DISCOUNT_FACTOR)
                .discountFactorSimulation(DISCOUNT_FACTOR)
                .coefficientExploitationExploration(0.3)
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

    public static MonteCarloTreeCreator<CartPoleVariables, Integer> createTreeCreatorForTraining() {
        EnvironmentGenericInterface<CartPoleVariables, Integer> environment = createEnvironment();
        ActionInterface<Integer> actionTemplate=  ActionCartPole.newRandom();
        MonteCarloSettings<CartPoleVariables, Integer> settings= MonteCarloSettings.<CartPoleVariables, Integer>builder()
                .actionSelectionPolicy(CartPolePolicies.newEqualProbability())
                .simulationPolicy(CartPolePolicies.newEqualProbability())
                .isDefensiveBackup(false)
                .coefficientMaxAverageReturn(0.0) //0 <=> average, 1 <=> max
                .maxTreeDepth(100)
                .timeBudgetMilliSeconds(100)
                .weightReturnsSteps(0)
                .discountFactorBackupSimulationNormal(DISCOUNT_FACTOR)
                .discountFactorSimulation(DISCOUNT_FACTOR)
                .nofSimulationsPerNode(100)
                .build();

        return MonteCarloTreeCreator.<CartPoleVariables, Integer>builder()
                .environment(environment)
                .startState(StateCartPole.newAllStatesAsZero())
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

    private static EnvironmentGenericInterface<CartPoleVariables, Integer> createEnvironment() {
        return EnvironmentCartPole.builder()
                .nonFailReward(NON_FAIL_REWARD)
                .failReward(FAIL_REWARD)
                .build();
    }

}
