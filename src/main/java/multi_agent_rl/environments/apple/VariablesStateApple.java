package multi_agent_rl.environments.apple;

import common.math.Discrete2DPos;
import lombok.Builder;

@Builder
public record VariablesStateApple(
        Discrete2DPos posA,
        Discrete2DPos posB,
        Discrete2DPos posApple
) {

    public VariablesStateApple copy() {
        return VariablesStateApple.builder()
                .posA(posA.copy())
                .posB(posB.copy())
                .posApple(posApple.copy())
                .build();
    }

    public boolean isInBounds(AppleSettings settings) {
        return isPosInBounds(posA, settings) &&
                isPosInBounds(posB, settings) &&
                isPosInBounds(posApple, settings);
    }

    public static boolean isPosInBounds(Discrete2DPos posA, AppleSettings settings) {
        return posA.x() >= settings.minPos().x() &&
                posA.y() >= settings.minPos().y() &&
                posA.x() <= settings.maxPos().x() &&
                posA.y() <= settings.maxPos().y();
    }

}
