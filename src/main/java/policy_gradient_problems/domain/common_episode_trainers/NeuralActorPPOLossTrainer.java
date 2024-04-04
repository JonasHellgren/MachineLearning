package policy_gradient_problems.domain.common_episode_trainers;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorII;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.helpers.ReturnCalculator;
import java.util.List;

@AllArgsConstructor
public class NeuralActorPPOLossTrainer<V> {

    AgentNeuralActorII<V> agent;
    TrainerParameters parameters;
    int nofActions;

    public void trainFromEpisode(List<Experience<V>> experienceList) {
        var rc = new ReturnCalculator<V>();
        var elwr = rc.createExperienceListWithReturns(experienceList, parameters.gamma());
        var inList = elwr.stream().map(experience -> experience.state().asList()).toList();
        var outList = elwr.stream().map(this::createOut).toList();
        agent.fitActor(inList,outList);

    }

    private List<Double> createOut(Experience<V> experience) {
        int actionInt = experience.action().asInt();
        Preconditions.checkArgument(nofActions>=0 && actionInt < nofActions,"Non valid action, actionInt =" + actionInt);
        double probOld=agent.getActionProbabilities().get(actionInt);
        double adv=experience.value();
        return List.of((double) actionInt,adv,probOld);
    }

}
