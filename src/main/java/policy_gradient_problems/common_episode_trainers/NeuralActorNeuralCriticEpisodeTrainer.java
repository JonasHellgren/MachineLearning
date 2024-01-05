package policy_gradient_problems.common_episode_trainers;

import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.agent_interfaces.AgentNeuralActorNeuralCriticI;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_helpers.ExperienceHelper;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import java.util.ArrayList;
import java.util.List;

@Builder
public class NeuralActorNeuralCriticEpisodeTrainer<V> {
    @NonNull AgentNeuralActorNeuralCriticI<V> agent;
    @NonNull TrainerParameters parameters;
    @NonNull Double valueTermState;
    @NonNull Integer nofActions;

    public void trainAgentFromExperiences(List<Experience<V>> experienceList) {
        List<List<Double>> inList = new ArrayList<>();
        List<Double> outList = new ArrayList<>();
        var helper= ExperienceHelper.<V>builder()
                .valueTermState(valueTermState).nofActions(nofActions).criticOut((s) -> agent.getCriticOut(s)).build();
        for (Experience<V> experience : experienceList) {
            var stateAsList = experience.state().asList();
            double vNext = helper.valNext(experience);
            double Gt = experience.reward() + parameters.gamma() * vNext;
            inList.add(stateAsList);
            outList.add(Gt);
            double v=agent.getCriticOut(experience.state());
            double adv = Gt - v;
            agent.fitActor(stateAsList, helper.createOneHot(experience,adv));
        }
        int nofFits = parameters.nofFits(experienceList.size());
        agent.fitCritic(inList, outList, nofFits);
    }

}
