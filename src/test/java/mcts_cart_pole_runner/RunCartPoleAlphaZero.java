package mcts_cart_pole_runner;

import common.Conditionals;
import lombok.extern.java.Log;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.cart_pole.*;
import monte_carlo_tree_search.exceptions.StartStateIsTrapException;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.network_training.CartPoleStateValueMemory;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import monte_carlo_tree_search.swing.CartPoleGraphics;
import org.jetbrains.annotations.NotNull;

/**
 * https://medium.com/@_michelangelo_/alphazero-for-dummies-5bcc713fc9c6
 */

@Log
public class RunCartPoleAlphaZero {

    private static final int BUFFER_SIZE_TRAINING = 10_000;
    private static final int BUFFER_SIZE_EPISODE = 1_000;
    private static final double VARIABLE_DECREASE=0.9;

    private static final double  MAX_ERROR = Double.MAX_VALUE;
    private static final int MAX_EPOCHS = 10;
    private static final int MINI_BATCH_SIZE = 30;
    private static final int MAX_NOF_EPOCHS = 50_000;
    private static final int NOF_EPISODES = 100;
    private static final int TIME_BUDGET_MILLI_SECONDS = 10;
    private static final int TBD_RETURN = 0;
    private static final int NOT_RELEVANT = 0;

    public static void main(String[] args) {
        CartPoleStateValueMemory<CartPoleVariables> memory=new CartPoleStateValueMemory<>();
        MonteCarloTreeCreator<CartPoleVariables, Integer> mcForSearch= createTreeCreatorForSearch(memory);
        ReplayBuffer<CartPoleVariables,Integer>  bufferTrainig=new ReplayBuffer<>(BUFFER_SIZE_TRAINING);

        CartPoleGraphics graphics=new CartPoleGraphics();
        EnvironmentGenericInterface<CartPoleVariables, Integer> environmentNotStepLimited =
                EnvironmentCartPole.builder().maxNofSteps(Integer.MAX_VALUE).build();
        ReplayBuffer<CartPoleVariables,Integer>  bufferEpisode=new ReplayBuffer<>(BUFFER_SIZE_EPISODE);
        MemoryTrainerHelper memoryTrainerHelper=new MemoryTrainerHelper(MINI_BATCH_SIZE, NOT_RELEVANT, MAX_ERROR, MAX_EPOCHS);

        mcForSearch.getSettings().setTimeBudgetMilliSeconds(10);
        for (int episode = 0; episode < NOF_EPISODES ; episode++) {
            boolean isFail=false;
            StateInterface<CartPoleVariables> state = getStartState();
            bufferEpisode.clear();
            double episodeReturn=0;
            int step=0;
            while (!isFail && step<EnvironmentCartPole.MAX_NOF_STEPS) {
                state.getVariables().nofSteps=0;  //reset nof steps
                mcForSearch.setStartState(state);
                try {
                    mcForSearch.run();
                } catch (StartStateIsTrapException e) {
                    log.warning("StartStateIsTrapException");
                    System.out.println("StartStateIsTrapException step = " + step+", episodeReturn = " + episodeReturn);
                    break;
                }
                ActionInterface<Integer> actionCartPole=mcForSearch.getFirstAction();
                StepReturnGeneric<CartPoleVariables> sr=environmentNotStepLimited.step(actionCartPole,state);
                state.setFromReturn(sr);

                bufferEpisode.addExperience(Experience.<CartPoleVariables, Integer>builder()
                        .stateVariables(state.getVariables())
                        .value(TBD_RETURN)
                        .build());

                double value=memory.read(state);
                graphics.render(state,step, value,actionCartPole.getValue());
                isFail=sr.isFail;
                step++;
                episodeReturn=episodeReturn+sr.reward;
            }

            System.out.println("step = " + step+", episodeReturn = " + episodeReturn);

            bufferEpisode.setAllValues(episodeReturn);
            bufferTrainig.addAll(bufferEpisode);

            Conditionals.executeIfTrue(bufferTrainig.size()>MINI_BATCH_SIZE, () ->
            memoryTrainerHelper.trainMemory(memory,bufferTrainig));

            System.out.println("bufferTrainig.size() = " + bufferTrainig.size());
            System.out.println("memory.getLearningRule().getTotalNetworkError() = " + memory.getLearningRule().getTotalNetworkError());

        }


    }


    @NotNull
    private static StateInterface<CartPoleVariables> getStartState() {
        StateInterface<CartPoleVariables> state= StateCartPole.newRandom();
        state.getVariables().theta=state.getVariables().theta*VARIABLE_DECREASE;
        state.getVariables().x=state.getVariables().x*VARIABLE_DECREASE;
        state.getVariables().thetaDot=state.getVariables().thetaDot*VARIABLE_DECREASE;
        state.getVariables().xDot=state.getVariables().xDot*VARIABLE_DECREASE;
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


}
