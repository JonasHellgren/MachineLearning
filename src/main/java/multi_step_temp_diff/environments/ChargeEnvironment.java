package multi_step_temp_diff.environments;


import common.MathUtils;
import common.SetUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import multi_step_temp_diff.environment_helpers.PositionTransitionRules;
import multi_step_temp_diff.environment_helpers.SiteStateRules;
import multi_step_temp_diff.interfaces_and_abstract.EnvironmentInterface;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.models.StepReturn;
import org.apache.commons.math3.util.Pair;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/***
 *     0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
 *     19| 18| 17| 16| 15| 14| 13| 12| 11| 10|
 * 30| 29| 28| 27| 26| 25| 24| 23| 22| 21| 20|
 *   « = left,  » = right,  < = left or stay, > = right or stay
 *   v = down, ^ = up, t = trap (always stay)
 *    »| »| »| »| »| »| »| »| »| v |
 *    ^| <| <| «| «| «| «| «| «| <v|
 *  t|^| «| «| «| «| «| «| «| «| < |
 * <p>
 * There are two vehicles, A and B. If any of these initially are located in trap (pos 30). Only
 * one vehicle is active.
 * states={pos A, soC A, pos B, soC B,time}
 * action            0       1       2       3
 * commands(A,B)     [0,0]   [0,1]  [1,0]   [1,1]
 * Command is action for specific vehicle, for cells with option to stay (<,>) when command 0 is stay. For other cells
 * command does not influence transition.
 * <p>
 *  position transition model:
 *  pos    new pos (command=0)      new pos (command=1)
 *  -------------------------------------------------------
 *  10      11                      20
 *  19      0                       0
 *  18      18                      19 (18 if isObstacleAt19)
 *  20      20                      21
 *  29      29                      19 (29 if isObstacleAt19)
 *  30      30                      30
 *  else    pos+1                   pos+1
 * time transition model
 * time <- time+1
 * soC transition model:
 * delta soC = | -1/40  (pos not in CHARGE_NODES and move)
 *             |  1/20  (pos in CHARGE_NODES
 *             |  0     (pos not in CHARGE_NODES and not move)
 *  there move is true if new pos differs from pos
 *  CHARGE_NODES=(21,..,29)
 *  reward model:
 *  reward =  COST_QUE*nofQueuing + costCharge + socPenalty + collisionPenalty
 *  nofQueuing: vehicle in pos 10 that not are moving, i.e. queuing for charge
 *  costCharge = | COST_CHARGE  (any vehicle in CHARGE_NODES
 *               | 0
 *  socPenalty = | R_BAD  (isAnySoCBad)
 *               | 0     (else)
 *  collisionPenalty =  | R_BAD  (isTwoAtSamePosition)
 *                      | R_BAD  (isTwoCharging, i.e. two in CHARGE_NODES)
 *                      | 0     (else)
 * A state is terminal according to following logic
 * isTerminalState=isAnySoCBad or isTwoAtSamePosition or isTwoCharging or isTimeUp
 * isAnySoCBad is true if any soC is below 0.1, isTwoCharging ís true if both vehicles are at CHARGE_NODES
 * isTimeUp is true if time>TIME_MAX
 * The collisionPenalty gives for example that a vehicle at pos 20 needs to wait
 * if the other vehicle is in any of CHARGE_NODES. R_BAD can be -100.
 * Still cost shall be higher than charge cost, for example COST_QUE=-1, COST_CHARGE=-0.01
 *    | | | | | | | | | |           | | | | | | | | | |         | | | | | | | | | |         | | | | | | | | | |
 *    | | | | | | | | | |           | | | | | | | | | |         | | | | | | | | |A|         |O|B| | | | | |A| |
 *  |B| | | | |A| | | | |         | | | | |B| | | | |A|         | | | | | |B| | | |         | | | | | | | | | |

 B is trapped, A free          A needs to wait until         A can choose to charge      B is blocked by obstacle,
 B leaves CHARGE_NODES         or not to charge                                         gap between A-B decreases
 *
 */

@Setter
@Getter
public class ChargeEnvironment implements EnvironmentInterface<ChargeVariables> {

    public static final int POS_MIN = 0, POS_MAX = 30;
    public static final int NOF_ACTIONS = 4;
    public static final int SOC_MIN = 0, SOC_MAX = 1;
    public static final boolean IS_OBSTACLE = false;
    static final Map<Integer, Pair<Integer, Integer>> commandPairs =
            Map.of(0, new Pair<>(0, 0), 1, new Pair<>(0, 1), 2, new Pair<>(1, 0), 3, new Pair<>(1, 1));
    public static final double DELTA_SOC_MOVING_NOT_IN_CHARGEAREA = -1 / 40d, DELTA_SOC_STILL_NOT_IN_CHARGEAREA = 0;
    public static final double DELTA_SOC_IN_CHARGE_AREA = 1 / 20d;
    public static final int CHARGE_QUE_POS = 20;
    public static final double COST_QUE = 1, COST_CHARGE = 0.01d, R_BAD = -100;

    BiPredicate<Integer, Integer> isMoving = (p, pNew) -> !Objects.equals(p, pNew);
    Predicate<Integer> isAtChargeQuePos = (p) -> p == CHARGE_QUE_POS;

    public enum SiteState {
        isAnySoCBad, isTwoAtSamePos, isTwoCharging, isTimeUp, isAllFine
    }

    final PositionTransitionRules positionTransitionRules;
    final SiteStateRules siteStateRules;
    boolean isObstacle;

    public ChargeEnvironment() {
        positionTransitionRules = new PositionTransitionRules();
        siteStateRules = new SiteStateRules();
        isObstacle = IS_OBSTACLE;
    }

    @Override
    public StepReturn<ChargeVariables> step(StateInterface<ChargeVariables> state, int action) {

        throwIfBadArgument(state, action);
        Pair<Integer, Integer> posAposBNew = getNewPositions(state, action);
        Pair<Double, Double> socAsocB = getNewSoCLevels(state, posAposBNew);
        int timeNew = ChargeState.time.apply(state) + 1;
        SiteState siteState = siteStateRules.getSiteState(state);

        return StepReturn.<ChargeVariables>builder()
                .isNewStateTerminal(siteState != SiteState.isAllFine)
                .newState(new ChargeState(ChargeVariables.builder()
                        .posA(posAposBNew.getFirst()).posB(posAposBNew.getSecond())
                        .socA(socAsocB.getFirst()).socB(socAsocB.getSecond())
                        .time(timeNew)
                        .build()))
                .reward(getReward(state, posAposBNew, siteState))
                .build();
    }

    @Override
    public boolean isTerminalState(StateInterface<ChargeVariables> state) {
        return siteStateRules.getSiteState(state) != SiteState.isAllFine;
    }

    @Override
    public Set<Integer> actionSet() {
        return SetUtils.getSetFromRange(0, NOF_ACTIONS);
    }

    @SneakyThrows
    @Override
    public Set<StateInterface<ChargeVariables>> stateSet() {
        throw new NoSuchMethodException();
    }


    private void throwIfBadArgument(StateInterface<ChargeVariables> state, int action) {
        Predicate<Integer> isNonValidAction = (a) -> a > NOF_ACTIONS - 1;
        Predicate<Integer> isNonValidPos = (p) -> p < POS_MIN || p > POS_MAX;
        Predicate<Double> isNonValidSoC = (s) -> s < SOC_MIN || s > SOC_MAX;
        Predicate<Integer> isNonValidTime = (t) -> t < 0;
        if (isNonValidAction.test(action) ||
                isNonValidPos.test(ChargeState.posA.apply(state)) || isNonValidPos.test(ChargeState.posB.apply(state)) ||
                isNonValidSoC.test(ChargeState.socA.apply(state)) || isNonValidSoC.test(ChargeState.socB.apply(state)) ||
                isNonValidTime.test(ChargeState.time.apply(state))
        ) {
            throw new IllegalArgumentException("Non valid action or state. State = " + state + ", action = " + action);
        }
    }

    private Pair<Integer, Integer> getNewPositions(StateInterface<ChargeVariables> state, int action) {
        Pair<Integer, Integer> commandPair = commandPairs.get(action);
        int posANew = positionTransitionRules.getNewPos(
                ChargeState.posA.apply(state), isObstacle, commandPair.getFirst());
        int posBNew = positionTransitionRules.getNewPos(
                ChargeState.posB.apply(state), isObstacle, commandPair.getSecond());
        return Pair.create(posANew, posBNew);
    }

    private Pair<Double, Double> getNewSoCLevels(StateInterface<ChargeVariables> state,
                                                 Pair<Integer, Integer> posAposBNew) {
        double deltaSoCA = getDeltaSoC(ChargeState.posA.apply(state), posAposBNew.getFirst());
        double deltaSoCB = getDeltaSoC(ChargeState.posB.apply(state), posAposBNew.getSecond());
        return Pair.create(
                MathUtils.clip(ChargeState.socA.apply(state) + deltaSoCA,SOC_MIN,SOC_MAX),
                MathUtils.clip(ChargeState.socB.apply(state) + deltaSoCB,SOC_MIN,SOC_MAX));
    }

    private double getDeltaSoC(int pos, int posNew) {
        boolean isInCharge = SiteStateRules.isChargePos.test(pos);
        boolean isMoving = pos != posNew;
        if (isInCharge) {
            return DELTA_SOC_IN_CHARGE_AREA;
        } else {
            return isMoving ? DELTA_SOC_MOVING_NOT_IN_CHARGEAREA : DELTA_SOC_STILL_NOT_IN_CHARGEAREA;
        }
    }

    private double getReward(StateInterface<ChargeVariables> state,
                             Pair<Integer, Integer> posAposBNew,
                             SiteState siteState) {

        BiPredicate<Integer, Integer> isStillAtChargeQuePos = (pos, posNew) ->
                isAtChargeQuePos.test(pos) && !isMoving.test(pos, posNew);
        Integer posA = ChargeState.posA.apply(state), posB = ChargeState.posB.apply(state);
        boolean isAInChargeQue = isStillAtChargeQuePos.test(posA, posAposBNew.getFirst());
        boolean isBInChargeQue = isStillAtChargeQuePos.test(posB, posAposBNew.getSecond());
        boolean isAInChargeArea = SiteStateRules.isChargePos.test(posA);
        boolean isBInChargeArea = SiteStateRules.isChargePos.test(posB);

        BiFunction<Boolean, Boolean, Double> costQue = (isA, isB) -> (isA || isB) ? -COST_QUE : 0d;
        BiFunction<Boolean, Boolean, Double> costCharge = (isA, isB) -> (isA || isB) ? -COST_CHARGE : 0d;
        Function<SiteState, Double> failPenalty = (ss) -> isAnyConstraintFailed(ss) ? R_BAD : 0;

        return costQue.apply(isAInChargeQue, isBInChargeQue) +
                costCharge.apply(isAInChargeArea, isBInChargeArea) +
                failPenalty.apply(siteState);
    }

    private static boolean isAnyConstraintFailed(SiteState ss) {
        return ss.equals(SiteState.isAnySoCBad) || ss.equals(SiteState.isTwoAtSamePos) || ss.equals(SiteState.isTwoCharging);
    }

}
