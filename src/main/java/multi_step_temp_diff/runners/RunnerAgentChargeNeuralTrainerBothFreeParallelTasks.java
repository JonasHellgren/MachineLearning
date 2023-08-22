package multi_step_temp_diff.runners;

import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.helpers_specific.*;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import lombok.extern.java.Log;
import org.apache.commons.lang3.tuple.Pair;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static multi_step_temp_diff.domain.helpers_specific.ChargeAgentParameters.*;

@Log
public class RunnerAgentChargeNeuralTrainerBothFreeParallelTasks {
    public static final int NOF_TASKS = 16;  //lets no core be lazy
    private static final int NOF_EPIS = 10_000, MAX_TRAIN_TIME_IN_SECONDS = 100 * 60;  //one will limit
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 10;
    public static final int BATCH_SIZE1 = 100;
    public static final int NOF_LAYERS_HIDDEN = 5;
    public static final int NOF_NEURONS_HIDDEN = 27;
    public static final double LEARNING_RATE1 = 0.1, DISCOUNT_FACTOR = 0.95;
    public static final Pair<Double, Double> START_END_PROB = Pair.of(0.9, 0.1);

    static EnvironmentInterface<ChargeVariables> environment;

    public static void main(String[] args) throws InterruptedException {
        ChargeEnvironmentSettings envSettings = ChargeEnvironmentSettings.newDefault();
        ChargeEnvironmentSettings envSettingsForTraining = envSettings.copyWithNewMaxNofSteps(MAX_NOF_STEPS_TRAINING);
        environment = new ChargeEnvironment(envSettingsForTraining);
        int nofCores = Runtime.getRuntime().availableProcessors();
        log.info("nofCores = " + nofCores);

        List<Pair<String,Integer>> bufferNameList = List.of(
                Pair.of("Uniform",MAX_BUFFER_SIZE_EXPERIENCE_REPLAY),
                Pair.of("Prioritized",MAX_BUFFER_SIZE_EXPERIENCE_REPLAY)
        );

        for(var bufferNameSizePair:bufferNameList) {
            String bufferType = bufferNameSizePair.getLeft();
            Integer bufferSize = bufferNameSizePair.getRight();
            ExecutorService executorService = Executors.newFixedThreadPool(nofCores);
            ChargeTrainerExecutorHelper.Settings helperSettings = ChargeTrainerExecutorHelper.Settings.builder()
                    .nofTasks(NOF_TASKS).nofEpis(NOF_EPIS).maxTrainTimeInSeconds(MAX_TRAIN_TIME_IN_SECONDS)
                    .nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                    .batchSize(BATCH_SIZE1).nofLayersHidden(NOF_LAYERS_HIDDEN).nofNeuronsHidden(NOF_NEURONS_HIDDEN)
                    .discountFactor(DISCOUNT_FACTOR).learningRate(LEARNING_RATE1).startEndProb(START_END_PROB)
                    .bufferType(bufferType).bufferSize(bufferSize)
                    .build();
            ChargeTrainerExecutorHelper helper = new ChargeTrainerExecutorHelper(helperSettings, environment);

            var tasks = helper.createTasks(envSettingsForTraining);
            var trainers = helper.runtTasksAndReturnResultingTrainers(tasks, executorService);
            helper.printBestAndAverageScore(bufferType, trainers);
            var bestTrainerAndItsScore = helper.getBestTrainerAndItsScore(trainers);
            NStepNeuralAgentTrainer<ChargeVariables> bestTrainer = bestTrainerAndItsScore.orElseThrow().getKey();
            helper.saveBestResult(bestTrainer);
            helper.plotBestResult(envSettings, bufferType, bestTrainer);
            executorService.shutdown();
        }
    }


}