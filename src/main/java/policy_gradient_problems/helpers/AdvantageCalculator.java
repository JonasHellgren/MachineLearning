package policy_gradient_problems.helpers;

import lombok.AllArgsConstructor;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import java.util.function.Function;

/**
 * advantage=Q(s,a)-V(s)=r+γ*V(s')-V(s')
 * If an action leads to a fail or terminal stateNew, the advantage calculation focus on the immediate reward,
 * value of future stateNew can be regarded as not possible to define/irrelevant due to the fail stateNew.
 */

@AllArgsConstructor
public class AdvantageCalculator<V> {

    TrainerParameters parameters;
    Function<StateI<V>,Double> criticOut;

    public double calcAdvantage(Experience<V> expAtTau) {
        double r = expAtTau.reward();
        double valueS = criticOut.apply(expAtTau.state());
        double valueSNew = criticOut.apply(expAtTau.stateNext());
        boolean isActionResultingInFailOrTerminalState = expAtTau.isFail() || expAtTau.isTerminal();
        return (isActionResultingInFailOrTerminalState) ? r : r + parameters.gamma() * valueSNew - valueS;
    }

}
