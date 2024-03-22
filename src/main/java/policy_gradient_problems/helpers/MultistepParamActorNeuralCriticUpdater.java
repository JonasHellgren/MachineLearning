package policy_gradient_problems.helpers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.domain.agent_interfaces.AgentParamActorNeuralCriticI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

import java.util.List;

/**
 * pseudocode in md file pseudocode_pgrl.md

 * The function criticOut etc is used to make the class generic
 */

@Builder
@AllArgsConstructor
public class MultistepParamActorNeuralCriticUpdater<V> {

    @NonNull TrainerParameters parameters;
    AgentParamActorNeuralCriticI<V> agent;

    public void updateActor(List<Experience<V>> experiences) {
        var nri = new MultiStepReturnEvaluator<>(parameters,experiences);
        var ac=new AdvantageCalculator<V>(parameters, s -> agent.getCriticOut(s));
        int nofExp = experiences.size();
        for (int tau = 0; tau < nofExp; tau++) {
            var expAtTau = nri.getExperience(tau);
            double advantage = ac.calcAdvantage(expAtTau);
            var gradLogVector = agent.calcGradLogVector(expAtTau.state(), expAtTau.action());
            var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRateNonNeuralActor() * advantage);
            agent.changeActor(changeInThetaVector);
        }
    }

}
