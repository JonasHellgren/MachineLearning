package multi_agent_rl.environments.apple;

import common.math.Discrete2DPos;
import lombok.Builder;

@Builder
public record AppleSettings(
        Discrete2DPos minPos,
        Discrete2DPos maxPos
        ) {
}
