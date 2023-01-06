package mcts_cart_pole_runner;

import lombok.SneakyThrows;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.cart_pole.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.network_training.CartPoleStateValueMemory;
import monte_carlo_tree_search.network_training.MemoryTrainerHelper;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import org.jetbrains.annotations.NotNull;

public class RunCartPolePretrainedMemory {
    private static final int BUFFER_SIZE = 1000;
    private static final int BATCH_SIZE = 30;
    private static final double  MAX_ERROR = 5e-5;
    private static final int MAX_EPOCHS = 50_000;
    private static final int NOF_STEPS = 1000;
    private static final int TIME_BUDGET_MILLI_SECONDS = 50;


    @SneakyThrows
    public static void main(String[] args) {
        MonteCarloTreeCreator<CartPoleVariables, Integer> mcForTraining= createTreeCreatorForTraining();
        MemoryTrainerHelper memoryTrainerHelper=new MemoryTrainerHelper(BATCH_SIZE,BUFFER_SIZE, MAX_ERROR, MAX_EPOCHS);
        ReplayBuffer<CartPoleVariables,Integer> buffer=memoryTrainerHelper.createExperienceBuffer(mcForTraining);
        CartPoleStateValueMemory<CartPoleVariables> memory=new CartPoleStateValueMemory<>(EnvironmentCartPole.MAX_NOF_STEPS_DEFAULT);
        memoryTrainerHelper.trainMemory(memory, buffer);

        MonteCarloTreeCreator<CartPoleVariables, Integer> mcForSearch= createTreeCreatorForSearch(memory);
        StateInterface<CartPoleVariables> state = getStartState();
        CartPoleRunner cpr=new CartPoleRunner(mcForSearch,memory,NOF_STEPS);
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
            CartPoleStateValueMemory<CartPoleVariables> memory)
    {
        EnvironmentGenericInterface<CartPoleVariables, Integer> environment = EnvironmentCartPole.newDefault();

        ActionInterface<Integer> actionTemplate=  ActionCartPole.newRandom();
        MonteCarloSettings<CartPoleVariables, Integer> settings= MonteCarloSettings.<CartPoleVariables, Integer>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(CartPolePolicies.newEqualProbability())
                .simulationPolicy(CartPolePolicies.newEqualProbability())
                .isDefensiveBackup(false)
                .maxTreeDepth(50)
                .alphaBackupNormal(0.9)
                .alphaBackupDefensive(0.1)
                .timeBudgetMilliSeconds(TIME_BUDGET_MILLI_SECONDS)
                .weightReturnsSteps(0)
                .weightMemoryValue(1)
                .weightReturnsSimulation(0)
                .nofSimulationsPerNode(0)
                .coefficientExploitationExploration(1)
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
        EnvironmentGenericInterface<CartPoleVariables, Integer> environment = EnvironmentCartPole.newDefault();
        final int NOF_SIMULATIONS_PER_NODE = 100;
        final int TIME_BUDGET_MILLI_SECONDS = 100;
        ActionInterface<Integer> actionTemplate=  ActionCartPole.newRandom();
        MonteCarloSettings<CartPoleVariables, Integer> settings= MonteCarloSettings.<CartPoleVariables, Integer>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(CartPolePolicies.newEqualProbability())
                .simulationPolicy(CartPolePolicies.newEqualProbability())
                .isDefensiveBackup(false)
                .coefficientMaxAverageReturn(0.0) //0 <=> average, 1 <=> max
                .maxTreeDepth(100)
                .timeBudgetMilliSeconds(TIME_BUDGET_MILLI_SECONDS)
                .weightReturnsSteps(0)
                .nofSimulationsPerNode(NOF_SIMULATIONS_PER_NODE)
                .build();

        return MonteCarloTreeCreator.<CartPoleVariables, Integer>builder()
                .environment(environment)
                .startState(StateCartPole.newAllStatesAsZero())
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }
}
