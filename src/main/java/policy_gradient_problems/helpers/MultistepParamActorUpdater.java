package policy_gradient_problems.helpers;

import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import java.util.List;
import java.util.function.*;

/**
 * pseudocode in md file pseudocode_pgrl.md

 * The function criticOut etc is used to make the class generic
 */

@Builder
public class MultistepParamActorUpdater<V> {

    @NonNull TrainerParameters parameters;
    @NonNull Function<StateI<V>,Double> criticOut;
    @NonNull BiFunction<StateI<V>, Action, ArrayRealVector> calcGradLogVector;
    @NonNull Consumer<RealVector> changeActor;

    public void updateActor(List<Experience<V>> experiences) {
        var nri = new MultiStepReturnEvaluator<>(experiences, parameters);
        var ac=new AdvantageCalculator<>(parameters, criticOut);
        int T = experiences.size();
        for (int tau = 0; tau < T; tau++) {
            var expAtTau = nri.getExperience(tau);
            double advantage = ac.calcAdvantage(expAtTau);
            var gradLogVector = calcGradLogVector.apply(expAtTau.state(), expAtTau.action());
            var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRateNonNeuralActor() * advantage);
            changeActor.accept(changeInThetaVector);
        }
    }

}
