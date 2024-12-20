package monte_carlo_tree_search.network_training;

import lombok.Builder;
import lombok.ToString;

/**
 *  An experience is achieved when taking step with specific action in stateNew s and transiting to sNew. The step
 *  gives a reward and the stateNew s has value value.
 */

@Builder
@ToString
public class Experience<S, A> {
    private static final int DEFAULT_REWARD = 0;
    private static final int DEFAULT_VALUE = 0;

    public S stateVariables;
    public A action;
    public S stateVariableNew;
    @Builder.Default
    public double reward= DEFAULT_REWARD;
    @Builder.Default
    public double value= DEFAULT_VALUE;

}
