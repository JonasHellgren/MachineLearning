package multi_step_temp_diff.runners;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import common.Counter;
import common.CpuTimer;
import common.ListUtils;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.helpers_specific.*;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.out;
import static multi_step_temp_diff.domain.helpers_specific.ChargeAgentParameters.*;


@Log
public class RunnerChargeHyperParameterOptimizer {
    public static final int NOF_TASKS = 16;
    private static final int NOF_EPIS = 10_000, MAX_TRAIN_TIME_IN_SECONDS = 3 * 60;  //one will limit
    public static final String BUFFER_TYPE = "Uniform";  //"Prioritized"

    record ParameterSetup(int nofStepBetween, int batchSize, int nofLayers, int nofNeuronsHidden) {
        ParameterSetup(List<Integer> list) {
            this(list.get(0), list.get(1), list.get(2), list.get(3));
        }
    }

    static final Set<Integer> NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED_SET = ImmutableSet.of(1, 5, 10, 15);
    static final Set<Integer> BATCH_SIZE_SET = ImmutableSet.of(30, 50);
    static final Set<Integer> NOF_LAYERS_HIDDEN_SET = ImmutableSet.of(2, 5, 10);
    static final Set<Integer> NOF_NEURONS_HIDDEN_SET = ImmutableSet.of(5, 15, 25);

    static EnvironmentInterface<ChargeVariables> environment;

    @SneakyThrows
    public static void main(String[] args) {
        ChargeEnvironmentSettings envSettings = ChargeEnvironmentSettings.newDefault();
        ChargeEnvironmentSettings envSettingsForTraining = envSettings.copyWithNewMaxNofSteps(MAX_NOF_STEPS_TRAINING);
        environment = new ChargeEnvironment(envSettingsForTraining);
        int nofCores = Runtime.getRuntime().availableProcessors();
        log.info("nofCores = " + nofCores);

        Set<List<Integer>> parameterPermutations =
                Sets.cartesianProduct(ImmutableList.of(
                        NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED_SET,
                        BATCH_SIZE_SET,
                        NOF_LAYERS_HIDDEN_SET,
                        NOF_NEURONS_HIDDEN_SET));


        log.info("Nof parameter permutations = "+parameterPermutations.size());
        Map<ParameterSetup, List<Double>> resultMapSumRewards = new HashMap<>();  //setup, sumRewardsDifferentAgents
        Map<ParameterSetup, List<Double>> resultMapMeanTDerror = new HashMap<>();  //setup, tempDiffDifferentAgents

        Counter counter=new Counter();
        CpuTimer timer=new CpuTimer();
        for (List<Integer> list : parameterPermutations) {
            out.println("permutation counter = " + counter);
            ExecutorService executorService = Executors.newFixedThreadPool(nofCores);

            ParameterSetup parameterSetup = new ParameterSetup(list);
            ChargeTrainerExecutorHelper helper = getChargeTrainerExecutorHelper(
                    BUFFER_TYPE,
                    MAX_BUFFER_SIZE_EXPERIENCE_REPLAY,
                    parameterSetup);
            var tasks = helper.createTasks(envSettingsForTraining);
            var trainers = helper.runtTasksAndReturnResultingTrainers(tasks, executorService);

            for (var trainer:trainers) {
                evaluateAndPutInResultMapSumRewards(resultMapSumRewards, parameterSetup, trainer.getAgentNeural());
                putInResultMapMeanTDerror(resultMapMeanTDerror, parameterSetup, trainer);
            }
            counter.increase();
            executorService.shutdown();
        }

        out.println("resultMapMeanTDerror.size() = " + resultMapMeanTDerror.size());

        out.println("Time in minutes = " + timer.timeInMinutesAsString());
        printResults(resultMapSumRewards,resultMapMeanTDerror);
    }

    @NotNull
    private static ChargeTrainerExecutorHelper getChargeTrainerExecutorHelper(String bufferType,
                                                                              Integer bufferSize,
                                                                              ParameterSetup parameterSetup) {
        ChargeTrainerExecutorHelper.Settings helperSettings = ChargeTrainerExecutorHelper.Settings.builder()
                .nofTasks(NOF_TASKS).nofEpis(NOF_EPIS).maxTrainTimeInSeconds(MAX_TRAIN_TIME_IN_SECONDS)
                .nofStepsBetweenUpdatedAndBackuped(parameterSetup.nofStepBetween())
                .batchSize(parameterSetup.batchSize())
                .nofLayersHidden(parameterSetup.nofLayers())
                .nofNeuronsHidden(parameterSetup.nofNeuronsHidden())
                .bufferType(bufferType).bufferSize(bufferSize)
                .build();
        return new ChargeTrainerExecutorHelper(helperSettings, environment);
    }

    private static void evaluateAndPutInResultMapSumRewards(Map<ParameterSetup, List<Double>> resultMap,
                                                            ParameterSetup parameterSetup,
                                                            AgentNeuralInterface<ChargeVariables> agent
    ) {
        ChargeScenariosEvaluator evaluator = ChargeScenariosEvaluator.newAllScenarios(environment, agent);
        evaluator.evaluate();
        double sumOfRewards = evaluator.getSumOfRewardAllScenarios();
        resultMap.putIfAbsent(parameterSetup,List.of(sumOfRewards));
        resultMap.computeIfPresent(parameterSetup, (p,list) -> ListUtils.merge(List.of(sumOfRewards),list) );
    }

    private static void putInResultMapMeanTDerror(Map<ParameterSetup, List<Double>> resultMapMeanTDerror,
                                                  ParameterSetup parameterSetup,
                                                  NStepNeuralAgentTrainer<ChargeVariables> trainer) {
        List<Double> valueHistory=trainer.getAgentInfo().getTemporalDifferenceTracker().getValueHistory();
        double averageTdError = ListUtils.findAverage(valueHistory).orElseThrow();
        resultMapMeanTDerror.putIfAbsent(parameterSetup,List.of(averageTdError));
        resultMapMeanTDerror.computeIfPresent(parameterSetup, (p,list) -> ListUtils.merge(List.of(averageTdError),list) );
    }



    private static void printResults(Map<ParameterSetup, List<Double>> resultMapSumRewards,
                                     Map<ParameterSetup, List<Double>> resultMapMeanTDError) {


        out.println("Non sorted results resultMapMeanTD -------------------" );
        List<Pair<ParameterSetup, Double>> meanTdErrPairs = getPairs(resultMapMeanTDError);
        meanTdErrPairs.forEach(out::println);

        out.println("Non sorted results resultMapSumRewards ------------------- " );
        List<Pair<ParameterSetup, Double>> sumRewardPairs = getPairs(resultMapSumRewards);
        sumRewardPairs.forEach(out::println);

        out.println("Sorted results resultMapMeanTDError -------------------");
        sortAndPrintListOfPairs(meanTdErrPairs);

        out.println("Sorted results resultMapSumRewards -------------------");
        sortAndPrintListOfPairs(sumRewardPairs);

    }

    private static void sortAndPrintListOfPairs(List<Pair<ParameterSetup, Double>> pairList) {
        List<Pair<ParameterSetup, Double>> pairListMutable=new ArrayList<>(pairList);
        pairListMutable.sort(Comparator.comparing(Pair::getRight));
        pairListMutable.forEach(out::println);
    }

    @NotNull
    private static List<Pair<ParameterSetup, Double>> getPairs(Map<ParameterSetup, List<Double>> resultMap) {

        List<Map.Entry<ParameterSetup, List<Double>>> entryListWithLists = new ArrayList<>(resultMap.entrySet());
        return entryListWithLists.stream()
                .map(e -> Pair.of(
                        e.getKey(),
                        ListUtils.findAverage(e.getValue()).orElseThrow()))
                .toList();
    }


}
