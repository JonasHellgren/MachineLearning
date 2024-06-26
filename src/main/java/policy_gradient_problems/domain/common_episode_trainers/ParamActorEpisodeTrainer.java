package policy_gradient_problems.domain.common_episode_trainers;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.agent_interfaces.AgentParamActorI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.helpers.ReturnCalculator;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import java.util.List;

@Log
@AllArgsConstructor
public class ParamActorEpisodeTrainer<V> {

    AgentParamActorI<V> agent;
    TrainerParameters parameters;

    public void trainFromEpisode(List<Experience<V>> experienceList) {
        var rc = new ReturnCalculator<V>();
        var elwr = rc.createExperienceListWithReturns(experienceList, parameters.gamma());
        for (Experience<V> experience : elwr) {
            var gradLogVector = agent.calcGradLogVector(experience.state(),experience.action());
            double vt = experience.value();
            var changeParam = gradLogVector.mapMultiplyToSelf(parameters.learningRateNonNeuralActor() * vt);
            agent.changeActor(changeParam);
        }
    }
}
