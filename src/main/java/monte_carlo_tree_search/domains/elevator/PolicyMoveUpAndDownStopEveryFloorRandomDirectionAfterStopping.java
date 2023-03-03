package monte_carlo_tree_search.domains.elevator;

import lombok.extern.java.Log;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@Log
public class PolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping
        implements SimulationPolicyInterface<VariablesElevator, Integer> {

    private static final int SPEED_STILL = ActionElevator.STILL_ACTION;
    private static final int SPEED_UP = ActionElevator.MAX_ACTION_DEFAULT;
    private static final int SPEED_DOWN = ActionElevator.MIN_ACTION_DEFAULT;

    Map<BiPredicate<Integer,Integer>, BiFunction<Integer,Integer,Integer>> decisionTable;

    public PolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping() {

        BiPredicate<Integer,Integer> isAtTop = EnvironmentElevator.isAtTop;
        BiPredicate<Integer,Integer> isNotAtTop = isAtTop.negate();
        BiPredicate<Integer,Integer> isAtBottom = EnvironmentElevator.isBottomFloor;
        BiPredicate<Integer,Integer> isNotAtBottom = isAtBottom.negate();
        BiPredicate<Integer,Integer> isStill = EnvironmentElevator.isStill;
        BiPredicate<Integer,Integer> isMovingUp = (s, p) -> s == SPEED_UP;
        BiPredicate<Integer,Integer> isMovingDown = (s, p) -> s == SPEED_DOWN;
        BiPredicate<Integer,Integer> isAtFloor = EnvironmentElevator.isAtFloor;
        BiPredicate<Integer,Integer> isNotAtFloor = isAtFloor.negate();

        decisionTable = new HashMap<>();
        decisionTable.put( (s,p) -> isMovingUp.and(isNotAtFloor).test(s,p) ,(s, p) -> SPEED_UP);
        decisionTable.put( (s,p) -> isMovingUp.and(isAtFloor).test(s,p),(s, p) -> SPEED_STILL);
        decisionTable.put( (s,p) -> isMovingDown.and(isNotAtFloor).test(s,p) ,(s, p) -> SPEED_DOWN);
        decisionTable.put( (s,p) -> isMovingDown.and(isAtFloor).test(s,p),(s, p) -> SPEED_STILL);
       // decisionTable.put( (s,p) -> isStill.and(isAtFloor).and(isNotAtTop).test(s,p),(s, p) -> SPEED_UP);
        decisionTable.put( (s,p) -> isStill.and(isAtFloor).and(isAtTop).test(s,p),(s, p) -> SPEED_DOWN);
        //decisionTable.put( (s,p) -> isStill.and(isAtFloor).and(isNotAtBottom).test(s,p),(s, p) -> SPEED_DOWN);
        decisionTable.put( (s,p) -> isStill.and(isAtFloor).and(isNotAtBottom).and(isNotAtTop).test(s,p),(s, p) -> SPEED_UP);
        decisionTable.put( (s,p) -> isStill.and(isAtFloor).and(isNotAtBottom).and(isNotAtTop).test(s,p),(s, p) -> SPEED_DOWN);
        decisionTable.put( (s,p) -> isStill.and(isAtFloor).and(isAtBottom).test(s,p),(s, p) -> SPEED_UP);
        decisionTable.put( (s,p) -> isStill.and(isAtFloor).and(isAtBottom).test(s,p),(s, p) -> SPEED_STILL); //chance to charge
        decisionTable.put( (s,p) -> isStill.and(isNotAtFloor).test(s,p),(s, p) -> SPEED_UP);
        decisionTable.put( (s,p) -> isStill.and(isNotAtFloor).test(s,p),(s, p) -> SPEED_DOWN);
    }

    @Override
    public ActionInterface<Integer> chooseAction(StateInterface<VariablesElevator> state) {
        Integer speed=state.getVariables().speed;
        Integer pos=state.getVariables().pos;
        DecisionTableReader reader=new DecisionTableReader(decisionTable);
        return ActionElevator.newValueDefaultRange(reader.readSingleAction(speed,pos));
    }

    public List<Integer> availableActionValues(StateInterface<VariablesElevator> state) {
        Integer speed=state.getVariables().speed;
        Integer pos=state.getVariables().pos;
        DecisionTableReader reader=new DecisionTableReader(decisionTable);
        return reader.readAllAvailableActions(speed, pos);
    }

}
