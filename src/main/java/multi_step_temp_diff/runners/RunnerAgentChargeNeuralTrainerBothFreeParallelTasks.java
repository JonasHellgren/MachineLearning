package multi_step_temp_diff.runners;

import common.CpuTimer;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.helpers_specific.*;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.System.out;
import static multi_step_temp_diff.domain.helpers_specific.ChargeAgentParameters.*;
import static multi_step_temp_diff.domain.helpers_specific.ChargeAgentParameters.FILENAME_CHARGE_BOTH_FREE_NET;

@Log
public class RunnerAgentChargeNeuralTrainerBothFreeParallelTasks {
    public static final int NOF_TASKS = 10;
    private static final int NOF_EPIS = 1000, MAX_TRAIN_TIME_IN_SECONDS = 1*10;  //one will limit
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 10;
    public static final int BATCH_SIZE1 = 30;
    public static final int NOF_LAYERS_HIDDEN = 10;
    public static final int NOF_NEURONS_HIDDEN = 15;

    static EnvironmentInterface<ChargeVariables> environment;

    public static void main(String[] args) throws InterruptedException {
        ChargeEnvironmentSettings envSettings = ChargeEnvironmentSettings.newDefault();
        ChargeEnvironmentSettings envSettingsForTraining = envSettings.copyWithNewMaxNofSteps(MAX_NOF_STEPS_TRAINING);
        environment = new ChargeEnvironment(envSettingsForTraining);
        ExecutorService executorService = createExecutorService();

        Set<NStepNeuralAgentTrainer<ChargeVariables>> tasks = createTasks(envSettingsForTraining);
        List<NStepNeuralAgentTrainer<ChargeVariables>> trainers = runtTasksAndReturnResultingTrainers(tasks, executorService);
        Map<NStepNeuralAgentTrainer<ChargeVariables>, Double> trainerScoreMap = createTrainerEvaluationMap(trainers);
        Optional<Map.Entry<NStepNeuralAgentTrainer<ChargeVariables>, Double>> bestTrainerAndItsScore =
                trainerScoreMap.entrySet().stream().max(Map.Entry.comparingByValue());
        out.println("best evaluation value = " + bestTrainerAndItsScore.orElseThrow().getValue());
        NStepNeuralAgentTrainer<ChargeVariables> bestTrainer = bestTrainerAndItsScore.orElseThrow().getKey();
        AgentNeuralInterface<ChargeVariables> bestTrainedAgentNeural = bestTrainer.getAgentNeural();
        bestTrainedAgentNeural.saveMemory(FOLDER_NETWORKS + FILENAME_CHARGE_BOTH_FREE_NET);
        ChargePlotHelper plotHelper = new ChargePlotHelper(bestTrainer.getAgentNeural(), bestTrainer);
        plotHelper.doMultiplePlots(envSettings);
        executorService.shutdown();

    }

    @NotNull
    private static Map<NStepNeuralAgentTrainer<ChargeVariables>, Double> createTrainerEvaluationMap(
            List<NStepNeuralAgentTrainer<ChargeVariables>> trainers) {
        Map<NStepNeuralAgentTrainer<ChargeVariables>, Double> trainerScoreMap = new HashMap<>();
        for (NStepNeuralAgentTrainer<ChargeVariables> trainer : trainers) {
            AgentNeuralInterface<ChargeVariables> agent = trainer.getAgentNeural();
            ChargeScenariosEvaluator evaluator = ChargeScenariosEvaluator.newAllScenarios(environment, agent);
            evaluator.evaluate();
            trainerScoreMap.put(trainer, evaluator.getSumOfRewardAllScenarios());
            out.println("evaluator = " + evaluator);
        }
        return trainerScoreMap;
    }

    @NotNull
    private static List<NStepNeuralAgentTrainer<ChargeVariables>> runtTasksAndReturnResultingTrainers(
            Set<NStepNeuralAgentTrainer<ChargeVariables>> tasks,
            ExecutorService executorService) throws InterruptedException {
        CpuTimer timer = new CpuTimer();
        List<Future<NStepNeuralAgentTrainer<ChargeVariables>>> futures = executorService.invokeAll(tasks);
        log.info("time for running all tasks = " + timer);
        List<NStepNeuralAgentTrainer<ChargeVariables>> trainers = new ArrayList<>();
        for (Future<NStepNeuralAgentTrainer<ChargeVariables>> future : futures) {
            try {
                trainers.add(future.get());
            } catch (Exception e) {
                log.severe("Serious problem");
                throw new RuntimeException(e);
            }
        }
        return trainers;
    }

    private static ExecutorService createExecutorService() {
        int nofCores = Runtime.getRuntime().availableProcessors();
        log.info("nofCores = " + nofCores);
        return Executors.newFixedThreadPool(nofCores);

    }

    /**
     a trainer includes an agent, so the trainer object below shall be seen as a team of a trainer and an agent
     */

    @NotNull
    private static Set<NStepNeuralAgentTrainer<ChargeVariables>> createTasks(ChargeEnvironmentSettings envSettingsForTraining) {
        ChargeStateSuppliers stateSupplier = new ChargeStateSuppliers(envSettingsForTraining);
        ChargeAgentFactory agentFactory = ChargeAgentFactory.builder()
                .environment(environment).envSettings(envSettingsForTraining)
                .build();
        Set<NStepNeuralAgentTrainer<ChargeVariables>> tasks = new HashSet<>();
        for (int i = 0; i < NOF_TASKS; i++) {
            AgentNeuralInterface<ChargeVariables> agent = agentFactory.buildAgent(
                    ChargeState.newDummy(),
                    NOF_LAYERS_HIDDEN,
                    NOF_NEURONS_HIDDEN);
            ChargeTrainerFactory<ChargeVariables> trainerFactory = getTrainerFactory(stateSupplier, agent, environment);
            NStepNeuralAgentTrainer<ChargeVariables> nStepNeuralAgentTrainerCallable =
                    trainerFactory.buildTrainer();
            tasks.add(nStepNeuralAgentTrainerCallable);
        }
        return tasks;
    }


    private static ChargeTrainerFactory<ChargeVariables> getTrainerFactory(ChargeStateSuppliers stateSupplier,
                                                                           AgentNeuralInterface<ChargeVariables> agent,
                                                                           EnvironmentInterface<ChargeVariables> environment) {

        return ChargeTrainerFactory.<ChargeVariables>builder()
                .agent(agent).environment(environment)
                .batchSize(BATCH_SIZE1).startEndProb(START_END_PROB)
                .nofEpis(NOF_EPIS).maxTrainingTimeInMilliS(1000 * MAX_TRAIN_TIME_IN_SECONDS)
                .nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                .startStateSupplier(() -> stateSupplier.randomDifferentSitePositionsAndHighSoC())
                .build();
    }


}