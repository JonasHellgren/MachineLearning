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
        int nofActions, int nofVehicles,
        double socMin, double socMax, double socBad,
        boolean isObstacleStart,
        Map<Integer, Commands> commandMap,
        double deltaSocMovingNotInChargeArea,
        double deltaSocStillNotInChargeArea,
        double deltaSocInChargeArea,
        int chargeQuePos, int chargeDecisionPos,
        Set<Integer> allNodes, Set<Integer> siteNodes, Set<Integer> chargeNodes,
        double costQue, double costCharge, double  rewardBad,
        int maxNofSteps
) {


    public static final int POS_MIN = 0, POS_MAX = 59;

    public static ChargeEnvironmentSettings newDefault() {
        return ChargeEnvironmentSettings.builder()
                .posMin(POS_MIN).posMax(POS_MAX)
                .nofActions(4).nofVehicles(2)
                .socMin(0).socMax(1).socBad(0.2)
                .isObstacleStart(false)
                .commandMap(Map.of(0,Commands.of(0, 0), 1,Commands.of(0, 1), 2,Commands.of(1, 0), 3,Commands.of(1, 1)))
                .deltaSocMovingNotInChargeArea(-1/60d)
                .deltaSocStillNotInChargeArea(-1/200d)
                .deltaSocInChargeArea(1/10d)
                .chargeQuePos(20).chargeDecisionPos(10)
                .allNodes(MySetUtils.getSetFromRange(POS_MIN, POS_MAX))
                .siteNodes(SetUtils.union(MySetUtils.getSetFromRangeInclusive(POS_MIN,20),Set.of(30,40,50,51,52,42,32,22)))
                .chargeNodes(Set.of(30,40,50,51,52,42,32))
                .costQue(1).costCharge(0.01).rewardBad(-10)
                .maxNofSteps(1000)
                .build();
    }

    public boolean isAtTrapNode(int pos) {
        Set<Integer> trapNodes=SetUtils.difference(allNodes,siteNodes);
        return trapNodes.contains(pos);
    }

}
