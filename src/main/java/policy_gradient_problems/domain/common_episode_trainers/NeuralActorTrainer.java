package policy_gradient_problems.domain.common_episode_trainers;

import lombok.Builder;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.helpers.ReturnCalculator;
import java.util.List;
import java.util.function.BiFunction;

/***
 * The labelFunction defines what shall go into the loss function via label argument
 */

@Builder
public class NeuralActorTrainer<V> {

    AgentNeuralActorI<V> agent;
    TrainerParameters parameters;
    BiFunction<Experience<V>,AgentNeuralActorI<V>,List<Double>> labelFunction;

    public void trainFromEpisode(List<Experience<V>> experienceList) {
        var rc = new ReturnCalculator<V>();
        var elwr = rc.createExperienceListWithReturns(experienceList, parameters.gamma());
        var inList = elwr.stream().map(experience -> experience.state().asList()).toList();
        var labelList = elwr.stream().map(e -> labelFunction.apply(e,agent)).toList();
        agent.fitActor(inList,labelList);
    }

}
