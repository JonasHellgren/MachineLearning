package safe_rl.domain.trainer.helpers;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.trainer.value_objects.MultiStepResults;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import java.util.List;

/**
  For learning directly from latest episode
 */

@AllArgsConstructor
public class MultiStepActorUpdater<V> {

    @NonNull AgentACDiscoI<V> agent;
    @NonNull TrainerParameters parameters;

    @SneakyThrows
    public void update(MultiStepResults<V> msr) {
         for (int step = 0; step < msr.nExperiences(); step++) {
            agent.fitActor(msr.stateAtStep(step),msr.actionAppliedAtStep(step),msr.advantageAtStep(step));
        }
    }

}
