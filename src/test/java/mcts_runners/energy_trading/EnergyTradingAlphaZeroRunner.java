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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log
public class EnergyTradingAlphaZeroRunner {

    private static final int NOF_SIMULATIONS = 100;  //todo skall inte behövas
    private static final int MAX_SIMULATION_DEPTH = 10;
    private static final int NOF_EPISODES = 500;
    private static final int NOF_EPISODES_BETWEEN_LOGGING = 10;
 //   private static final int BUFFER_SIZE = 1_000;
    private static final int MINI_BATCH_SIZE = 20;
    private static final double MAX_ERROR = 1e-8;  //1e-5;
    private static final int MAX_NOF_EPOCHS = 10;
    private static final int BUFFER_SIZE_TRAINING = 5_000;  //todo skall inte behövas
    private static final int BUFFER_SIZE_EPISODE = 1_000;  //todo skall inte behövas
    private static final double PROBABILITY_RANDOM_ACTION_START = 0.1;
    private static final double PROBABILITY_RANDOM_ACTION_END = 0.1;
    private static final double DISCOUNT_FACTOR = 1.0;
    private static final int BUFFER_SIZE_TRAINING_LIMIT = MINI_BATCH_SIZE;
    private static final boolean IS_FIRST_VISIT = true;
    private static final double FRACTION_OF_EPISODE_BUFFER_TO_INCLUDE = 1.0;
    private static final int ZERO_VALUE = 0;
    private static final int NOF_EPISODES_RESETTING_MEMORY = 100_000;
    private static final List<Double> WEIGHTS_MEM= Arrays.asList(0.0,0.25,0.5,0.75,1.0,1.25,1.5);
    private static final int MAX_TREE_DEPTH =3;
    private static final double COEFFICIENT_EXPLOITATION_EXPLORATION = 1e0;
    private static final int START_TIME = 0;
    private static final double START_SOE = 0.5;


    static ActionInterface<Integer> actionTemplate;
    static EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment;
    static MonteCarloSimulator<VariablesEnergyTrading,Integer> simulator;
  //  static MemoryTrainerInterface<VariablesEnergyTrading, Integer> trainer;
    static NetworkMemoryInterface<VariablesEnergyTrading, Integer> memory;
    static MonteCarloTreeCreator<VariablesEnergyTrading, Integer> searcherTraining;
    static ReplayBuffer<VariablesEnergyTrading, Integer> bufferTraining;
    static ReplayBuffer<VariablesEnergyTrading, Integer> bufferEpisode;
    static ScalerLinear probScaler;

    @SneakyThrows
    public static void main(String[] args) {
        actionTemplate=  ActionEnergyTrading.newValue(0);
        StateInterface<VariablesEnergyTrading> stateStart= StateEnergyTrading.newFromTimeAndSoE(0,0.5);
        environment = EnvironmentEnergyTrading.newDefault();
        simulator= new MonteCarloSimulator<>(environment,createSimulatorSettings());
        memory=new EnergyTraderValueMemory<>();
       // trainer=new EnergyTraderMemoryTrainer(MINI_BATCH_SIZE, BUFFER_SIZE_TRAINING, MAX_ERROR, MAX_NOF_EPOCHS);
        searcherTraining = createSearcherTraining(stateStart,memory);
        bufferTraining = new ReplayBuffer<>(BUFFER_SIZE_TRAINING);
        bufferEpisode = new ReplayBuffer<>(BUFFER_SIZE_EPISODE);
        probScaler=new ScalerLinear(0,NOF_EPISODES,PROBABILITY_RANDOM_ACTION_START,PROBABILITY_RANDOM_ACTION_END);

        resetMemory(memory);
        printMemory();


        for (int episode = 0; episode < NOF_EPISODES; episode++) {
            boolean isTerminal = false;
            StateInterface<VariablesEnergyTrading> state = StateEnergyTrading.newFromTimeAndSoE(
                    RandUtils.getRandomIntNumber(START_TIME,EnvironmentEnergyTrading.MAX_TIME+1),
                    RandUtils.getRandomDouble(EnvironmentEnergyTrading.SOE_MIN,EnvironmentEnergyTrading.SOE_MAX));
             state.getVariables().SoE=RandUtils.getRandomDouble(EnvironmentEnergyTrading.SOE_MIN,EnvironmentEnergyTrading.SOE_MAX);

          //  StateInterface<VariablesEnergyTrading> state =StateEnergyTrading.newRandom();
            bufferEpisode.clear();
            int step = 0;
            StepReturnGeneric<VariablesEnergyTrading> sr=null;
            while (!isTerminal) {

                ActionInterface<Integer> action = isRandomAction(probScaler, episode)
                        ? ActionEnergyTrading.newRandom()
                        : getActionFromSearch(searcherTraining, state);

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

       // System.out.println("bufferTraining = " + bufferTraining);

        printMemory();


        for (Double weightMem:WEIGHTS_MEM) {
            EnergyTradingRunner runner = new EnergyTradingRunner(createSearcherRunner(stateStart, memory, weightMem), environment);
            runner.run(StateEnergyTrading.newFromTimeAndSoE(START_TIME, START_SOE));
            System.out.println("weightMem = "+weightMem+System.lineSeparator()+", runner.toString() = " + runner.toString());
        }
    }

    private static void printMemory() {
        Map<Double, Double> soEValueMap=new HashMap<>();
        for (int time=START_TIME;time<EnvironmentEnergyTrading.AFTER_MAX_TIME;time++) {
            soEValueMap.clear();
            for (double SoE:Arrays.asList(0.8,0.5,0.3))  {
                soEValueMap.put(SoE,memory.read(StateEnergyTrading.newFromTimeAndSoE(time,SoE)));
            }
            System.out.println("time = " + time+", soEValueMap= "+soEValueMap);
        }

    }

    private static void addExperienceOfTerminal(StepReturnGeneric<VariablesEnergyTrading> sr) {
        sr.reward=0; //sr.isFail ? EnvironmentEnergyTrading.REWARD_FAIL:0;
        addExperience(bufferEpisode, sr.newState, ActionEnergyTrading.newValue(0), sr);
    }

    private static void someLogging(ReplayBuffer<VariablesEnergyTrading, Integer> bufferTrainig, int episode, int step) {
        Conditionals.executeIfTrue(episode % NOF_EPISODES_BETWEEN_LOGGING == 0, () ->
        log.info("episode = " + episode + ", steps = " + step +", buffersize = " + bufferTrainig.size()));
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
                new EnergyTraderMemoryTrainer(MINI_BATCH_SIZE, 1e-8, 10_000);
        Conditionals.executeIfTrue(buffer.size() > BUFFER_SIZE_TRAINING_LIMIT, () ->
                trainer.trainMemory(memory, buffer));
    }

    private static void trainMemory(
            NetworkMemoryInterface<VariablesEnergyTrading,Integer> memory,
            ReplayBuffer<VariablesEnergyTrading, Integer> bufferTraining,
            ReplayBuffer<VariablesEnergyTrading, Integer> bufferEpisode) {
        ReplayBufferValueSetter<VariablesEnergyTrading, Integer> rbvs =
                new ReplayBufferValueSetter<>(bufferEpisode, DISCOUNT_FACTOR, IS_FIRST_VISIT);
        bufferTraining.addAll(rbvs.createBufferFromReturns(FRACTION_OF_EPISODE_BUFFER_TO_INCLUDE));  //candidate = createBufferFromAllReturns
        MemoryTrainerInterface<VariablesEnergyTrading, Integer> trainer=
                new EnergyTraderMemoryTrainer(MINI_BATCH_SIZE, MAX_ERROR, MAX_NOF_EPOCHS);

        Conditionals.executeIfTrue(bufferTraining.size() > BUFFER_SIZE_TRAINING_LIMIT, () ->
                trainer.trainMemory(memory, bufferTraining));
    }

    private static void warnIfBadSolution() {
        TreeInfoHelper<VariablesEnergyTrading,Integer> tih=
                new TreeInfoHelper<>(searcherTraining.getNodeRoot(), createSearchTrainerSettings());
        if (tih.getActionsOnBestPath().size()!= EnvironmentEnergyTrading.AFTER_MAX_TIME) {
            log.warning("No adequate solution found, tree not deep enough");
        }
    }

    private static void doPrinting(StateInterface<VariablesEnergyTrading> stateStart) {
        TreeInfoHelper<VariablesEnergyTrading,Integer> tih=
                new TreeInfoHelper<>(searcherTraining.getNodeRoot(), createSearchTrainerSettings());
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

    public static MonteCarloTreeCreator<VariablesEnergyTrading, Integer> createSearcherTraining(
            StateInterface<VariablesEnergyTrading> state,
            NetworkMemoryInterface<VariablesEnergyTrading, Integer> memory) {
        EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment = EnvironmentEnergyTrading.newDefault();
          return MonteCarloTreeCreator.<VariablesEnergyTrading, Integer>builder()
                .environment(environment)
                .startState(state)
                .memory(memory)
                .monteCarloSettings(createSearchTrainerSettings())
                .actionTemplate(actionTemplate)
                .build();
    }

    private static MonteCarloSettings<VariablesEnergyTrading, Integer> createSearchTrainerSettings() {
        return MonteCarloSettings.<VariablesEnergyTrading, Integer>builder()
                .actionSelectionPolicy(PoliciesEnergyTrading.newRandom())
                .simulationPolicy(PoliciesEnergyTrading.newRandom())
                .isDefensiveBackup(true)
                .alphaBackupDefensiveStep(0.1)
                .discountFactorBackupSimulationDefensive(0.1)
                .coefficientMaxAverageReturn(0.0) //average
                .maxTreeDepth(MAX_TREE_DEPTH)
                .minNofIterations(100).maxNofIterations(1_000).timeBudgetMilliSeconds(20)
                .weightReturnsSteps(1.0).weightReturnsSimulation(0.0).weightMemoryValue(1.0)
                .nofSimulationsPerNode(0)
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)  //0.1
                .isCreatePlotData(false)
                .isDoLogging(false)
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
                .isDefensiveBackup(true)
                .alphaBackupDefensiveStep(0.1)
                .discountFactorBackupSimulationDefensive(0.1)
                .coefficientMaxAverageReturn(0.0) //average
                .maxTreeDepth(5)  //MAX_TREE_DEPTH
                .minNofIterations(100).maxNofIterations(10_000).timeBudgetMilliSeconds(100)
                .weightReturnsSteps(1.0).weightReturnsSimulation(0.0).weightMemoryValue(weightMem)
                .nofSimulationsPerNode(0)
                .coefficientExploitationExploration(COEFFICIENT_EXPLOITATION_EXPLORATION)  //0.1
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
