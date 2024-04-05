package policy_gradient_problems.domain.common_episode_trainers;

import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.helpers.ExperienceHelper;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import java.util.ArrayList;
import java.util.List;

@Builder
public class NeuralActorNeuralCriticCEMTrainer<V> {
    @NonNull AgentNeuralActorNeuralCriticI<V> agent;
    @NonNull TrainerParameters parameters;
    @NonNull Double valueTermState;
    @NonNull Integer nofActions;

    public void trainAgentFromExperiences(List<Experience<V>> experienceList) {
        List<List<Double>> inList = new ArrayList<>();
        List<Double> outList = new ArrayList<>();
        List<List<Double>> oneHotList = new ArrayList<>();

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
            oneHotList.add(helper.createOneHot(experience, adv));
        }

        agent.fitCritic(inList, outList);
        agent.fitActor(inList, oneHotList);
    }

}
