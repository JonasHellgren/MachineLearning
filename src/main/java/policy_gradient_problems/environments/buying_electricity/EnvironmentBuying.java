package policy_gradient_problems.environments.buying_electricity;

import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.EnvironmentI;
import policy_gradient_problems.domain.abstract_classes.StateI;
import common.reinforcment_learning.value_classes.StepReturn;

public class EnvironmentBuying  implements EnvironmentI<VariablesBuying> {


    @Override
    public StepReturn<VariablesBuying> step(StateI<VariablesBuying> state, Action action) {
        return null;
    }
}
