package safe_rl.domain.trainer.helpers;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.trainer.value_objects.MultiStepResults;
import safe_rl.domain.trainer.value_objects.TrainerParameters;

/**
For learning directly from latest episode
 */

@AllArgsConstructor
public class MultiStepCriticUpdater<V> {

    @NonNull AgentACDiscoI<V> agent;
    @NonNull TrainerParameters parameters;

    public void update(MultiStepResults<V> msr)  {
        for (int step = 0; step < msr.nExperiences() ; step++) {
            agent.fitCritic(msr.stateAtStep(step),msr.valueTarAtStep(step)- agent.readCritic(msr.stateAtStep(step)));
        }
    }
}
