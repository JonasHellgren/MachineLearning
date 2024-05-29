package multi_agent_rl.environments.apple;

import common.math.Discrete2DPos;

public record VariablesApple(
        Discrete2DPos posA,
        Discrete2DPos posB,
        Discrete2DPos posApple
        )

{
}
