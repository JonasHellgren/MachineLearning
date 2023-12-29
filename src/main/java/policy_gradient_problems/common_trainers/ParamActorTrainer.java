package policy_gradient_problems.common_trainers;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import policy_gradient_problems.abstract_classes.AgentParamActorI;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_generic.ReturnCalculator;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import java.util.List;

@Log
@AllArgsConstructor
public class ParamActorTrainer<V> {

    AgentParamActorI<V> agent;
    TrainerParameters parameters;

    public void trainFromEpisode(List<Experience<V>> experienceList) {
        var rc = new ReturnCalculator<V>();
        var elwr = rc.createExperienceListWithReturns(experienceList, parameters.gamma());
        for (Experience<V> experience : elwr) {
            var gradLogVector = agent.calcGradLogVector(experience.state(),experience.action());
            double vt = experience.value();
            var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRateActor() * vt);
            agent.changeActor(changeInThetaVector);
        }
    }
}
