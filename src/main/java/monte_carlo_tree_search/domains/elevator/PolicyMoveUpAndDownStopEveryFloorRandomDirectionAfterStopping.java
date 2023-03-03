package monte_carlo_tree_search.domains.elevator;

import lombok.extern.java.Log;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import org.apache.commons.lang3.RandomUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * multiple equal conditions means branching
 * for ex isMovingUp.and(isAtFloor).. gives SPEED_UP, SPEED_STILL or SPEED_DOWN
 */

@Log
public class PolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping
        implements SimulationPolicyInterface<VariablesElevator, Integer> {

    private static final int SPEED_STILL = ActionElevator.STILL_ACTION;
    private static final int SPEED_UP = ActionElevator.MAX_ACTION_DEFAULT;
    private static final int SPEED_DOWN = ActionElevator.MIN_ACTION_DEFAULT;

    Map<BiPredicate<Integer,Integer>, BiFunction<Integer,Integer,Integer>> decisionTable;

    public PolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping() {

        BiPredicate<Integer,Integer> isAtTop = EnvironmentElevator.isAtTop;
        BiPredicate<Integer,Integer> isStill = EnvironmentElevator.isStill;
        BiPredicate<Integer,Integer> isMovingUp = (s, p) -> s == SPEED_UP;
        BiPredicate<Integer,Integer> isMovingDown = (s, p) -> s == SPEED_DOWN;
        BiPredicate<Integer,Integer> isAtFloor = EnvironmentElevator.isAtFloor;
        BiPredicate<Integer,Integer> isNotAtFloor = isAtFloor.negate();

        decisionTable = new HashMap<>();
        decisionTable.put( (s,p) -> isMovingUp.and(isNotAtFloor).test(s,p) ,(s, p) -> SPEED_UP);

        decisionTable.put( (s,p) -> isMovingDown.and(isNotAtFloor).test(s,p) ,(s, p) -> SPEED_DOWN);

        /*
        decisionTable.put( (s,p) -> isMovingUp.and(isAtFloor).test(s,p),(s, p) -> SPEED_UP);
        decisionTable.put( (s,p) -> isMovingUp.and(isAtFloor).test(s,p),(s, p) -> SPEED_STILL);
        decisionTable.put( (s,p) -> isMovingUp.and(isAtFloor).test(s,p),(s, p) -> SPEED_DOWN);

        decisionTable.put( (s,p) -> isMovingDown.and(isAtFloor).test(s,p),(s, p) -> SPEED_UP);
        decisionTable.put( (s,p) -> isMovingDown.and(isAtFloor).test(s,p),(s, p) -> SPEED_STILL);
        decisionTable.put( (s,p) -> isMovingDown.and(isAtFloor).test(s,p),(s, p) -> SPEED_DOWN);
*/

        decisionTable.put( (s,p) -> isAtFloor.test(s,p),(s, p) -> SPEED_UP);
        decisionTable.put( (s,p) -> isAtFloor.test(s,p),(s, p) -> SPEED_STILL);
        decisionTable.put( (s,p) -> isAtFloor.test(s,p),(s, p) -> SPEED_DOWN);


        decisionTable.put((s,p) -> isStill.and(isAtTop).test(s,p),(s, p) -> SPEED_STILL);
        decisionTable.put((s,p) -> isStill.and(isAtTop).test(s,p),(s, p) -> SPEED_DOWN);

        decisionTable.put((s,p) -> isStill.and(isAtFloor).test(s,p),(s, p) -> SPEED_UP);
        decisionTable.put((s,p) -> isStill.and(isAtFloor).test(s,p),(s, p) -> SPEED_STILL);
    }

    @Override
    public ActionInterface<Integer> chooseAction(StateInterface<VariablesElevator> state) {
        Integer speed=state.getVariables().speed;
        Integer pos=state.getVariables().pos;
        DecisionTableReader reader=new DecisionTableReader(decisionTable);
        return ActionElevator.newValueDefaultRange(reader.readSingleActionChooseRandomIfMultiple(speed,pos));
    }

    public Set<Integer> availableActionValues(StateInterface<VariablesElevator> state) {
        Integer speed=state.getVariables().speed;
        Integer pos=state.getVariables().pos;
        DecisionTableReader reader=new DecisionTableReader(decisionTable);
        return reader.readAllAvailableActions(speed, pos);
    }

}
