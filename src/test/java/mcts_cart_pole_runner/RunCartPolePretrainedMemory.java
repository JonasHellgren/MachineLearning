package mcts_cart_pole_runner;

import lombok.SneakyThrows;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.cart_pole.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.network_training.CartPoleStateValueMemory;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import monte_carlo_tree_search.swing.CartPoleGraphics;

public class RunCartPolePretrainedMemory {
    private static final int BUFFER_SIZE = 1000;
    private static final int MINI_BATCH_SIZE = 30;
    private static final double  MAX_ERROR = 5e-5;
    private static final int MAX_NOF_EPOCHS = 50_000;
    private static final int NOF_STEPS = 1000;
    private static final int TIME_BUDGET_MILLI_SECONDS = 50;


    @SneakyThrows
    public static void main(String[] args) {
        MonteCarloTreeCreator<CartPoleVariables, Integer> mcForTraining= createTreeCreatorForTraining();
        MemoryTrainerHelper memoryTrainerHelper=new MemoryTrainerHelper(MINI_BATCH_SIZE,BUFFER_SIZE, MAX_ERROR, MAX_NOF_EPOCHS);
        ReplayBuffer<CartPoleVariables,Integer> buffer=memoryTrainerHelper.createExperienceBuffer(mcForTraining);
        CartPoleStateValueMemory<CartPoleVariables> memory=new CartPoleStateValueMemory<>();
        memoryTrainerHelper.trainMemory(memory, buffer);

        MonteCarloTreeCreator<CartPoleVariables, Integer> mcForSearch= createTreeCreatorForSearch(memory);

        CartPoleGraphics graphics=new CartPoleGraphics();
        EnvironmentGenericInterface<CartPoleVariables, Integer> environmentNotStepLimited =
                EnvironmentCartPole.builder().maxNofSteps(Integer.MAX_VALUE).build();
        StateInterface<CartPoleVariables> state= StateCartPole.newAllStatesAsZero();
        state.getVariables().theta=EnvironmentCartPole.THETA_THRESHOLD_RADIANS/2;
        state.getVariables().x=EnvironmentCartPole.X_TRESHOLD*3/4;

        int i=0;
        boolean isFail;
        do {
            state.getVariables().nofSteps=0;  //reset nof steps
            mcForSearch.setStartState(state);
            mcForSearch.run();
            ActionInterface<Integer> actionCartPole=mcForSearch.getFirstAction();
            StepReturnGeneric<CartPoleVariables> sr=environmentNotStepLimited.step(actionCartPole,state);
            state.setFromReturn(sr);
            double value=memory.read(state);
            graphics.render(state,i, value,actionCartPole.getValue());
            isFail=sr.isFail;
            i++;
        } while (i < NOF_STEPS && !isFail);
        System.out.println("state.getVariables().nofSteps = " + state.getVariables().nofSteps);

    }

    public static MonteCarloTreeCreator<CartPoleVariables, Integer> createTreeCreatorForSearch(
            CartPoleStateValueMemory<CartPoleVariables> memory)
    {
        EnvironmentGenericInterface<CartPoleVariables, Integer> environment = EnvironmentCartPole.newDefault();
        final int VALUE_LEFT = 0;

        ActionInterface<Integer> actionTemplate=  ActionCartPole.builder().rawValue(VALUE_LEFT).build();
        MonteCarloSettings<CartPoleVariables, Integer> settings= MonteCarloSettings.<CartPoleVariables, Integer>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(CartPolePolicies.newEqualProbability())
                .simulationPolicy(CartPolePolicies.newEqualProbability())
                .isDefensiveBackup(false)
                .maxTreeDepth(50)
                .alphaBackupNormal(0.9)
                .alphaBackupDefensive(0.1)
                .maxTreeDepth(50)
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
        final int VALUE_LEFT = 0;
        final int NOF_SIMULATIONS_PER_NODE = 100;
        final double COEFFICIENT_EXPLOITATION_EXPLORATION = 0.1;
        final int TIME_BUDGET_MILLI_SECONDS = 100;
        ActionInterface<Integer> actionTemplate=  ActionCartPole.builder().rawValue(VALUE_LEFT).build();
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
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)
                .build();

        return MonteCarloTreeCreator.<CartPoleVariables, Integer>builder()
                .environment(environment)
                .startState(StateCartPole.newAllStatesAsZero())
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }
}
