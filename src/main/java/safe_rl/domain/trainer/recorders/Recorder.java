package safe_rl.domain.trainer.recorders;

import common.list_arrays.ListUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import policy_gradient_problems.domain.value_classes.ProgressMeasures;
import safe_rl.domain.agent.helpers.LossTracker;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.simulator.value_objects.SimulationResult;
import safe_rl.domain.simulator.AgentSimulator;
import safe_rl.domain.trainer.helpers.EpisodeInfo;
import safe_rl.domain.trainer.value_objects.TrainerParameters;

import java.util.List;

public class Recorder<V> {

    @Delegate
    RecorderTrainingProgress recorderTrainingProgress;

    AgentSimulator<V> simulator;
    TrainerParameters trainerParameters;

    public Recorder(AgentSimulator<V> simulator, TrainerParameters trainerParameters) {
        this.simulator = simulator;
        this.trainerParameters = trainerParameters;
        this.recorderTrainingProgress = new RecorderTrainingProgress(trainerParameters);
    }

    @SneakyThrows
    public void recordTrainingProgress(List<Experience<V>> experiences,
                                       AgentACDiscoI<V> agent) {
        var ei = new EpisodeInfo<>(experiences);
        List<Double> entropies = experiences.stream()
                .map(e -> agent.entropy(e.state())).toList();
        var simulationResults = simulator.simulateWithNoExploration();
        var lossTracker = agent.getLossTracker();
        recorderTrainingProgress.add(ProgressMeasures.builder()
                .nSteps(ei.size())
                .sumRewards(ei.sumRewards())
                .eval(SimulationResult.sumRewards(simulationResults))
                .criticLoss(lossCriticLastUpdates(lossTracker))
                .actorLoss(lossActorLastUpdates(lossTracker))
                .entropy(ListUtils.findAverage(entropies).orElseThrow())
                .build());
        clearLossesRecording(lossTracker);
    }

    public double lossCriticLastUpdates(LossTracker lossTracker) {
        return lossTracker.averageCriticLosses();
    }

    public double lossActorLastUpdates(LossTracker lossTracker) {
        return lossTracker.averageMeanLosses() + lossTracker.averageStdLosses();
    }

    public void clearLossesRecording(LossTracker lossTracker) {
        lossTracker.clearCriticLosses();
        lossTracker.clearActorLosses();
    }


}
