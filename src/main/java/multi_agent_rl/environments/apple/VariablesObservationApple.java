package multi_agent_rl.environments.apple;

import common.math.Discrete2DVector;

public record VariablesObservationApple(
        Discrete2DVector distOtherAgent,
        Discrete2DVector distApple
) {

    public VariablesObservationApple copy() {
        return new VariablesObservationApple(distOtherAgent,distApple);
    }

}
