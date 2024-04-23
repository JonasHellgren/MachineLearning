package safe_rl.recorders;

import common.list_arrays.ListUtils;
import policy_gradient_problems.domain.value_classes.ProgressMeasures;
import policy_gradient_problems.helpers.RecorderStateValues;
import policy_gradient_problems.helpers.RecorderTrainingProgress;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.value_classes.Experience;
import safe_rl.helpers.EpisodeInfo;

import java.util.List;

public class Recorders<V> {

    public final RecorderStateValues recorderStateValues=new RecorderStateValues();
    public final RecorderTrainingProgress recorderTrainingProgress =new RecorderTrainingProgress();


    public void recordTrainingProgress(List<Experience<V>> experiences,
                                       AgentACDiscoI<V> agent) {
        var ei=new EpisodeInfo<>(experiences);
        List<Double> entropies=experiences.stream()
                .map(e -> agent.entropy(e.state())).toList();
        recorderTrainingProgress.add(ProgressMeasures.builder()
                .nSteps(ei.size())
                .sumRewards(ei.sumRewards())
                .criticLoss(agent.lossCriticLastUpdates())
                .actorLoss(agent.lossActorLastUpdates())
                .entropy(ListUtils.findAverage(entropies).orElseThrow())
                .build());
        agent.clearActorLosses();
        agent.clearCriticLosses();
    }


}
