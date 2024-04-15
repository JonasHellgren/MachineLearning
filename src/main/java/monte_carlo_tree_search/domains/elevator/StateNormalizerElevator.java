package monte_carlo_tree_search.domains.elevator;

import common.math.ScalerLinear;

public class StateNormalizerElevator<SSV> {

private static final int RANGE_MIN = -1;
private static final int RANGE_MAX = 1;

    ScalerLinear soEScaler;

    public StateNormalizerElevator() {
        this.soEScaler = new ScalerLinear(0,1,RANGE_MIN,RANGE_MAX);
    }

    public VariablesElevator normalize(SSV variables) {

        VariablesElevator v=(VariablesElevator) variables;
        return VariablesElevator.builder()
                .SoE(soEScaler.calcOutDouble(v.SoE))
                .build();
    }

}


