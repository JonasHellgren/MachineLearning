package policy_gradient_problems.common_helpers;

import lombok.AllArgsConstructor;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import java.util.function.Function;

/**
 * advantage=Q(s,a)-V(s)=r+γ*V(s')-V(s')
 * If an action leads to a fail state, the advantage calculation focus on the immediate reward,
 * value of future state can be regarded as not possible to define/irrelevant due to the fail state.
 */

@AllArgsConstructor
public class AdvantageCalculator<V> {

    TrainerParameters parameters;
    Function<StateI<V>,Double> criticOut;

    public double calcAdvantage(Experience<V> expAtTau) {
        double r = expAtTau.reward();
        double valueS = criticOut.apply(expAtTau.state());
        double valueSNew = criticOut.apply(expAtTau.stateNext());
        boolean isActionResultingInFailState = expAtTau.isFail();
        return (isActionResultingInFailState) ? r : r + parameters.gamma() * valueSNew - valueS;
    }

}
