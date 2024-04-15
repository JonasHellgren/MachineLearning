package policy_gradient_problems.domain.common_episode_trainers;

import common.math.MathUtils;
import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.helpers.ExperienceHelper;

import java.util.ArrayList;
import java.util.List;

@Builder
public class ActorCriticEpisodeTrainerPPOCont<V> implements EpisodeTrainerI<V> {
    @NonNull AgentNeuralActorNeuralCriticI<V> agent;
    @NonNull TrainerParameters parameters;
    @NonNull Double valueTermState;

    @Override
    public void trainAgentFromExperiences(List<Experience<V>> experienceList) {
        List<List<Double>> inList = new ArrayList<>();
        List<Double> outList = new ArrayList<>();
        List<List<Double>> ppoLabelList = new ArrayList<>();

        var helper= ExperienceHelper.<V>builder()
                .valueTermState(valueTermState).criticOut((s) -> agent.criticOut(s)).build();

        for (Experience<V> experience : experienceList) {
            var stateAsList = experience.state().asList();
            double vNext = helper.valNext(experience);
            double returnAtTime = experience.reward() + parameters.gamma() * vNext;
            inList.add(stateAsList);
            outList.add(returnAtTime);
            double v=agent.criticOut(experience.state());
            double adv = returnAtTime - v;
            double action = experience.action().doubleValue().orElseThrow();
            double pdfOld=MathUtils.pdf(action, agent.meanAndStd(experience.state()));
            ppoLabelList.add(List.of(action,adv,pdfOld));
        }

     //   System.out.println("agent.getState() = " + agent.getState());
     //   ppoLabelList.forEach(System.out::println);

        agent.fitCritic(inList, outList);
        agent.fitActor(inList, ppoLabelList);
    }
}
