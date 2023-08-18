package multi_step_temp_diff.runners;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import common.Counter;
import common.CpuTimer;
import common.ListUtils;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.factories.TrainerFactory;
import multi_step_temp_diff.domain.helpers_specific.*;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import java.util.*;
import static java.lang.System.out;
import static multi_step_temp_diff.domain.helpers_specific.ChargeAgentParameters.*;


@Log
public class RunnerChargeHyperParameterOptimizer {

    private static final int NOF_EPIS = 2000;
    public static final int MAX_TRAIN_TIME_IN_SEC = 60*5;

    record ParameterSetup(int nofStepBetween, int batchSize, int nofLayers, int nofNeuronsHidden) {
        ParameterSetup(List<Integer> list) {
            this(list.get(0), list.get(1), list.get(2), list.get(3));
        }
    }

    static final Set<Integer> NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED_SET = ImmutableSet.of(1, 5, 10, 15);
    static final Set<Integer> BATCH_SIZE_SET = ImmutableSet.of(30, 50);
    static final Set<Integer> NOF_LAYERS_HIDDEN_SET = ImmutableSet.of(2, 5, 10);
    static final Set<Integer> NOF_NEURONS_HIDDEN_SET = ImmutableSet.of(5, 15, 25);

    static AgentNeuralInterface<ChargeVariables> agent;
    static NStepNeuralAgentTrainer<ChargeVariables> trainer;
    static EnvironmentInterface<ChargeVariables> environment;
    static ChargeEnvironmentLambdas lambdas;
    static ChargeEnvironment environmentCasted;
    static ChargeEnvironmentSettings envSettingsForTraining;

    public static void main(String[] args) {
        ChargeEnvironmentSettings envSettings = ChargeEnvironmentSettings.newDefault();
        envSettingsForTraining = envSettings.copyWithNewMaxNofSteps(MAX_NOF_STEPS_TRAINING);
        environment = new ChargeEnvironment(envSettingsForTraining);
        environmentCasted = (ChargeEnvironment) environment;
        lambdas = new ChargeEnvironmentLambdas(envSettingsForTraining);

        out.println("environment = " + environment);
        ChargeStateSuppliers stateSupplier = new ChargeStateSuppliers(envSettingsForTraining);
        ChargeAgentFactory agentFactory=ChargeAgentFactory.builder()
                .environment(environment).envSettings(envSettings)
                .build();

        Set<List<Integer>> parameterPermutations =
                Sets.cartesianProduct(ImmutableList.of(
                        NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED_SET,
                        BATCH_SIZE_SET,
                        NOF_LAYERS_HIDDEN_SET,
                        NOF_NEURONS_HIDDEN_SET));

        log.info("Nof parameter permutations = "+parameterPermutations.size());
        Map<ParameterSetup, Double> resultMapSumRewards = new HashMap<>();
        Map<ParameterSetup, Double> resultMapMeanTDerror = new HashMap<>();

        Counter counter=new Counter();
        CpuTimer timer=new CpuTimer();
        for (List<Integer> list : parameterPermutations) {
            out.println("permutation counter = " + counter);
            ParameterSetup parameterSetup = new ParameterSetup(list);
            agent = agentFactory.buildAgent(
                    ChargeState.newDummy(),
                    parameterSetup.nofLayers(),
                    parameterSetup.nofNeuronsHidden());
            TrainerFactory<ChargeVariables> trainerHelper = TrainerFactory.<ChargeVariables>builder()
                    .agent(agent).environment(environment)
                    .batchSize(parameterSetup.batchSize()).startEndProb(START_END_PROB)
                    .nofEpis(NOF_EPIS).maxTrainingTimeInMilliS(1000*MAX_TRAIN_TIME_IN_SEC)
                    .nofStepsBetweenUpdatedAndBackuped(parameterSetup.nofStepBetween())
                    .startStateSupplier(() -> stateSupplier.randomDifferentSitePositionsAndHighSoC())
                    .build();
            trainer = trainerHelper.buildTrainer();
            trainer.train();
            evaluateAndPutInResultMapSumRewards(resultMapSumRewards, parameterSetup,agent);
            putInResultMapMeanTDerror(resultMapMeanTDerror, parameterSetup,trainer);
            counter.increase();
        }

        out.println("Time in minutes = " + timer.timeInMinutesAsString());
        printResults(resultMapSumRewards,resultMapMeanTDerror);
    }

    private static void putInResultMapMeanTDerror(Map<ParameterSetup, Double> resultMapMeanTDerror,
                                                  ParameterSetup parameterSetup,
                                                  NStepNeuralAgentTrainer<ChargeVariables> trainer) {
        List<Double> valueHistory=trainer.getAgentInfo().getTemporalDifferenceTracker().getValueHistory();
        resultMapMeanTDerror.put(parameterSetup, ListUtils.findAverage(valueHistory).orElseThrow());
    }

    private static void evaluateAndPutInResultMapSumRewards(Map<ParameterSetup, Double> resultMap,
                                                            ParameterSetup parameterSetup,
                                                            AgentNeuralInterface<ChargeVariables> agent
                                                            ) {
        ChargeScenariosEvaluator evaluator = ChargeScenariosEvaluator.newAllScenarios(environment, agent);
        evaluator.evaluate();
        resultMap.put(parameterSetup, evaluator.getSumOfRewardAllScenarios());
    }

    private static void printResults(Map<ParameterSetup, Double> resultMapSumRewards,
                                     Map<ParameterSetup, Double> resultMapMeanTDError) {
        out.println("Non sorted results resultMapSumRewards" );
        resultMapSumRewards.entrySet().forEach(out::println);

        out.println("Non sorted results resultMapMeanTD" );
        resultMapMeanTDError.entrySet().forEach(out::println);

        out.println("Sorted results resultMapSumRewards");
        sortAndPrintMap(resultMapSumRewards);

        out.println("Sorted results resultMapMeanTDError");
        sortAndPrintMap(resultMapMeanTDError);

    }

    private static void sortAndPrintMap(Map<ParameterSetup, Double> resultMap) {
        List<Map.Entry<ParameterSetup, Double>> entryList = new ArrayList<>(resultMap.entrySet());
        entryList.sort(Map.Entry.comparingByValue());   // Sort the list based on values
        for (Map.Entry<ParameterSetup, Double> entry : entryList) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }


}
