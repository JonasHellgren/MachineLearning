package multi_agent_rl.environments.apple;

import common.math.Discrete2DPos;
import lombok.AllArgsConstructor;
import multi_agent_rl.domain.abstract_classes.ActionJoint;
import multi_agent_rl.domain.abstract_classes.EnvironmentI;
import multi_agent_rl.domain.abstract_classes.StateI;
import multi_agent_rl.domain.value_classes.StepReturn;

import static common.math.MathUtils.isEqualDoubles;
import static common.other.MyFunctions.numIfTrueElseZero;

@AllArgsConstructor
public class EnvironmentApple implements EnvironmentI<VariablesApple> {

    public static final double REWARD_COLLECTED = 10d, REWARD_SAME_POS = -1, REWARD_MOVE = -0.1;
    public static final int INDEX_A = 0, INDEX_B = 1;
    public static final double DIST_SMALL = 1e-2;
    public static final int DIST_2CELLS = 2;
    public static final double DIAGONAL_TRIANGLE = Discrete2DPos.of(0,0)
            .distance(Discrete2DPos.of(DIST_2CELLS,DIST_2CELLS));
    AppleSettings settings;

    public static EnvironmentApple newDefault() {
        return new EnvironmentApple(AppleSettings.newDefault());
    }

    @Override
    public StepReturn<VariablesApple> step(StateI<VariablesApple> state0, ActionJoint action) {
        StateApple stateApple = (StateApple) state0;
        var newState = getNewState(action, stateApple);
        boolean isAppleCollected = isAppleBetweenRobots(newState) &&
                isCorrectDistanceCollected(newState);
        return StepReturn.<VariablesApple>builder()
                .state(newState)
                .isFail(false)
                .isTerminal(isAppleCollected)
                .reward(getReward(isAppleCollected, isRobotsAtSamePos(newState)))
                .build();
    }

    static boolean isCorrectDistanceCollected(StateApple newState) {
        double dist=newState.posA().distance(newState.posB());
        return isEqualDoubles(DIST_2CELLS,dist, DIST_SMALL) ||
                isEqualDoubles(DIAGONAL_TRIANGLE,dist, DIST_SMALL);
    }

    static boolean isRobotsAtSamePos(StateApple stateApple) {
        return stateApple.posA().equals(stateApple.posB());
    }

    static boolean isAppleBetweenRobots(StateApple stateApple) {
        var posBetween = stateApple.posA().midPos(stateApple.posB());
        return posBetween
                .map(discrete2DPos -> discrete2DPos.equals(stateApple.posApple()))
                .orElse(false);
    }

    static double getReward(boolean isAppleBetween, boolean isAtSamePos) {
        return numIfTrueElseZero.apply(isAppleBetween, REWARD_COLLECTED) +
                numIfTrueElseZero.apply(isAtSamePos, REWARD_SAME_POS) +
                REWARD_MOVE;
    }

    StateApple getNewState(ActionJoint action, StateApple stateApple) {
        var actionList = action.asInts();
        var actionA = ActionRobot.fromInt(actionList.get(INDEX_A));
        var actionB = ActionRobot.fromInt(actionList.get(INDEX_B));
        var clipPosA = moveAndClipPos(stateApple.posA(), actionA);
        var clipPosB = moveAndClipPos(stateApple.posB(),actionB);
        var posANew= getNewPosNotAtApple(clipPosA, stateApple.posA(), stateApple.posApple());
        var posBNew= getNewPosNotAtApple(clipPosB, stateApple.posB(), stateApple.posApple());
        return new StateApple(VariablesApple.builder()
                .posA(posANew)
                .posB(posBNew)
                .posApple(stateApple.posApple())
                .build());
    }

    Discrete2DPos getNewPosNotAtApple(Discrete2DPos clipPosA, Discrete2DPos pos, Discrete2DPos posApple) {
        return clipPosA.equals(posApple) ? pos : clipPosA;
    }

    Discrete2DPos moveAndClipPos(Discrete2DPos pos,ActionRobot actionA) {
        return pos.move(actionA.getDirection()).clip(settings.minPos(), settings.maxPos());
    }
}
