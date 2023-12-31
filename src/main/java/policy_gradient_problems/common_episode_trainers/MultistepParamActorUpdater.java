package policy_gradient_problems.common_episode_trainers;

import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_helpers.AdvantageCalculator;
import policy_gradient_problems.common_helpers.NStepReturnInfo;
import policy_gradient_problems.common_value_classes.TrainerParameters;
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
        var nri = new NStepReturnInfo<>(experiences, parameters);
        var ac=new AdvantageCalculator<>(parameters, criticOut);
        int T = experiences.size();
        for (int tau = 0; tau < T; tau++) {
            var expAtTau = nri.getExperience(tau);
            double advantage = ac.calcAdvantage(expAtTau);
            var gradLogVector = calcGradLogVector.apply(expAtTau.state(), expAtTau.action());
            var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRateActor() * advantage);
            changeActor.accept(changeInThetaVector);
        }
    }

}