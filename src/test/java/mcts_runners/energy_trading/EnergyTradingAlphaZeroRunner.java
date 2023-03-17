package mcts_runners.energy_trading;

import common.Conditionals;
import common.ListUtils;
import common.RandUtils;
import common.ScalerLinear;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloSimulator;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.energy_trading.*;
import monte_carlo_tree_search.exceptions.StartStateIsTrapException;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.interfaces.*;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import monte_carlo_tree_search.network_training.ReplayBufferValueSetter;

import java.util.List;

@Log
public class EnergyTradingAlphaZeroRunner {

    private static final int NOF_SIMULATIONS = 100;
    private static final int MAX_SIMULATION_DEPTH = 10;
    private static final int NOF_EPISODES = 200;
    private static final int BUFFER_SIZE = 1_000;
    private static final int MINI_BATCH_SIZE = 20;
    private static final double MAX_ERROR = 1e-8;  //1e-5;
    private static final int MAX_NOF_EPOCHS = 100;
    private static final int BUFFER_SIZE_TRAINING = 5_000;
    private static final int BUFFER_SIZE_EPISODE = 1_000;
    private static final double PROBABILITY_RANDOM_ACTION_START = 0.1;
    private static final double PROBABILITY_RANDOM_ACTION_END = 0.1;
    private static final int TIME_START = 0;
    private static final double SOE_START = 0.5;
    private static final double DISCOUNT_FACTOR = 1.0;
    private static final int BUFFER_SIZE_TRAINING_LIMIT = MINI_BATCH_SIZE;
    private static final boolean IS_FIRST_VISIT = true;
    private static final double FRACTION_OF_EPISODE_BUFFER_TO_INCLUDE = 1.0;
    private static final int NOT_RELEVANT = 0;
    private static final int ZERO_VALUE = 0;
    private static final int NOF_EPISODES_RESETTING_MEMORY = 100_000;

    static EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment;
    static MonteCarloSimulator<VariablesEnergyTrading,Integer> simulator;
    static MemoryTrainerInterface<VariablesEnergyTrading, Integer> trainer;
    static NetworkMemoryInterface<VariablesEnergyTrading, Integer> memory;
    static MonteCarloTreeCreator<VariablesEnergyTrading, Integer> monteCarloTreeCreator;
    static ReplayBuffer<VariablesEnergyTrading, Integer> bufferTraining;
    static ReplayBuffer<VariablesEnergyTrading, Integer> bufferEpisode;
    static ScalerLinear probScaler;
    static EnergyTradingRunner runner;

    @SneakyThrows
    public static void main(String[] args) {
        StateInterface<VariablesEnergyTrading> stateStart= StateEnergyTrading.newFromTimeAndSoE(0,0.5);
        environment = EnvironmentEnergyTrading.newDefault();
        simulator= new MonteCarloSimulator<>(environment,createSimulatorSettings());
        memory=new EnergyTraderValueMemory<>();
        trainer=new EnergyTraderMemoryTrainer(MINI_BATCH_SIZE, BUFFER_SIZE, MAX_ERROR, MAX_NOF_EPOCHS);
        monteCarloTreeCreator=createTreeCreator(stateStart,memory);
        bufferTraining = new ReplayBuffer<>(BUFFER_SIZE_TRAINING);
        bufferEpisode = new ReplayBuffer<>(BUFFER_SIZE_EPISODE);
        probScaler=new ScalerLinear(0,NOF_EPISODES,PROBABILITY_RANDOM_ACTION_START,PROBABILITY_RANDOM_ACTION_END);
        runner = new EnergyTradingRunner(createTreeCreator(stateStart,memory),environment);

        resetMemory(memory);
        printMemory();


        for (int episode = 0; episode < NOF_EPISODES; episode++) {
            boolean isTerminal = false;
          //  StateInterface<VariablesEnergyTrading> state = StateEnergyTrading.newFromTimeAndSoE(7,0.7);
            //   state.getVariables().SoE=RandUtils.getRandomDouble(EnvironmentEnergyTrading.SOE_MIN,EnvironmentEnergyTrading.SOE_MAX);

            StateInterface<VariablesEnergyTrading> state =StateEnergyTrading.newRandom();
            bufferEpisode.clear();
            int step = 0;
            StepReturnGeneric<VariablesEnergyTrading> sr=null;
            while (!isTerminal) {

                ActionInterface<Integer> action = isRandomAction(probScaler, episode)
                        ? ActionEnergyTrading.newRandom()
                        : getActionFromSearch(monteCarloTreeCreator, state);

                StateInterface<VariablesEnergyTrading> stateBefore=state.copy();
                sr = stepAndUpdateState(environment, state, action);
                addExperience(bufferEpisode, stateBefore, action, sr);
                isTerminal = sr.isTerminal;
            }
            addExperienceOfTerminal(sr);

            trainMemory(memory, bufferTraining, bufferEpisode);

          //  System.out.println("bufferEpisode = " + bufferEpisode);
         //   System.out.println("bufferTraining = " + bufferTraining);
            someLogging(bufferTraining, episode, step);

            // someTracking(memory, learningErrors, returns, rbvs, bufferTraining);
        }

        System.out.println("bufferTraining = " + bufferTraining);

        printMemory();

        runner.run(StateEnergyTrading.newFromTimeAndSoE(6,0.8));
        System.out.println("runner.toString() = " + runner.toString());

    }

    private static void printMemory() {
        System.out.println("memory.read(StateEnergyTrading.newFromTimeAndSoE(6,0.8)) = " + memory.read(StateEnergyTrading.newFromTimeAndSoE(6, 0.8)));
        System.out.println("memory.read(StateEnergyTrading.newFromTimeAndSoE(6,0.5)) = " + memory.read(StateEnergyTrading.newFromTimeAndSoE(6, 0.5)));
        System.out.println("memory.read(StateEnergyTrading.newFromTimeAndSoE(6,0.3)) = " + memory.read(StateEnergyTrading.newFromTimeAndSoE(6, 0.3)));
        System.out.println("memory.read(StateEnergyTrading.newFromTimeAndSoE(7,0.8)) = " + memory.read(StateEnergyTrading.newFromTimeAndSoE(7, 0.8)));
        System.out.println("memory.read(StateEnergyTrading.newFromTimeAndSoE(7,0.5)) = " + memory.read(StateEnergyTrading.newFromTimeAndSoE(7, 0.5)));
        System.out.println("memory.read(StateEnergyTrading.newFromTimeAndSoE(7,0.3)) = " + memory.read(StateEnergyTrading.newFromTimeAndSoE(7, 0.3)));
        System.out.println("memory.read(StateEnergyTrading.newFromTimeAndSoE(8,0.7)) = " + memory.read(StateEnergyTrading.newFromTimeAndSoE(8, 0.7)));
        System.out.println("memory.read(StateEnergyTrading.newFromTimeAndSoE(5,0.5)) = " + memory.read(StateEnergyTrading.newFromTimeAndSoE(5, 0.5)));
    }

    private static void addExperienceOfTerminal(StepReturnGeneric<VariablesEnergyTrading> sr) {
        sr.reward=0; //sr.isFail ? EnvironmentEnergyTrading.REWARD_FAIL:0;
        addExperience(bufferEpisode, sr.newState, ActionEnergyTrading.newValue(0), sr);
    }

    private static void someLogging(ReplayBuffer<VariablesEnergyTrading, Integer> bufferTrainig, int episode, int step) {
        log.info("episode = " + episode + ", steps = " + step +", buffersize = " + bufferTrainig.size());
    }

    private static boolean isRandomAction(ScalerLinear probScaler, int episode) {
        return RandUtils.getRandomDouble(0, 1) < probScaler.calcOutDouble(episode);
    }

    private static ActionInterface<Integer> getActionFromSearch(
            MonteCarloTreeCreator<VariablesEnergyTrading, Integer> mcForSearch,
            StateInterface<VariablesEnergyTrading> state) {
        StateInterface<VariablesEnergyTrading> stateForSearch = state.copy();
      //  stateForSearch.getVariables().nofSteps = 0;  //reset nof steps
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

    private static void  resetMemory(
            NetworkMemoryInterface<VariablesEnergyTrading, Integer> memory) {

        ReplayBuffer<VariablesEnergyTrading, Integer> buffer=new ReplayBuffer<>(BUFFER_SIZE_TRAINING);
        for (int i = 0; i < NOF_EPISODES_RESETTING_MEMORY; i++) {
            StateInterface<VariablesEnergyTrading> state = StateEnergyTrading.newRandom();
            buffer.addExperience(Experience.<VariablesEnergyTrading, Integer>builder()
                    .stateVariables(state.getVariables()).value(ZERO_VALUE).build());
        }
        MemoryTrainerInterface<VariablesEnergyTrading, Integer> trainer=
                new EnergyTraderMemoryTrainer(MINI_BATCH_SIZE, buffer.size(), 1e-8, 10_000);
        Conditionals.executeIfTrue(buffer.size() > BUFFER_SIZE_TRAINING_LIMIT, () ->
                trainer.trainMemory(memory, buffer));
    }

    private static ReplayBufferValueSetter<VariablesEnergyTrading, Integer> trainMemory(
            NetworkMemoryInterface<VariablesEnergyTrading,Integer> memory,
            ReplayBuffer<VariablesEnergyTrading, Integer> bufferTraining,
            ReplayBuffer<VariablesEnergyTrading, Integer> bufferEpisode) {
        ReplayBufferValueSetter<VariablesEnergyTrading, Integer> rbvs =
                new ReplayBufferValueSetter<>(bufferEpisode, DISCOUNT_FACTOR, IS_FIRST_VISIT);
        bufferTraining.addAll(rbvs.createBufferFromReturns(FRACTION_OF_EPISODE_BUFFER_TO_INCLUDE));  //candidate = createBufferFromAllReturns
        Conditionals.executeIfTrue(bufferTraining.size() > BUFFER_SIZE_TRAINING_LIMIT, () ->
                trainer.trainMemory(memory, bufferTraining));
       return rbvs;
    }

    private static void warnIfBadSolution() {
        TreeInfoHelper<VariablesEnergyTrading,Integer> tih=
                new TreeInfoHelper<>(monteCarloTreeCreator.getNodeRoot(), createSearchSettings());
        if (tih.getActionsOnBestPath().size()!= EnvironmentEnergyTrading.AFTER_MAX_TIME) {
            log.warning("No adequate solution found, tree not deep enough");
        }
    }

    private static void doPrinting(StateInterface<VariablesEnergyTrading> stateStart) {
        TreeInfoHelper<VariablesEnergyTrading,Integer> tih=
                new TreeInfoHelper<>(monteCarloTreeCreator.getNodeRoot(), createSearchSettings());
        List<ActionInterface<Integer>> actions= tih.getActionsOnBestPath();
        List<Double> rewards=simulator.stepWithActions(stateStart,actions);
        double sumOfRewards= ListUtils.sumDoubleList(rewards);
        double avgError=memory.getAverageValueError(bufferTraining.getBuffer());
        System.out.println("avgError = " + avgError);
        System.out.println("actions = " + actions);
        System.out.println("rewards = " + rewards);
        simulator.getStates().forEach(System.out::println);
        System.out.println("sumOfRewards = " + sumOfRewards);
    }

    public static MonteCarloTreeCreator<VariablesEnergyTrading, Integer> createTreeCreator(
            StateInterface<VariablesEnergyTrading> state,
            NetworkMemoryInterface<VariablesEnergyTrading, Integer> memory) {
        EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment = EnvironmentEnergyTrading.newDefault();
        ActionInterface<Integer> actionTemplate=  ActionEnergyTrading.newValue(0);
        MonteCarloSettings<VariablesEnergyTrading, Integer> settings = createSearchSettings();

        return MonteCarloTreeCreator.<VariablesEnergyTrading, Integer>builder()
                .environment(environment)
                .startState(state)
                .memory(memory)
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

    private static MonteCarloSettings<VariablesEnergyTrading, Integer> createSearchSettings() {
        return MonteCarloSettings.<VariablesEnergyTrading, Integer>builder()
                .actionSelectionPolicy(PoliciesEnergyTrading.newRandom())
                .simulationPolicy(PoliciesEnergyTrading.newRandom())
                .isDefensiveBackup(true)
                .alphaBackupDefensiveStep(0.1)
                .discountFactorBackupSimulationDefensive(0.1)
                .coefficientMaxAverageReturn(0.0) //average
                .maxTreeDepth(8)
                .maxNofIterations(1_000)  //100_000
                .timeBudgetMilliSeconds(10)
                .weightReturnsSteps(1.0)
                .weightReturnsSimulation(0.0)
                .weightMemoryValue(1.0)
                .nofSimulationsPerNode(0)
                .coefficientExploitationExploration(1e0)  //0.1
                .isCreatePlotData(false)
                .isDoLogging(false)
                .build();
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
