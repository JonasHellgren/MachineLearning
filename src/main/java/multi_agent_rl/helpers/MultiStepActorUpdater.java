package multi_agent_rl.helpers;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.value_classes.MultiStepResults;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.helpers.CorrectedActionPenalizer;

import java.util.List;

/**
  For learning directly from latest episode
 */

@AllArgsConstructor
public class MultiStepActorUpdater<V> {

    @NonNull AgentACDiscoI<V> agent;
    @NonNull TrainerParameters parameters;

    @SneakyThrows
    public void update(MultiStepResults<V> msr, List<Double> lossCriticList) {
        var penalizer = CorrectedActionPenalizer.<V>builder()
                .agent(agent).parameters(parameters)
                .lossCriticList(lossCriticList).multiStepResults(msr)
                .build();

        for (int step = 0; step < msr.nExperiences(); step++) {
            penalizer.maybePenalize(step);
            agent.fitActor(msr.stateAtStep(step),msr.actionAppliedAtStep(step),msr.advantageAtStep(step));
        }

    }


}
