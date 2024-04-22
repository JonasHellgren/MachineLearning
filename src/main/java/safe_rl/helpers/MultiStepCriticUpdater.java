package safe_rl.helpers;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.value_classes.MultiStepResults;
import safe_rl.domain.value_classes.TrainerParameters;

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
