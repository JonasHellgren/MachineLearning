package policy_gradient_problems.domain.common_episode_trainers;

import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.helpers.ExperienceHelper;

import java.util.ArrayList;
import java.util.List;

@Builder
public class ActorCriticPPOTrainer<V> {
    @NonNull AgentNeuralActorNeuralCriticI<V> agent;
    @NonNull TrainerParameters parameters;
    @NonNull Double valueTermState;
    @NonNull Integer nofActions;

    public void trainAgentFromExperiences(List<Experience<V>> experienceList) {
        List<List<Double>> inList = new ArrayList<>();
        List<Double> outList = new ArrayList<>();
        List<List<Double>> ppoLabelList = new ArrayList<>();

        var helper= ExperienceHelper.<V>builder()
                .valueTermState(valueTermState).nofActions(nofActions).criticOut((s) -> agent.criticOut(s)).build();
        for (Experience<V> experience : experienceList) {
            var stateAsList = experience.state().asList();
            double vNext = helper.valNext(experience);
            double returnAtTime = experience.reward() + parameters.gamma() * vNext;
            inList.add(stateAsList);
            outList.add(returnAtTime);
            double v=agent.criticOut(experience.state());
            double adv = returnAtTime - v;
            int action = experience.action().intValue().orElseThrow();
            double probOld=agent.actorOut(experience.state()).get(action);
            ppoLabelList.add(List.of((double) action,adv,probOld));
        }

        agent.fitCritic(inList, outList);
        agent.fitActor(inList, ppoLabelList);
    }
}
