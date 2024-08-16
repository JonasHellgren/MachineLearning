package multi_agent_rl.helpers;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.trainer.value_objects.MultiStepResults;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.domain.trainer.helpers.CorrectedActionPenalizer;

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

        throw new IllegalArgumentException("To fix  using safe rl classes");
     /*   var penalizer = CorrectedActionPenalizer.<V>builder()
                .agent(agent).parameters(parameters)
                //.lossCriticList(lossCriticList)  //.multiStepResults(msr)
                .build();
*/
/*
        for (int step = 0; step < msr.nExperiences(); step++) {
            //penalizer.maybePenalize(step, msr, lossCriticList);
            agent.fitActor(msr.stateAtStep(step),msr.actionAppliedAtStep(step),msr.advantageAtStep(step));
        }
*/

    }


}
