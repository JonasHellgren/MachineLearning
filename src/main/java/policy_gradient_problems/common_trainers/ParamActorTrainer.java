package policy_gradient_problems.common_trainers;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.math3.linear.RealVector;
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
            var gradLogVector = agent.calcGradLogVector(experience.action().asInt());
            double vt = experience.value();
            var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRateActor() * vt);
            logging(experience, changeInThetaVector);
            agent.changeActor(changeInThetaVector);
        }
    }

    private void logging(Experience<V> experience, RealVector changeInThetaVector) {
        log.fine("experience = " + experience +
                ", changeInThetaVector = " + changeInThetaVector);
    }


}
