package marl;

import common.math.Discrete2DPos;
import multi_agent_rl.domain.abstract_classes.ActionJoint;
import multi_agent_rl.domain.abstract_classes.StateI;
import multi_agent_rl.environments.apple.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**      0    1   2   3   4
 *4     |   |   |   |   |   |
 *3     |   | A |   | B |   |
 *2     |   |   | o |   |   |
 *1     |   |   |   |   |   |
 *0     |   |   |   |   |   |
 */


public class TestEnvironmentApple {

    public static final Discrete2DPos POS_APPLE = Discrete2DPos.of(2, 2);
    public static final Discrete2DPos POS_A = Discrete2DPos.of(1, 3);
    public static final Discrete2DPos POS_B = Discrete2DPos.of(3, 3);
    public static final int STOP = ActionRobot.STOP.getIndex();
    public static final int WEST = ActionRobot.W.getIndex();
    public static final int EAST = ActionRobot.E.getIndex();
    public static final int SOUTH = ActionRobot.S.getIndex();
    public static final int NORTH = ActionRobot.N.getIndex();

    EnvironmentApple environment;
    AppleSettings settings=AppleSettings.newDefault();
    StateApple startState;

    @BeforeEach
    void init() {
        environment=new EnvironmentApple(settings);
        startState=StateApple.of(POS_APPLE, POS_A, POS_B,settings);
    }

    @Test
    void whenStill_thenCorrect() {
        var sr=environment.step(startState, ActionJoint.ofInteger(List.of(STOP,STOP)));
        Assertions.assertFalse(sr.isTerminal());
        Assertions.assertEquals(EnvironmentApple.REWARD_MOVE,sr.reward());
        Assertions.assertEquals(startState,sr.state());
    }

    @Test
    void whenBothToPos21_thenCorrect() {
        var sr=environment.step(startState, ActionJoint.ofInteger(List.of(EAST,WEST)));
        Assertions.assertFalse(sr.isTerminal());
        Assertions.assertEquals(EnvironmentApple.REWARD_MOVE+EnvironmentApple.REWARD_SAME_POS,sr.reward());
    }

    @Test
    void whenBothSouth_thenCorrect() {
        var sr=environment.step(startState, ActionJoint.ofInteger(List.of(SOUTH,SOUTH)));
        Assertions.assertTrue(sr.isTerminal());
        Assertions.assertEquals(EnvironmentApple.REWARD_MOVE+EnvironmentApple.REWARD_COLLECTED,sr.reward());
    }

    @Test
    void whenInEachCorner_thenNotCollected() {
        Discrete2DPos cornerA = Discrete2DPos.of(0, 4);
        Discrete2DPos cornerB = Discrete2DPos.of(4, 0);
        StateI<VariablesApple> state=StateApple.of(POS_APPLE,cornerA,cornerB,settings);
        var sr=environment.step(state, ActionJoint.ofInteger(List.of(STOP,STOP)));
        Assertions.assertFalse(sr.isTerminal());
        Assertions.assertEquals(EnvironmentApple.REWARD_MOVE,sr.reward());
    }

    @Test
    void whenASouth2Times_thenCollected() {
        ActionJoint action = ActionJoint.ofInteger(List.of(SOUTH, STOP));
        var sr0=environment.step(startState, action);
        var sr1=environment.step(sr0.state(), action);
        Assertions.assertTrue(sr1.isTerminal());
        Assertions.assertEquals(EnvironmentApple.REWARD_MOVE+EnvironmentApple.REWARD_COLLECTED,sr1.reward());
    }

    @Test
    void whenANorth2Times_thenHittingWall() {
        ActionJoint action = ActionJoint.ofInteger(List.of(NORTH, STOP));
        var sr0=environment.step(startState, action);
        var sr1=environment.step(sr0.state(), action);
        Assertions.assertFalse(sr1.isTerminal());
        Assertions.assertEquals(EnvironmentApple.REWARD_MOVE,sr1.reward());
        Assertions.assertEquals(Discrete2DPos.of(1, 4),sr1.state().getVariables().posA());
    }

    @Test
    void whenASouthAndEast_thenNotAtApple() {
        ActionJoint action0 = ActionJoint.ofInteger(List.of(SOUTH, STOP));
        ActionJoint action1 = ActionJoint.ofInteger(List.of(EAST, STOP));
        var sr0=environment.step(startState, action0);
        var sr1=environment.step(sr0.state(), action1);
        Assertions.assertFalse(sr1.isTerminal());
        Assertions.assertEquals(EnvironmentApple.REWARD_MOVE,sr1.reward());
        Assertions.assertEquals(Discrete2DPos.of(1, 2),sr1.state().getVariables().posA());
    }

    @Test
    void whenBSouthAndWest_thenNotAtApple() {
        ActionJoint action0 = ActionJoint.ofInteger(List.of(STOP,SOUTH));
        ActionJoint action1 = ActionJoint.ofInteger(List.of(STOP,WEST));

        var sr0=environment.step(startState, action0);
        var sr1=environment.step(sr0.state(), action1);
        Assertions.assertFalse(sr1.isTerminal());
        Assertions.assertEquals(EnvironmentApple.REWARD_MOVE,sr1.reward());
        Assertions.assertEquals(Discrete2DPos.of(3, 2),sr1.state().getVariables().posB());
    }


    @Test
    void whenManyRandomActions_thenFinallyCollected() {

        StateI<VariablesApple> state=null;
        for (int i = 0; i < 10000; i++) {
             state = getStateCollected();
        }
        var posA = state.getVariables().posA();
        var posB = state.getVariables().posB();
        var xList= List.of(posA.x(),posB.x());
        var yList= List.of(posA.y(),posB.y());
        var possibleXCollectPos=List.of(1,2,3);
        var possibleYCollectPos=List.of(1,2,3);

        Assertions.assertTrue(possibleXCollectPos.stream().anyMatch(x -> xList.contains(x)));
        Assertions.assertTrue(possibleYCollectPos.stream().anyMatch(y -> yList.contains(y)));
    }

    private StateI<VariablesApple> getStateCollected() {
        boolean collected=false;
        StateI<VariablesApple> state=startState;
        while (!collected) {
            ActionJoint action = ActionJoint.ofInteger(List.of(
                    ActionRobot.random().getIndex(),
                    ActionRobot.random().getIndex()));
            var sr=environment.step(state, action);
            state=sr.state();
            collected=sr.isTerminal();
        }
        return state;
    }


}
