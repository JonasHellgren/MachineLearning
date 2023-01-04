package monte_carlo_tree_search.network_training;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class Experience<SSV, AV> {
    private static final int DEFAULT_REWARD = 0;
    private static final int DEFAULT_VALUE = 0;

    public SSV stateVariables;
    public AV action;
    public SSV stateVariableNew;
    @Builder.Default
    public double reward= DEFAULT_REWARD;
    @Builder.Default
    public double value= DEFAULT_VALUE;

}
