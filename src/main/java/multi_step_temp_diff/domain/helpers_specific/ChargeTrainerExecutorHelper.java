package multi_step_temp_diff.domain.helpers_specific;

import common.CpuTimer;
import common.ListUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.factories.ReplayBufferFactory;
import multi_step_temp_diff.domain.factories.TrainerFactory;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.lang.System.out;
import static multi_step_temp_diff.domain.helpers_specific.ChargeAgentParameters.*;

@Log
@AllArgsConstructor
public class ChargeTrainerExecutorHelper {

    @Builder
    public record Settings(
            int nofTasks,
            int nofEpis,
            int maxTrainTimeInSeconds,
            int nofStepsBetweenUpdatedAndBackuped,
            int batchSize,
            int nofLayersHidden,
            int nofNeuronsHidden,
            @NonNull String bufferType,
            int bufferSize,
            double discountFactor,
            double learningRate,
            Pair<Double, Double> startEndProb
    ) {
    }

    Settings settings;
    EnvironmentInterface<ChargeVariables> environment;


    @NotNull
    public Map<NStepNeuralAgentTrainer<ChargeVariables>, Double> createTrainerEvaluationMap(
            List<NStepNeuralAgentTrainer<ChargeVariables>> trainers,
            EnvironmentInterface<ChargeVariables> environment) {
        Map<NStepNeuralAgentTrainer<ChargeVariables>, Double> trainerScoreMap = new HashMap<>();
        for (NStepNeuralAgentTrainer<ChargeVariables> trainer : trainers) {
            AgentNeuralInterface<ChargeVariables> agent = trainer.getAgentNeural();
            ChargeScenariosEvaluator evaluator = ChargeScenariosEvaluator.newAllScenarios(environment, agent);
            evaluator.evaluate();
            trainerScoreMap.put(trainer, evaluator.getSumOfRewardAllScenarios());
        }
        return trainerScoreMap;
    }

    public void printEvaluators(
            List<NStepNeuralAgentTrainer<ChargeVariables>> trainers,
            EnvironmentInterface<ChargeVariables> environment) {
        for (NStepNeuralAgentTrainer<ChargeVariables> trainer : trainers) {
            var evaluator = ChargeScenariosEvaluator.newAllScenarios(environment, trainer.getAgentNeural());
            evaluator.evaluate();
            out.println("evaluator = " + evaluator);
        }
    }


    @NotNull
    public List<NStepNeuralAgentTrainer<ChargeVariables>> runtTasksAndReturnResultingTrainers(
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

    @NotNull
    public Optional<Map.Entry<NStepNeuralAgentTrainer<ChargeVariables>, Double>> getBestTrainerAndItsScore(
            List<NStepNeuralAgentTrainer<ChargeVariables>> trainers) {
        var trainerScoreMap = createTrainerEvaluationMap(trainers, environment);
        return trainerScoreMap.entrySet().stream().max(Map.Entry.comparingByValue());
    }

    public OptionalDouble getAverageScore(
            Map<NStepNeuralAgentTrainer<ChargeVariables>, Double> trainerScoreMap) {
        List<Double> scores = trainerScoreMap.values().stream().toList();
        return ListUtils.findAverage(scores);
    }


    /**
     * a trainer includes an agent, so the trainer object below shall be seen as a team of a trainer and an agent
     */

    @NotNull
    public Set<NStepNeuralAgentTrainer<ChargeVariables>> createTasks(ChargeEnvironmentSettings envSettingsForTraining) {
        ChargeStateSuppliers stateSupplier = new ChargeStateSuppliers(envSettingsForTraining);
        ChargeAgentFactory agentFactory = ChargeAgentFactory.builder()
                .environment(environment).envSettings(envSettingsForTraining)
                .discountFactor(settings.discountFactor).learningRate(settings.learningRate)
                .build();
        Set<NStepNeuralAgentTrainer<ChargeVariables>> tasks = new HashSet<>();
        for (int i = 0; i < settings.nofTasks; i++) {
            var agent = agentFactory.buildAgent(ChargeState.newDummy(), settings.nofLayersHidden, settings.nofNeuronsHidden);
            TrainerFactory<ChargeVariables> trainerFactory = getTrainerFactory(stateSupplier, agent);
            NStepNeuralAgentTrainer<ChargeVariables> nStepNeuralAgentTrainer = trainerFactory.buildTrainer();
            tasks.add(nStepNeuralAgentTrainer);
        }
        return tasks;
    }


    public void printBestAndAverageScore(String name,
                                         List<NStepNeuralAgentTrainer<ChargeVariables>> trainers) {

        printEvaluators(trainers, environment);
        var bestTrainerAndItsScore = getBestTrainerAndItsScore(trainers);
        var trainerScoreMap = createTrainerEvaluationMap(trainers, environment);
        out.println(name + ", best evaluation value = " + bestTrainerAndItsScore.orElseThrow().getValue());
        var averageScore = getAverageScore(trainerScoreMap);
        out.println("averageScore = " + averageScore);
    }

    public void plotBestResult(ChargeEnvironmentSettings envSettings, String name, NStepNeuralAgentTrainer<ChargeVariables> bestTrainer) {
        var plotHelper = new ChargePlotHelper(bestTrainer.getAgentNeural(), bestTrainer, name);
        plotHelper.doMultiplePlots(envSettings);
    }

    public void saveBestResult(NStepNeuralAgentTrainer<ChargeVariables> bestTrainer) {
        AgentNeuralInterface<ChargeVariables> bestTrainedAgentNeural = bestTrainer.getAgentNeural();
        bestTrainedAgentNeural.saveMemory(FOLDER_NETWORKS + FILENAME_CHARGE_BOTH_FREE_NET);
    }

    public TrainerFactory<ChargeVariables> getTrainerFactory(ChargeStateSuppliers stateSupplier,
                                                             AgentNeuralInterface<ChargeVariables> agent) {

        ReplayBufferFactory<ChargeVariables> bufferFactory = new ReplayBufferFactory<>(settings.bufferType, settings.bufferSize);
        return TrainerFactory.<ChargeVariables>builder()
                .agent(agent).environment(environment)
                .batchSize(settings.batchSize).startEndProb(settings.startEndProb)
                .nofEpis(settings.nofEpis).maxTrainingTimeInMilliS(1000 * settings.maxTrainTimeInSeconds)
                .nofStepsBetweenUpdatedAndBackuped(settings.nofStepsBetweenUpdatedAndBackuped)
                .startStateSupplier(() -> stateSupplier.randomDifferentSitePositionsAndHighSoC())
                .buffer(bufferFactory.create())
                .build();
    }

}
