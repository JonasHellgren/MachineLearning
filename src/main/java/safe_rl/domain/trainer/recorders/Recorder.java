package safe_rl.domain.trainer.recorders;

import common.list_arrays.ListUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import policy_gradient_problems.domain.value_classes.ProgressMeasures;
import policy_gradient_problems.helpers.RecorderStateValues;
import policy_gradient_problems.helpers.RecorderTrainingProgress;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.simulator.value_objects.SimulationResult;
import safe_rl.domain.simulator.AgentSimulator;
import safe_rl.domain.trainer.helpers.EpisodeInfo;

import java.util.List;

@AllArgsConstructor
public class Recorder<V> {

    public final RecorderStateValues recorderStateValues = new RecorderStateValues();
    public final RecorderTrainingProgress recorderTrainingProgress = new RecorderTrainingProgress();
    AgentSimulator<V> simulator;


    @SneakyThrows
    public void recordTrainingProgress(List<Experience<V>> experiences,
                                       AgentACDiscoI<V> agent) {
        var ei = new EpisodeInfo<>(experiences);
        List<Double> entropies = experiences.stream()
                .map(e -> agent.entropy(e.state())).toList();
        var simulationResults = simulator.simulateWithNoExploration();
        recorderTrainingProgress.add(ProgressMeasures.builder()
                .nSteps(ei.size())
                .sumRewards(ei.sumRewards())
                .eval(SimulationResult.sumRewards(simulationResults))
                .criticLoss(agent.lossCriticLastUpdates())
                .actorLoss(agent.lossActorLastUpdates())
                .entropy(ListUtils.findAverage(entropies).orElseThrow())
                .build());
        agent.clearActorLosses();
        agent.clearCriticLosses();
    }


}
