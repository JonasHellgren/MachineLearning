package multi_step_temp_diff.domain.environments.charge;


import common.MathUtils;
import common.MySetUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import static multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas.*;
import static multi_step_temp_diff.domain.environments.charge.ChargeState.*;
import static multi_step_temp_diff.domain.environments.charge.SiteState.*;

/***
 *     >> = always right, .> = stay or right, << = always left, .< = stay or left
 *     vv = always down, .v = stay or down, ^^ = always up, .^ = stay or up
 *     no marker = always stay
 *
 *     0 >>|  1>>| 2 >>| 3 >>| 4 >>| 5 >>| 6 >>| 7 .>| 8 >>|  9vv|
 *     19^^| 18<<| 17<<| 16<<| 15<<| 14<<| 13<<| 12<<| 11<<| 10<v|
 *     29  | 28  | 27  | 26  | 25  | 24  | 23  | 22.^| 21  | 20.v|
 *     39  | 38  | 37  | 36  | 35  | 34  | 33  | 32^^| 31  | 30vv|
 *     49  | 48  | 47  | 46  | 45  | 44  | 43  | 42^^| 41  | 40vv|
 *     59  | 58  | 57  | 56  | 55  | 54  | 53  | 52^^| 51<<| 50<<|
 *
 *     , where
 *     NOD_WHERE_OBSTACLE_CAN_BE=8
 *     CHARGE_NODES=(30,40,50,51,52,42,32), TRAP_NODES={21,23-29,31,33-39,....},
 *
 * <p>
 * There are two vehicles, A and B. If any of these initially are located in a trap (for ex posReal 29). Only
 * one vehicle is active.
 * states={posReal A, soC A, posReal B, soC B,time}
 * action            0       1       2       3
 * commands(A,B)     [0,0]   [0,1]  [1,0]   [1,1]
 * Command is action for specific vehicle, for cells with option to stay (with .) when command 0 is stay. For other cells
 * command does not influence transition.
 * <p>
 *  position transition model:
 *  posReal             new posReal (command=0)      new posReal (command=1)    comment
 *  ------------------------------------------------------------------------
 *  7               7                           8
 10              11                          20
 20              20                          30
 22              22                          12
 30              40                          40
 40              50                          50
 51              52                          52
 52              42                          42
 42              32                          32
 32              22                          22
 19              0                           0
 trap           posReal                          posReal
 *  else        posReal+1                        posReal+1
 *
 * time transition model
 * time <- time+1
 * soC transition model:
 * delta soC = | -1/60  (posReal not in CHARGE_NODES and move)
 *             |  1/200 (posReal not in CHARGE_NODES and not move)
 *             |  1/10  (posReal in CHARGE_NODES)
 *             |  0     (posReal not in CHARGE_NODES and not move)
 *  there move is true if new posReal differs from posReal
 *  reward model:
 *  reward =  COST_QUE*isAQueing+ COST_QUE*isBqueing + costCharge + socPenalty + collisionPenalty
 *  nofQueuing: vehicle in posReal 20 that not are moving, i.e. queuing for charge
 *  costCharge = | COST_CHARGE  (any vehicle in CHARGE_NODES)
 *               | 0
 *  socPenalty = | R_BAD  (isAnySoCBad)
 *               | 0     (else)
 *  collisionPenalty =  | R_BAD  (isTwoAtSamePosition)
 *                      | R_BAD  (isTwoCharging, i.e. two in CHARGE_NODES)
 *                      | 0     (else)
 * A state is terminal according to following logic
 * isTerminalState=isAnySoCBad or isTwoAtSamePosition or isTwoCharging or isTimeUp
 * Terms in isTerminalState consider the state AFTER transition. So action leading to for example collision can be
 * excluded directly
 * isAnySoCBad is true if any soC is below 0.1, isTwoCharging ís true if both vehicles are at CHARGE_NODES
 * isTimeUp is true if time>TIME_MAX
 * The collisionPenalty gives for example that a vehicle at posReal 20 needs to wait
 * if the other vehicle is in any of CHARGE_NODES. R_BAD can be -100.
 * Still cost shall be higher than charge cost, for example COST_QUE=-1, COST_CHARGE=-0.01
 *    | | | | |A| | | | |           | | | | | | | | | |         | | | | | | | | |B|         | |A| | | | |B|O | |
 *    | | | | | | | | | |           | | | | | | | | | |         | | | | | | | | |A|         | | | | | | | | | |
 *    |B| | | | | | | | |             | | | | | | | |A|         | | | | | | | | | |         | | | | | | | | | |
 |B|

 B is trapped, A free          A needs to wait until         A can choose to charge      B is blocked by obstacle,
 B ends charging               or not to charge            gap between A-B decreases
 *                                                           B might soon need to wait
 *                                                           for charging
 */

@Setter
@Getter
@Log
public class ChargeEnvironment implements EnvironmentInterface<ChargeVariables> {

    public static final Set<SiteState> FAIL_STATES = Set.of(isTwoCharging, isAnySoCBad, isTwoAtSamePos);
    ChargeEnvironmentSettings settings;
    ChargeEnvironmentLambdas lambdas;
    final PositionTransitionRules positionTransitionRules;
    final SiteStateRules siteStateRules;
    boolean isObstacle;

    public ChargeEnvironment() {
        this(ChargeEnvironmentSettings.newDefault());
    }

    public ChargeEnvironment(ChargeEnvironmentSettings settings) {
        this.settings = settings;
        lambdas = new ChargeEnvironmentLambdas(settings);
        positionTransitionRules = new PositionTransitionRules(settings);
        siteStateRules = new SiteStateRules(settings);
        isObstacle = settings.isObstacleStart();
        log.info("ChargeEnvironment created");
    }

    @Override
    public StepReturn<ChargeVariables> step(StateInterface<ChargeVariables> state, int action) {

        throwIfBadArgument(state, action);
        Positions positions = getNewPositions(state, action);
        SoCLevels socs = getNewSoCLevels(state, positions);

        ChargeState newState = new ChargeState(ChargeVariables.builder()
                .posA(positions.posA()).posB(positions.posB())
                .socA(socs.socA()).socB(socs.socB())
                .time(ChargeState.time.apply(state) + 1)
                .build());
        SiteState siteState = siteStateRules.getSiteState(newState);
        return StepReturn.<ChargeVariables>builder()
                .newState(newState)
                .reward(getReward(state, positions, siteState))
                .isNewStateTerminal(!siteState.equals(isAllFine))  //also valid for timeUp
                .isNewStateFail(FAIL_STATES.contains(siteState))
                .build();
    }

    @Override
    public boolean isTerminalState(StateInterface<ChargeVariables> state) {
        return siteStateRules.getSiteState(state) != SiteState.isAllFine;
    }

    @Override
    public Set<Integer> actionSet() {
        return MySetUtils.getSetFromRange(0, settings.nofActions());
    }

    @SneakyThrows
    @Override
    public Set<StateInterface<ChargeVariables>> stateSet() {
        throw new NoSuchMethodException();
    }

    private void throwIfBadArgument(StateInterface<ChargeVariables> state, int action) {
        if (lambdas.isNonValidAction.test(action) ||
                lambdas.isNonValidPos.test(posA.apply(state)) || lambdas.isNonValidPos.test(posB.apply(state)) ||
                lambdas.isNonValidSoC.test(socA.apply(state)) || lambdas.isNonValidSoC.test(socB.apply(state)) ||
                isNonValidTime.test(time.apply(state))
        ) {
            throw new IllegalArgumentException("Non valid action or state. State = " + state + ", action = " + action);
        }
    }

    private Positions getNewPositions(StateInterface<ChargeVariables> state, int action) {
        Commands commands = settings.commandMap().get(action);
        int posANew = positionTransitionRules.getNewPos(posA.apply(state), isObstacle, commands.cA());
        int posBNew = positionTransitionRules.getNewPos(posB.apply(state), isObstacle, commands.cB());
        return Positions.of(posANew, posBNew);
    }

    private SoCLevels getNewSoCLevels(StateInterface<ChargeVariables> state,
                                      Positions positionsNew) {
        double dSoCA = getDeltaSoC(posA.apply(state), positionsNew.posA());
        double dSoCB = getDeltaSoC(posB.apply(state), positionsNew.posB());
        Function<Double,Double> clipper=(soc) ->  MathUtils.clip(soc, settings.socMin(), settings.socMax());
        return SoCLevels.of(clipper.apply(socA.apply(state) + dSoCA),clipper.apply(socB.apply(state) + dSoCB));
    }

    private double getDeltaSoC(int pos, int posNew) {
        boolean isNotInSite = !lambdas.isPosInSite.test(posNew);
        if (isNotInSite) {
            return 0;  //todo setting
        }

        boolean isInCharge = lambdas.isChargePos.test(posNew);
        if (isInCharge) {
            return settings.deltaSocInChargeArea();
        } else {
            return isMoving.test(pos, posNew)
                    ? settings.deltaSocMovingNotInChargeArea()
                    : settings.deltaSocStillNotInChargeArea();
        }
    }

    private double getReward(StateInterface<ChargeVariables> state,
                             Positions positionsNew,
                             SiteState siteState) {

        Integer posAnew = positionsNew.posA(), posBnew = positionsNew.posB();
        boolean isAStill=isStill.test(posA.apply(state), posAnew);
        boolean isBStill=isStill.test(posB.apply(state), posBnew);
        boolean isAInSite=lambdas.isPosInSite.test(posAnew);
        boolean isBInSite=lambdas.isPosInSite.test(posBnew);
        boolean isAInChargeArea = lambdas.isChargePos.test(posAnew);
        boolean isBInChargeArea = lambdas.isChargePos.test(posBnew);

        BiFunction<Boolean, Boolean, Double> costQue = (isStill, isInSite) -> (isStill && isInSite) ? -settings.costQue() : 0d;
        BiFunction<Boolean, Boolean, Double> costCharge = (isA, isB) -> (isA || isB) ? -settings.costCharge() : 0d;
        Function<SiteState, Double> failPenalty = (ss) -> FAIL_STATES.contains(ss) ? settings.rewardBad() : 0;

        return  costQue.apply(isAStill,isAInSite)+costQue.apply(isBStill,isBInSite)+
                costCharge.apply(isAInChargeArea, isBInChargeArea) +
                failPenalty.apply(siteState);
    }


}
