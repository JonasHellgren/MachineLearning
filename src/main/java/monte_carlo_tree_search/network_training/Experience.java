package monte_carlo_tree_search.network_training;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class Experience<SSV, AV> {

    public SSV stateVariables;
    public AV action;
    public SSV stateVariableNew;
    public double reward;
    public double value;

}
