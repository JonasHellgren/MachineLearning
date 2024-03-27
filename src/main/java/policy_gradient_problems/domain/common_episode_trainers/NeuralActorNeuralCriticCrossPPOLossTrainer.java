package policy_gradient_problems.domain.common_episode_trainers;

import lombok.NonNull;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.helpers.ExperienceHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NeuralActorNeuralCriticCrossPPOLossTrainer<V> {
    @NonNull AgentNeuralActorNeuralCriticI<V> agent;
    @NonNull TrainerParameters parameters;
    @NonNull Double valueTermState;
    @NonNull Integer nofActions;

    public void trainAgentFromExperiences(List<Experience<V>> experienceList) {
        List<List<Double>> inList = new ArrayList<>();
        List<Double> outList = new ArrayList<>();
        List<List<Double>> ppoLabelList = new ArrayList<>();

        var helper= ExperienceHelper.<V>builder()
                .valueTermState(valueTermState).nofActions(nofActions).criticOut((s) -> agent.getCriticOut(s)).build();
        for (Experience<V> experience : experienceList) {
            var stateAsList = experience.state().asList();
            double vNext = helper.valNext(experience);
            double returnAtTime = experience.reward() + parameters.gamma() * vNext;
            inList.add(stateAsList);
            outList.add(returnAtTime);
            double v=agent.getCriticOut(experience.state());
            double adv = returnAtTime - v;
            //ppoLabelList.add(helper.createOneHot(experience, adv));
            int action = experience.action().intValue().orElseThrow();
            double probOld=agent.getActionProbabilities().get(action);
            ppoLabelList.add(List.of((double) action,adv,probOld));

        }

        agent.fitCritic(inList, outList);
        agent.fitActor(inList, ppoLabelList);
    }
}
