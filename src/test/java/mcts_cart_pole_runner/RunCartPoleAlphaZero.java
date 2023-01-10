package mcts_cart_pole_runner;

import common.Conditionals;
import common.RandUtils;
import common.ScalerLinear;
import lombok.extern.java.Log;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.cart_pole.*;
import monte_carlo_tree_search.exceptions.StartStateIsTrapException;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.network_training.*;
import monte_carlo_tree_search.swing.CartPoleGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * https://medium.com/@_michelangelo_/alphazero-for-dummies-5bcc713fc9c6
 * for ex MINI_BATCH_SIZE is from reference above
 *
 * Fast training but less precise values: NOF_EPISODES=1000, PROBABILITY_RANDOM_ACTION_XXX=0.99, FRACTION_OF_EPISODE_BUFFER_TO_INCLUDE=0.01
*  Slow training but more precise values: NOF_EPISODES=200, PROBABILITY_RANDOM_ACTION={0.1,0.01}, FRACTION_OF_EPISODE_BUFFER_TO_INCLUDE=0.5
*
 * The faster will give similar process as RunCartPolePretrainedMemory, random rollout determines value.
 */

@Log
public class RunCartPoleAlphaZero {

    private static final int BUFFER_SIZE_TRAINING = 5_000;
    private static final int BUFFER_SIZE_EPISODE = 1_000;
    private static final double INIT_STATE_VARIABLE_DEVIATION = 0.99;  //small <=> close to zero, close to one <=> random
    private static final int NOF_EPISODES = 200;
    private static final int NOT_RELEVANT = 0;
    private static final int MAX_NOF_STEPS_IN_EVALUATION = Integer.MAX_VALUE;

    private static final double MAX_ERROR = Double.MAX_VALUE;
    private static final int MAX_EPOCHS = 10;
    private static final int MINI_BATCH_SIZE = 128;

    private static final int TIME_BUDGET_MILLI_SECONDS_TRAINING = 1;  //small => faster training
    private static final int TIME_BUDGET_MILLI_SECONDS_EVALUATION = 50;
    private static final int MAX_TREE_DEPTH_SEARCH = 5;  //small => faster training
    private static final int MAX_TREE_DEPTH_EVALUATION = 50;
    private static final double DISCOUNT_FACTOR = 1.0;
    private static final int BUFFER_SIZE_TRAINING_LIMIT = MINI_BATCH_SIZE;
    private static final double PROBABILITY_RANDOM_ACTION_START = 0.1;
    private static final double PROBABILITY_RANDOM_ACTION_END = 0.1;
    private static final boolean IS_FIRST_VISIT = true;
    private static final double FRACTION_OF_EPISODE_BUFFER_TO_INCLUDE = 0.5;
    private static final String FILE = "networks/cartPoleStateValue.nnet";

    public static void main(String[] args) {
        NetworkMemoryInterface<CartPoleVariables> memory = new CartPoleStateValueMemory<>();  //todo interface
        MonteCarloTreeCreator<CartPoleVariables, Integer> mcForSearch = createTreeCreatorForSearch(memory);
        ReplayBuffer<CartPoleVariables, Integer> bufferTraining = new ReplayBuffer<>(BUFFER_SIZE_TRAINING);

        CartPoleGraphics graphics = new CartPoleGraphics("Training animation");
        EnvironmentGenericInterface<CartPoleVariables, Integer> environmentTraining =
                EnvironmentCartPole.newDefault();
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
                ActionInterface<Integer> actionCartPole = isRandomAction(probScaler, episode)
                        ?ActionCartPole.newRandom()
                        :getActionFromSearch(mcForSearch, state);

                StepReturnGeneric<CartPoleVariables> sr = stepAndUpdateState(environmentTraining, state, actionCartPole);
                addExperience(bufferEpisode, state, sr);
                renderGraphics(memory, graphics, state, step++, actionCartPole);
                isTerminal = sr.isTerminal;
            }

            ReplayBufferValueSetter rbvs = trainMemoryFromEpisode(memory, bufferTraining, bufferEpisode);

            someLogging(bufferTraining, episode, step);
            someTracking(memory, learningErrors, returns, rbvs, bufferTraining);
        }

        doPlotting(learningErrors,returns);
        memory.save(FILE);

        mcForSearch.getSettings().setTimeBudgetMilliSeconds(TIME_BUDGET_MILLI_SECONDS_EVALUATION);
        mcForSearch.getSettings().setMaxTreeDepth(MAX_TREE_DEPTH_EVALUATION);
        CartPoleRunner cpr = new CartPoleRunner(mcForSearch, memory, MAX_NOF_STEPS_IN_EVALUATION);
        StateInterface<CartPoleVariables> state = StateCartPole.newAllStatesAsZero();
        cpr.run(state);

    }

    private static void someLogging(ReplayBuffer<CartPoleVariables, Integer> bufferTrainig, int episode, int step) {
        log.info("episode = " + episode + ", steps = " + step +", buffersize = " + bufferTrainig.size());
    }

    private static void someTracking(NetworkMemoryInterface<CartPoleVariables> memory,
                                     List<Double> learningErrors,
                                     List<Double> returns,
                                     ReplayBufferValueSetter rbvs,
                                     ReplayBuffer<CartPoleVariables, Integer> bufferTraining) {
        //learningErrors.add(memory.getLearningRule().getTotalNetworkError());
        List<Experience<CartPoleVariables, Integer>> miniBatch=bufferTraining.getMiniBatch(MINI_BATCH_SIZE);
        learningErrors.add(memory.getAverageValueError(miniBatch));
        returns.add(rbvs.getEpisodeReturn());
    }

    private static boolean isRandomAction(ScalerLinear probScaler, int episode) {
        return RandUtils.calcRandomFromInterval(0, 1) < probScaler.calcOutDouble(episode);
    }

    private static void doPlotting(List<Double> learningErrors,List<Double> returns) {
        TwoPanelsPlotter plotter=new TwoPanelsPlotter("Error","Return","Step");
        plotter.plot(learningErrors,returns);
    }

    private static void renderGraphics(NetworkMemoryInterface<CartPoleVariables> memory,
                                       CartPoleGraphics graphics,
                                       StateInterface<CartPoleVariables> state,
                                       int step,
                                       ActionInterface<Integer> actionCartPole) {
        double rootNodeValue=0;
        graphics.render(state, step, memory.read(state), rootNodeValue, actionCartPole.getValue());
    }

    private static void addExperience(ReplayBuffer<CartPoleVariables, Integer> bufferEpisode,
                                      StateInterface<CartPoleVariables> state,
                                      StepReturnGeneric<CartPoleVariables> sr) {
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
    private static ReplayBufferValueSetter trainMemoryFromEpisode(NetworkMemoryInterface<CartPoleVariables> memory,
                                                                  ReplayBuffer<CartPoleVariables, Integer> bufferTraining,
                                                                  ReplayBuffer<CartPoleVariables, Integer> bufferEpisode) {
        ReplayBufferValueSetter rbvs = new ReplayBufferValueSetter(bufferEpisode, DISCOUNT_FACTOR, IS_FIRST_VISIT);
        MemoryTrainerHelper memoryTrainerHelper = new MemoryTrainerHelper(MINI_BATCH_SIZE, NOT_RELEVANT, MAX_ERROR, MAX_EPOCHS);
        bufferTraining.addAll(rbvs.createBufferFromStartReturn(FRACTION_OF_EPISODE_BUFFER_TO_INCLUDE));  //candidate = createBufferFromAllReturns
       // bufferTrainig.addAll(rbvs.createBufferFromReturns(FRACTION_OF_EPISODE_BUFFER_TO_INCLUDE));

        Conditionals.executeIfTrue(bufferTraining.size() > BUFFER_SIZE_TRAINING_LIMIT, () ->
                memoryTrainerHelper.trainMemory(memory, bufferTraining));
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
        state.getVariables().theta = state.getVariables().theta * INIT_STATE_VARIABLE_DEVIATION;
        state.getVariables().x = state.getVariables().x * INIT_STATE_VARIABLE_DEVIATION;
        state.getVariables().thetaDot = state.getVariables().thetaDot * INIT_STATE_VARIABLE_DEVIATION;
        state.getVariables().xDot = state.getVariables().xDot * INIT_STATE_VARIABLE_DEVIATION;
        return state;
    }

    public static MonteCarloTreeCreator<CartPoleVariables, Integer> createTreeCreatorForSearch(
            NetworkMemoryInterface<CartPoleVariables> memory) {
        EnvironmentGenericInterface<CartPoleVariables, Integer> environment = EnvironmentCartPole.newDefault();

        ActionInterface<Integer> actionTemplate = ActionCartPole.newRandom();
        MonteCarloSettings<CartPoleVariables, Integer> settings = MonteCarloSettings.<CartPoleVariables, Integer>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(CartPolePolicies.newEqualProbability())
                .simulationPolicy(CartPolePolicies.newEqualProbability())
                .isDefensiveBackup(false)
                .maxTreeDepth(MAX_TREE_DEPTH_SEARCH)
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
