package multi_agent_rl.environments.apple;

import common.math.Discrete2DPos;
import lombok.AllArgsConstructor;
import multi_agent_rl.domain.abstract_classes.Action;
import multi_agent_rl.domain.abstract_classes.EnvironmentI;
import multi_agent_rl.domain.abstract_classes.StateI;
import multi_agent_rl.domain.value_classes.StepReturn;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import static common.other.MyFunctions.numIfTrueElseZero;

@AllArgsConstructor
public class EnvironmentApple implements EnvironmentI<VariablesApple> {

    public static final double REWARD_COLLECTED = 10d, REWARD_SAME_POS = -1, REWARD_MOVE = -0.1;
    public static final int INDEX_A = 0, INDEX_B = 1;

    AppleSettings settings;

    public static EnvironmentApple newDefault() {
        return new EnvironmentApple(AppleSettings.builder()
                .minPos(Discrete2DPos.of(0, 0))
                .maxPos(Discrete2DPos.of(4, 4))
                .build());
    }

    @Override
    public StepReturn<VariablesApple> step(StateI<VariablesApple> state0, Action action) {
        StateApple stateApple = (StateApple) state0;
        var newState = getNewState(action, stateApple);
        boolean isAppleBetween = isAppleBetween(stateApple);
        boolean isAtSamePos = isAtSamePos(stateApple);
        return StepReturn.<VariablesApple>builder()
                .state(newState)
                .isFail(false)
                .isTerminal(isAppleBetween)
                .reward(getReward(isAppleBetween, isAtSamePos))
                .build();
    }

    private static boolean isAtSamePos(StateApple stateApple) {
        return stateApple.posA().equals(stateApple.posB());
    }

    private static boolean isAppleBetween(StateApple stateApple) {
        Optional<Discrete2DPos> posBetween = stateApple.posA().midPos(stateApple.posB());
        return posBetween
                .map(discrete2DPos -> discrete2DPos.equals(stateApple.posApple()))
                .orElse(false);
    }

    private static double getReward(boolean isAppleBetween, boolean isAtSamePos) {
        return numIfTrueElseZero.apply(isAppleBetween, REWARD_COLLECTED) +
                numIfTrueElseZero.apply(isAtSamePos, REWARD_SAME_POS) +
                REWARD_MOVE;
    }

    @NotNull
    private StateI<VariablesApple> getNewState(Action action, StateApple stateApple) {
        var actionList = action.asInts();
        var actionA = ActionRobot.fromInt(actionList.get(INDEX_A));
        var actionB = ActionRobot.fromInt(actionList.get(INDEX_B));
        var minPos = settings.minPos();
        var maxPos = settings.maxPos();
        return new StateApple(VariablesApple.builder()
                .posA(stateApple.posA().move(actionA.getDirection()).clip(minPos, maxPos))
                .posB(stateApple.posB().move(actionB.getDirection()).clip(minPos, maxPos))
                .build());
    }
}
