package multi_agent_rl.environments.apple;

import com.google.common.base.Preconditions;
import common.math.Discrete2DVector;
import common.other.RandUtilsML;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum ActionAppleRobot {
    N(0,Discrete2DVector.of(0,1)),
    E(1, Discrete2DVector.of(1,0)),
    S(2, Discrete2DVector.of(0,-1)),
    W(3, Discrete2DVector.of(-1,0)),
    STOP(4, Discrete2DVector.of(0,0));

    @Getter  private final int index;
    @Getter private final Discrete2DVector direction;

    private static final Map<Integer, ActionAppleRobot> INTEGER_ACTION_ROBOT_MAP
            = new HashMap<>();

    // Static block to populate the map
    static {
        for (ActionAppleRobot myEnum : ActionAppleRobot.values()) {
            INTEGER_ACTION_ROBOT_MAP.put(myEnum.index, myEnum);
        }
    }

    ActionAppleRobot(int value, Discrete2DVector direction) {
        this.index = value;
        this.direction = direction;
    }

    public static ActionAppleRobot fromInt(int i) {
        Preconditions.checkArgument(
                INTEGER_ACTION_ROBOT_MAP.containsKey(i),"Action index not present");
        return INTEGER_ACTION_ROBOT_MAP.get(i);
    }

    public static ActionAppleRobot random() {
        int randIdx= RandUtilsML.getRandomIntNumber(0, nActions() -1);
        return INTEGER_ACTION_ROBOT_MAP.get(randIdx);
    }

    public static int nActions() {
        return ActionAppleRobot.INTEGER_ACTION_ROBOT_MAP.size();
    }


}
