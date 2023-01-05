package mcts_cart_pole_runner;

import common.Conditionals;
import common.RandUtils;
import common.ScalerLinear;
import lombok.extern.java.Log;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.cart_pole.*;
import monte_carlo_tree_search.domains.models_battery_cell.CellResultsPlotter;
import monte_carlo_tree_search.domains.models_battery_cell.EnvironmentCell;
import monte_carlo_tree_search.exceptions.StartStateIsTrapException;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.network_training.CartPoleStateValueMemory;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import monte_carlo_tree_search.swing.CartPoleGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * https://medium.com/@_michelangelo_/alphazero-for-dummies-5bcc713fc9c6
 */

@Log
public class RunCartPoleAlphaZero {

    private static final int BUFFER_SIZE_TRAINING = 10_000;
    private static final int BUFFER_SIZE_EPISODE = 1_000;
    private static final double VARIABLE_DECREASE = 0.1;

    private static final double MAX_ERROR = Double.MAX_VALUE;
    private static final int MAX_EPOCHS = 10;      //10
    private static final int MINI_BATCH_SIZE = 100;  //30
    private static final int NOF_EPISODES = 100;    //100
    private static final int NOT_RELEVANT = 0;
    private static final int MAX_NOF_STEPS_IN_EVALUATION = 10_000;

    private static final int TIME_BUDGET_MILLI_SECONDS_TRAINING = 1;
    private static final int TIME_BUDGET_MILLI_SECONDS_EVALUATION = 50;
    private static final double DISCOUNT_FACTOR = 1.0;
    private static final int BUFFER_SIZE_TRAINING_LIMIT = MINI_BATCH_SIZE;
    private static final double PROBABILITY_RANDOM_ACTION_START = 0.9;
    private static final double PROBABILITY_RANDOM_ACTION_END = 0.1;


    public static void main(String[] args) {
        CartPoleStateValueMemory<CartPoleVariables> memory = new CartPoleStateValueMemory<>();
        MonteCarloTreeCreator<CartPoleVariables, Integer> mcForSearch = createTreeCreatorForSearch(memory);
        ReplayBuffer<CartPoleVariables, Integer> bufferTrainig = new ReplayBuffer<>(BUFFER_SIZE_TRAINING);

        CartPoleGraphics graphics = new CartPoleGraphics("Training animation");
        EnvironmentGenericInterface<CartPoleVariables, Integer> environmentTraining =
                EnvironmentCartPole.builder().build();
        ReplayBuffer<CartPoleVariables, Integer> bufferEpisode = new ReplayBuffer<>(BUFFER_SIZE_EPISODE);

        ScalerLinear probScaler=new ScalerLinear(0,NOF_EPISODES,PROBABILITY_RANDOM_ACTION_START,PROBABILITY_RANDOM_ACTION_END);
        List<Double> learningErrors = new ArrayList<>();
        List<Double> returns = new ArrayList<>();


        for (int episode = 0; episode < NOF_EPISODES; episode++) {
            boolean isTerminal = false;
            StateInterface<CartPoleVariables> state = getStartState();
            bufferEpisode.clear();
            int step = 0;
            while (!isTerminal) {
                ActionInterface<Integer> actionCartPole = RandUtils.calcRandomFromInterval(0,1)< probScaler.calcOutDouble(episode)
                        ?ActionCartPole.newRandom()
                        :getActionFromSearch(mcForSearch, state);

                StepReturnGeneric<CartPoleVariables> sr = stepAndUpdateState(environmentTraining, state, actionCartPole);
                addExperience(bufferEpisode, state, sr);
                renderGraphics(memory, graphics, state, step, actionCartPole);
                isTerminal = sr.isTerminal;
                step++;
            }

            ReplayBufferValueSetter rbvs = trainMemoryFromEpisode(memory, bufferTrainig, bufferEpisode);

            System.out.println("episode = " + episode + ", steps = " + step+", buffersize = " + bufferTrainig.size());
            learningErrors.add(memory.getLearningRule().getTotalNetworkError());
            returns.add(rbvs.getEpisodeReturn());
        }

        doPlotting(learningErrors,returns);

        mcForSearch.getSettings().setTimeBudgetMilliSeconds(TIME_BUDGET_MILLI_SECONDS_EVALUATION);
        CartPoleRunner cpr = new CartPoleRunner(mcForSearch, memory, MAX_NOF_STEPS_IN_EVALUATION);
        StateInterface<CartPoleVariables> state = StateCartPole.newAllStatesAsZero();
        cpr.run(state);

    }

    private static void doPlotting(List<Double> learningErrors,List<Double> returns) {
        CartPoleTrainResultsPlotter plotter=new CartPoleTrainResultsPlotter("Plot");
        plotter.plot(learningErrors,returns);
    }

    private static void renderGraphics(CartPoleStateValueMemory<CartPoleVariables> memory, CartPoleGraphics graphics, StateInterface<CartPoleVariables> state, int step, ActionInterface<Integer> actionCartPole) {
        graphics.render(state, step, memory.read(state), actionCartPole.getValue());
    }

    private static void addExperience(ReplayBuffer<CartPoleVariables, Integer> bufferEpisode, StateInterface<CartPoleVariables> state, StepReturnGeneric<CartPoleVariables> sr) {
        bufferEpisode.addExperience(Experience.<CartPoleVariables, Integer>builder()
                .stateVariables(state.getVariables()).reward(sr.reward).build());
    }

    private static StepReturnGeneric<CartPoleVariables> stepAndUpdateState(EnvironmentGenericInterface<CartPoleVariables, Integer> environmentTraining,
                                                                           StateInterface<CartPoleVariables> state,
                                                                           ActionInterface<Integer> actionCartPole) {
        StepReturnGeneric<CartPoleVariables> sr = environmentTraining.step(actionCartPole, state);
        state.setFromReturn(sr);
        return sr;
    }

    @NotNull
    private static ReplayBufferValueSetter trainMemoryFromEpisode(CartPoleStateValueMemory<CartPoleVariables> memory,
                                                                  ReplayBuffer<CartPoleVariables, Integer> bufferTrainig,
                                                                  ReplayBuffer<CartPoleVariables, Integer> bufferEpisode) {
        ReplayBufferValueSetter rbvs = new ReplayBufferValueSetter(bufferEpisode, DISCOUNT_FACTOR);
        MemoryTrainerHelper memoryTrainerHelper = new MemoryTrainerHelper(MINI_BATCH_SIZE, NOT_RELEVANT, MAX_ERROR, MAX_EPOCHS);
        bufferTrainig.addAll(rbvs.createBuffer());

        Conditionals.executeIfTrue(bufferTrainig.size() > BUFFER_SIZE_TRAINING_LIMIT, () ->
                memoryTrainerHelper.trainMemory(memory, bufferTrainig));
        return rbvs;
    }

    private static ActionInterface<Integer> getActionFromSearch(MonteCarloTreeCreator<CartPoleVariables, Integer> mcForSearch,
                                                                StateInterface<CartPoleVariables> state) {
        StateInterface<CartPoleVariables> stateForSearch = state.copy();
        stateForSearch.getVariables().nofSteps = 0;  //reset nof steps
        mcForSearch.setStartState(stateForSearch);
        try {
            mcForSearch.run();
        } catch (StartStateIsTrapException ignored) {
            log.fine("Bad start state");
            return ActionCartPole.newRandom();
        }

        return mcForSearch.getFirstAction();
    }


    @NotNull
    private static StateInterface<CartPoleVariables> getStartState() {
        StateInterface<CartPoleVariables> state = StateCartPole.newRandom();
        state.getVariables().theta = state.getVariables().theta * VARIABLE_DECREASE;
        state.getVariables().x = state.getVariables().x * VARIABLE_DECREASE;
        state.getVariables().thetaDot = state.getVariables().thetaDot * VARIABLE_DECREASE;
        state.getVariables().xDot = state.getVariables().xDot * VARIABLE_DECREASE;
        return state;
    }

    public static MonteCarloTreeCreator<CartPoleVariables, Integer> createTreeCreatorForSearch(
            CartPoleStateValueMemory<CartPoleVariables> memory) {
        EnvironmentGenericInterface<CartPoleVariables, Integer> environment = EnvironmentCartPole.newDefault();

        ActionInterface<Integer> actionTemplate = ActionCartPole.newRandom();
        MonteCarloSettings<CartPoleVariables, Integer> settings = MonteCarloSettings.<CartPoleVariables, Integer>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(CartPolePolicies.newEqualProbability())
                .simulationPolicy(CartPolePolicies.newEqualProbability())
                .isDefensiveBackup(false)
                .maxTreeDepth(50)
                .alphaBackupNormal(0.9)
                .alphaBackupDefensive(0.1)
                .timeBudgetMilliSeconds(TIME_BUDGET_MILLI_SECONDS_TRAINING)
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
