package multi_step_temp_diff.domain.environment_valueobj;

import lombok.Builder;
import multi_step_temp_diff.domain.environments.charge.Commands;
import org.apache.commons.math3.util.Pair;

import java.util.Map;

@Builder
public record ChargeEnvironmentSettings(
        int posMin, int posMax,
        int nofActions,
        int socMin, int socMax,
        boolean isObstacleStart,
        Map<Integer, Commands> commandMap,
        double deltaSocMovingNotInChargeArea,
        double deltaSocStillNotInChargeArea,
        double deltaSocInChargeArea,
        int chargeQuePos,
        double costQue, double costCharge, double  rewardBad
) {


    public static ChargeEnvironmentSettings newDefault() {
        return ChargeEnvironmentSettings.builder()
                .posMin(0).posMax(59)
                .nofActions(4)
                .socMin(0).socMax(1)
                .isObstacleStart(false)
                .commandMap(Map.of(0,Commands.of(0, 0), 1,Commands.of(0, 1), 2,Commands.of(1, 0), 3,Commands.of(1, 1)))
                .deltaSocMovingNotInChargeArea(-1/40d)
                .deltaSocStillNotInChargeArea(0)
                .deltaSocInChargeArea(1/10d)
                .chargeQuePos(20)
                .costQue(1).costCharge(0.01).rewardBad(-100)
                .build();
    }

}
