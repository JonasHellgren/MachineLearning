package multi_agent_rl.environments.apple;

import common.math.Discrete2DPos;
import lombok.Builder;

@Builder
public record AppleSettings(
        Discrete2DPos minPos,
        Discrete2DPos maxPos
        ) {

        public static AppleSettings newDefault() {
                return  AppleSettings.builder()
                        .minPos(Discrete2DPos.of(0, 0))
                        .maxPos(Discrete2DPos.of(4, 4))
                        .build();
        }

}
