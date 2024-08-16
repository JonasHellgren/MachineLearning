package mcts_runners.energy_trading;

import common.math.ScalerLinear;
import common.other.Conditionals;
import common.other.RandUtilsML;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import monte_carlo_tree_search.models_and_support_classes.StepReturnGeneric;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.energy_trading.*;
import monte_carlo_tree_search.exceptions.StartStateIsTrapException;
import monte_carlo_tree_search.interfaces.*;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import monte_carlo_tree_search.network_training.ReplayBufferValueSetter;
import common.plotters.PlotterMultiplePanelsTrajectory;

import java.util.*;

/***
 *   The parameter weightMemoryValue is critical
 *   Optimal/correct result is sumRewardsNSteps = 3.0, mostly found for some weightMemoryValue setting
 */

@Log
public class EnergyTradingAlphaZeroRunner {

    @Getter
    public static class  SumRewardsTracker{
        List<Double> sumOfRewardsList;
        double sumOfRewards;

        public SumRewardsTracker() {
            sumOfRewardsList=new ArrayList<>();
            reset();
        }

        void reset() {
            sumOfRewards=0;
        }

        void increaseSumOfRewards(double reward) {
            sumOfRewards=sumOfRewards+reward;
        }

        public void addSumOfRewardsToListAndResetSumOfRewards() {
            sumOfRewardsList.add(sumOfRewards);
            reset();
        }

    }

    private static final int NOF_EPISODES = 300;
    private static final int NOF_EPISODES_BETWEEN_LOGGING = 100;
    private static final int MINI_BATCH_SIZE = 20;
    private static final double MAX_NETWORK_ERROR = 1e-8;  //1e-5;
    private static final int MAX_NOF_EPOCHS = 5;  //how much network is learning from each mini-batch - high <=> learn more
    private static final int MAX_BUFFER_SIZE_TRAINING = 5_000;
    private static final int MAX_BUFFER_SIZE_EPISODE = 1_000;
    private static final double PROBABILITY_RANDOM_ACTION_START = 0.1;
    private static final double PROBABILITY_RANDOM_ACTION_END = 0.0;
    private static final double DISCOUNT_FACTOR = 1.0;
    private static final int BUFFER_SIZE_TRAINING_LIMIT = MINI_BATCH_SIZE;
    private static final boolean IS_FIRST_VISIT = true;
    private static final double FRACTION_OF_EPISODE_BUFFER_TO_INCLUDE = 1.0;
    private static final int NOF_EPISODES_RESETTING_MEMORY = 100_000;
    private static final List<Double> WEIGHTS_MEM = Arrays.asList(0.0, 0.25, 0.5, 0.75, 1.0, 1.25, 1.5);
    private static final double COEFFICIENT_EXPLOITATION_EXPLORATION = 1e0;
    private static final int START_TIME = 0;
    private static final double START_SOE = 0.5;
    static ActionInterface<Integer> actionTemplate;
    static EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment;
    static NetworkMemoryInterface<VariablesEnergyTrading, Integer> memory;
    static MonteCarloTreeCreator<VariablesEnergyTrading, Integer> searcherTraining;
    static ReplayBuffer<VariablesEnergyTrading, Integer> bufferTraining;
    static ReplayBuffer<VariablesEnergyTrading, Integer> bufferEpisode;
    static ScalerLinear probScaler;

    @SneakyThrows
    public static void main(String[] args) {
        setupFields();
        resetMemory(memory);
        printMemory();
        SumRewardsTracker tracker=new SumRewardsTracker();

        for (int episode = 0; episode < NOF_EPISODES; episode++) {
            StateInterface<VariablesEnergyTrading> startState = StateEnergyTrading.newRandom();
            bufferEpisode.clear();
            StepReturnGeneric<VariablesEnergyTrading> sr;
             do {
                ActionInterface<Integer> action = isRandomAction(probScaler, episode)
                        ? ActionEnergyTrading.newRandom()
                        : getActionFromSearch(searcherTraining, startState);
                StateInterface<VariablesEnergyTrading> stateBefore = startState.copy();
                sr = stepAndUpdateState(environment, startState, action);
                addExperience(bufferEpisode, stateBefore, action, sr);
                tracker.increaseSumOfRewards(sr.reward);
            } while (!sr.isTerminal);
            addExperienceOfTerminal(sr);
            trainMemory(memory, bufferTraining, bufferEpisode);
            someLogging(bufferTraining, episode);
            tracker.addSumOfRewardsToListAndResetSumOfRewards();
        }

        PlotterMultiplePanelsTrajectory plotter=new PlotterMultiplePanelsTrajectory(Collections.singletonList("return"),"Iteration");
        plotter.plot(Collections.singletonList(tracker.getSumOfRewardsList()));
        printMemory();
        runWithMultipleMemoryWeights();
    }

    private static void setupFields() {
        actionTemplate = ActionEnergyTrading.newValue(0);
        environment = EnvironmentEnergyTrading.newDefault();
        memory = new EnergyTraderValueMemoryNetwork<>();
        searcherTraining = createSearcherTraining(memory);
        bufferTraining = new ReplayBuffer<>(MAX_BUFFER_SIZE_TRAINING);
        bufferEpisode = new ReplayBuffer<>(MAX_BUFFER_SIZE_EPISODE);
        probScaler = new ScalerLinear(0, NOF_EPISODES, PROBABILITY_RANDOM_ACTION_START, PROBABILITY_RANDOM_ACTION_END);
    }

    private static void runWithMultipleMemoryWeights() {
        StateInterface<VariablesEnergyTrading> stateStart = StateEnergyTrading.newFromTimeAndSoE(0, 0.5);
        for (Double weightMem : WEIGHTS_MEM) {
            EnergyTradingRunner runner = new EnergyTradingRunner(createSearcherRunner(stateStart, memory, weightMem), environment);
            runner.run(StateEnergyTrading.newFromTimeAndSoE(START_TIME, START_SOE));
            System.out.println("weightMem = " + weightMem + System.lineSeparator() + ", runner = " + runner);
        }
    }

    private static void printMemory() {
        Map<Double, Double> soEValueMap = new HashMap<>();
        for (int time = START_TIME; time < EnvironmentEnergyTrading.AFTER_MAX_TIME; time++) {
            soEValueMap.clear();
            for (double SoE : Arrays.asList(0.8, 0.5, 0.3)) {
                soEValueMap.put(SoE, memory.read(StateEnergyTrading.newFromTimeAndSoE(time, SoE)));
            }
            System.out.println("time = " + time + ", soEValueMap= " + soEValueMap);
        }
    }

    private static void addExperienceOfTerminal(StepReturnGeneric<VariablesEnergyTrading> sr) {
        if (sr != null) {
            sr.reward = 0;
            addExperience(bufferEpisode, sr.newState, ActionEnergyTrading.newValue(0), sr);
        }
    }

    private static void someLogging(ReplayBuffer<VariablesEnergyTrading, Integer> bufferTrainig, int episode) {
        Conditionals.executeIfTrue(episode % NOF_EPISODES_BETWEEN_LOGGING == 0, () ->
                log.info("episode = " + episode + ", buffersize = " + bufferTrainig.size()));
    }

    private static boolean isRandomAction(ScalerLinear probScaler, int episode) {
        return RandUtilsML.getRandomDouble(0, 1) < probScaler.calcOutDouble(episode);
    }

    private static ActionInterface<Integer> getActionFromSearch(
            MonteCarloTreeCreator<VariablesEnergyTrading, Integer> mcForSearch,
            StateInterface<VariablesEnergyTrading> state) {
        StateInterface<VariablesEnergyTrading> stateForSearch = state.copy();
        mcForSearch.setStartState(stateForSearch);
        try {
            mcForSearch.run();
        } catch (StartStateIsTrapException e) {
            return ActionEnergyTrading.newRandom();
        }
        return mcForSearch.getFirstAction();
    }

    private static StepReturnGeneric<VariablesEnergyTrading> stepAndUpdateState(
            EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environmentTraining,
            StateInterface<VariablesEnergyTrading> state,
            ActionInterface<Integer> action) {
        StepReturnGeneric<VariablesEnergyTrading> sr = environmentTraining.step(action, state);
        state.setFromReturn(sr);
        return sr;
    }

    private static void addExperience(ReplayBuffer<VariablesEnergyTrading, Integer> bufferEpisode,
                                      StateInterface<VariablesEnergyTrading> state,
                                      ActionInterface<Integer> action,
                                      StepReturnGeneric<VariablesEnergyTrading> sr) {
        bufferEpisode.addExperience(Experience.<VariablesEnergyTrading, Integer>builder()
                .stateVariables(state.getVariables()).action(action.getValue()).reward(sr.reward).build());
    }

    private static void resetMemory(
            NetworkMemoryInterface<VariablesEnergyTrading, Integer> memory) {
        final int ZERO_VALUE = 0;
        final int maxNofEpochs = 10_000;
        ReplayBuffer<VariablesEnergyTrading, Integer> buffer = new ReplayBuffer<>(MAX_BUFFER_SIZE_TRAINING);
        for (int i = 0; i < NOF_EPISODES_RESETTING_MEMORY; i++) {
            StateInterface<VariablesEnergyTrading> state = StateEnergyTrading.newRandom();
            buffer.addExperience(Experience.<VariablesEnergyTrading, Integer>builder()
                    .stateVariables(state.getVariables()).value(ZERO_VALUE).build());
        }
        MemoryTrainerInterface<VariablesEnergyTrading, Integer> trainer =
                new EnergyTraderMemoryTrainer(MINI_BATCH_SIZE, MAX_NETWORK_ERROR, maxNofEpochs);
        Conditionals.executeIfTrue(buffer.size() > BUFFER_SIZE_TRAINING_LIMIT, () ->
                trainer.trainMemory(memory, buffer));
    }

    private static void trainMemory(
            NetworkMemoryInterface<VariablesEnergyTrading, Integer> memory,
            ReplayBuffer<VariablesEnergyTrading, Integer> bufferTraining,
            ReplayBuffer<VariablesEnergyTrading, Integer> bufferEpisode) {
        ReplayBufferValueSetter<VariablesEnergyTrading, Integer> rbvs =
                new ReplayBufferValueSetter<>(bufferEpisode, DISCOUNT_FACTOR, IS_FIRST_VISIT);
        bufferTraining.addAll(rbvs.createBufferFromReturns(FRACTION_OF_EPISODE_BUFFER_TO_INCLUDE));  //candidate = createBufferFromAllReturns
        MemoryTrainerInterface<VariablesEnergyTrading, Integer> trainer =
                new EnergyTraderMemoryTrainer(MINI_BATCH_SIZE, MAX_NETWORK_ERROR, MAX_NOF_EPOCHS);

        Conditionals.executeIfTrue(bufferTraining.size() > BUFFER_SIZE_TRAINING_LIMIT, () ->
                trainer.trainMemory(memory, bufferTraining));
    }


    public static MonteCarloTreeCreator<VariablesEnergyTrading, Integer> createSearcherTraining(
            NetworkMemoryInterface<VariablesEnergyTrading, Integer> memory) {
        EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment = EnvironmentEnergyTrading.newDefault();
        return MonteCarloTreeCreator.<VariablesEnergyTrading, Integer>builder()
                .environment(environment)
                .startState(StateEnergyTrading.newRandom())
                .memory(memory)
                .monteCarloSettings(createSearchTrainerSettings())
                .actionTemplate(actionTemplate)
                .build();
    }

    private static MonteCarloSettings<VariablesEnergyTrading, Integer> createSearchTrainerSettings() {
        return MonteCarloSettings.<VariablesEnergyTrading, Integer>builder()
                .actionSelectionPolicy(PoliciesEnergyTrading.newRandom())
                .simulationPolicy(PoliciesEnergyTrading.newRandom())
                .isDefensiveBackup(true).alphaBackupDefensiveStep(0.1).discountFactorBackupSimulationDefensive(0.1)
                .coefficientMaxAverageReturn(0.0) //average
                .maxTreeDepth(2)
                .minNofIterations(100).maxNofIterations(1_000).timeBudgetMilliSeconds(5)
                .weightReturnsSteps(1.0).weightReturnsSimulation(0.0).weightMemoryValue(1.0)
                .nofSimulationsPerNode(0)
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)
                .isCreatePlotData(false).isDoLogging(false)
                .build();
    }

    public static MonteCarloTreeCreator<VariablesEnergyTrading, Integer> createSearcherRunner(
            StateInterface<VariablesEnergyTrading> state,
            NetworkMemoryInterface<VariablesEnergyTrading, Integer> memory,
            double weightMem) {
        EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment = EnvironmentEnergyTrading.newDefault();
        return MonteCarloTreeCreator.<VariablesEnergyTrading, Integer>builder()
                .environment(environment)
                .startState(state)
                .memory(memory)
                .monteCarloSettings(createSearchRunnerSettings(weightMem))
                .actionTemplate(actionTemplate)
                .build();
    }

    private static MonteCarloSettings<VariablesEnergyTrading, Integer> createSearchRunnerSettings(double weightMem) {
        return MonteCarloSettings.<VariablesEnergyTrading, Integer>builder()
                .actionSelectionPolicy(PoliciesEnergyTrading.newRandom())
                .simulationPolicy(PoliciesEnergyTrading.newRandom())
                .isDefensiveBackup(true).alphaBackupDefensiveStep(0.1).discountFactorBackupSimulationDefensive(0.1)
                .coefficientMaxAverageReturn(0.0) //average
                .maxTreeDepth(5)
                .minNofIterations(100).maxNofIterations(10_000).timeBudgetMilliSeconds(100)
                .weightReturnsSteps(1.0).weightReturnsSimulation(0.0).weightMemoryValue(weightMem)
                .nofSimulationsPerNode(0)
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)
                .isCreatePlotData(false).isDoLogging(false)
                .build();
    }


}
