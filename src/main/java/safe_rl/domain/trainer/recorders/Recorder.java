package safe_rl.domain.trainer.recorders;

import common.list_arrays.ListUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import policy_gradient_problems.domain.value_classes.ProgressMeasures;
import policy_gradient_problems.helpers.RecorderStateValues;
import policy_gradient_problems.helpers.RecorderTrainingProgress;
import safe_rl.domain.agent.helpers.LossTracker;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.simulator.value_objects.SimulationResult;
import safe_rl.domain.simulator.AgentSimulator;
import safe_rl.domain.trainer.helpers.EpisodeInfo;

import java.util.List;

@AllArgsConstructor
public class Recorder<V> {

    public final RecorderTrainingProgress recorderTrainingProgress = new RecorderTrainingProgress();
    AgentSimulator<V> simulator;

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
