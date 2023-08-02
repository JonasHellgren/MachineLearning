package multi_step_temp_diff.domain.environment_valueobj;

import common.MySetUtils;
import lombok.Builder;
import multi_step_temp_diff.domain.environments.charge.Commands;
import org.apache.commons.collections4.SetUtils;
import java.util.Map;
import java.util.Set;

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
        Set<Integer> allNodes, Set<Integer> siteNodes, Set<Integer> chargeNodes,
        double costQue, double costCharge, double  rewardBad
) {


    public static final int POS_MIN = 0, POS_MAX = 59;

    public static ChargeEnvironmentSettings newDefault() {
        return ChargeEnvironmentSettings.builder()
                .posMin(POS_MIN).posMax(POS_MAX)
                .nofActions(4)
                .socMin(0).socMax(1)
                .isObstacleStart(false)
                .commandMap(Map.of(0,Commands.of(0, 0), 1,Commands.of(0, 1), 2,Commands.of(1, 0), 3,Commands.of(1, 1)))
                .deltaSocMovingNotInChargeArea(-1/40d)
                .deltaSocStillNotInChargeArea(0)
                .deltaSocInChargeArea(1/10d)
                .chargeQuePos(20)
                .allNodes(MySetUtils.getSetFromRange(POS_MIN, POS_MAX))
                .siteNodes(SetUtils.union(MySetUtils.getSetFromRangeInclusive(POS_MIN,20),Set.of(30,40,50,51,52,42,32,22)))
                .chargeNodes(Set.of(30,40,50,51,52,42,32,22))
                .costQue(1).costCharge(0.01).rewardBad(-100)
                .build();
    }

    public boolean isAtTrapNode(int pos) {
        Set<Integer> trapNodes=SetUtils.difference(allNodes,siteNodes);
        return trapNodes.contains(pos);
    }

}
