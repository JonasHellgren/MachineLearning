package policy_gradient_problems.domain.common_episode_trainers;

import com.google.common.base.Preconditions;
import common.ListUtils;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.helpers.ReturnCalculator;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

import java.util.List;

@Log
@AllArgsConstructor
public class NeuralActorCrossEntropyLossTrainer<V> {

    AgentNeuralActorI<V> agent;
    TrainerParameters parameters;
    int nofActions;

    public void trainFromEpisode(List<Experience<V>> experienceList) {
        var rc = new ReturnCalculator<V>();
        var elwr = rc.createExperienceListWithReturns(experienceList, parameters.gamma());
        var inList = elwr.stream().map(experience -> experience.state().asList()).toList();
        var outList = elwr.stream().map(this::createOut).toList();
        agent.fitActor(inList, outList);

    }

    private List<Double> createOut(Experience<V> experience) {
        int actionInt = experience.action().asInt();
        Preconditions.checkArgument(actionInt < nofActions, "Non valid action, actionInt =" + actionInt);
        List<Double> out = ListUtils.createListWithEqualElementValues(nofActions, 0d);
        out.set(actionInt, experience.value());
        return out;
    }

}
