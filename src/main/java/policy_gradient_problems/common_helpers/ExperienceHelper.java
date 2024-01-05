package policy_gradient_problems.common_helpers;

import common.ListUtils;
import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.common_generic.Experience;

import java.util.List;
import java.util.function.Function;

@Builder
public class ExperienceHelper<V> {

    @NonNull Double valueTermState;
    @NonNull Integer nofActions;
    @NonNull Function<StateI<V>, Double> criticOut;

    public List<Double> createOneHot(Experience<V> experience, double adv) {
        int actionInt = experience.action().asInt();
        throwIfNonValidAction(actionInt);
        List<Double> out = ListUtils.createListWithEqualElementValues(nofActions, 0d);
        out.set(actionInt, adv);
        return out;
    }

    private void throwIfNonValidAction(int actionInt) {
        if (actionInt >= nofActions) {
            throw new IllegalArgumentException("Non valid action, actionInt = " + actionInt);
        }
    }

    public double valNext(Experience<V> experience) {
        return experience.isTerminal()
                ? valueTermState
                : criticOut.apply(experience.stateNext());
    }


}
