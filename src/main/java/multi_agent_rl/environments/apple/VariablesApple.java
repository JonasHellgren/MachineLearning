package multi_agent_rl.environments.apple;

import common.math.Discrete2DPos;
import lombok.Builder;

@Builder
public record VariablesApple(
        Discrete2DPos posA,
        Discrete2DPos posB,
        Discrete2DPos posApple
        )
{

        public VariablesApple copy() {
                return VariablesApple.builder()
                        .posA(posA.copy())
                        .posB(posB.copy())
                        .posApple(posApple.copy())
                        .build();
        }

}
