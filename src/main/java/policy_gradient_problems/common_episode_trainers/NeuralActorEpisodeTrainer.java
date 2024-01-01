package policy_gradient_problems.common_episode_trainers;

import common.ListUtils;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import policy_gradient_problems.agent_interfaces.AgentNeuralActorI;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_helpers.ReturnCalculator;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import java.util.List;

@Log
@AllArgsConstructor
public class NeuralActorEpisodeTrainer<V> {

    AgentNeuralActorI<V> agent;
    TrainerParameters parameters;
    int nofActions;

    public void trainFromEpisode(List<Experience<V>> experienceList) {
        var rc = new ReturnCalculator<V>();
        var elwr = rc.createExperienceListWithReturns(experienceList, parameters.gamma());
        for (Experience<V> experience : elwr) {
            agent.fitActor(agent.getState().asList(), createOut(experience));
        }
    }

    private List<Double> createOut(Experience<V> experience) {
        int actionInt = experience.action().asInt();
        throwIfNonValidAction(actionInt);
        List<Double> out=ListUtils.createListWithEqualElementValues(nofActions,0d);
        out.set(actionInt, experience.value());
        return out;
    }

    private void throwIfNonValidAction(int actionInt) {
        if (actionInt >=nofActions) {
            throw new IllegalArgumentException("Non valid action, actionInt = "+ actionInt);
        }
    }
}
