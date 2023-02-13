package monte_carlo_tree_search.domains.elevator;

import lombok.extern.java.Log;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import org.apache.commons.lang3.RandomUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@Log
public class PolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping
        implements SimulationPolicyInterface<VariablesElevator, Integer> {

    private static final int BACKUP = 0;
    private static final int POS_BETWEEN = 10;
    private static final int TOP_POS = 30;
    private static final int BOTTOM_POS = 0;
    private static final int SPEED_STILL = 0;
    private static final int SPEED_UP = 1;
    private static final int SPEED_DOWN = -1;

    Map<BiPredicate<Integer,Integer>, BiFunction<Integer,Integer,Integer>> decisionTable;

    public PolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping() {

        BiPredicate<Integer,Integer> isAtTop = (s, p) -> p == TOP_POS;
        BiPredicate<Integer,Integer> isNotAtTop = isAtTop.negate();
        BiPredicate<Integer,Integer> isAtBottom = (s, p)-> p == BOTTOM_POS;
        BiPredicate<Integer,Integer> isNotAtBottom = isAtBottom.negate();
        BiPredicate<Integer,Integer> isStill = (s, p) -> s == SPEED_STILL;
        BiPredicate<Integer,Integer> isMovingUp = (s, p) -> s == SPEED_UP;
        BiPredicate<Integer,Integer> isMovingDown = (s, p) -> s == SPEED_DOWN;
        BiPredicate<Integer,Integer> isAtFloor = (s, p) ->  p % POS_BETWEEN == 0;
        BiPredicate<Integer,Integer> isNotAtFloor = isAtFloor.negate();

        decisionTable = new HashMap<>();
        decisionTable.put( (s,p) -> isMovingUp.and(isNotAtFloor).test(s,p) ,(s, p) -> SPEED_UP);
        decisionTable.put( (s,p) -> isMovingUp.and(isAtFloor).test(s,p),(s, p) -> SPEED_STILL);
        decisionTable.put( (s,p) -> isStill.and(isAtFloor).and(isNotAtTop).test(s,p),(s, p) -> SPEED_UP);
        decisionTable.put( (s,p) -> isStill.and(isAtFloor).and(isAtTop).test(s,p),(s, p) -> SPEED_DOWN);
        decisionTable.put( (s,p) -> isMovingDown.and(isNotAtFloor).test(s,p) ,(s, p) -> SPEED_DOWN);
        decisionTable.put( (s,p) -> isMovingDown.and(isAtFloor).test(s,p),(s, p) -> SPEED_STILL);
        decisionTable.put( (s,p) -> isStill.and(isAtFloor).and(isNotAtBottom).test(s,p),(s, p) -> SPEED_DOWN);
        decisionTable.put( (s,p) -> isStill.and(isAtFloor).and(isAtBottom).test(s,p),(s, p) -> SPEED_UP);

    }

    @Override
    public ActionInterface<Integer> chooseAction(StateInterface<VariablesElevator> state) {
        Integer speed=state.getVariables().speed;
        Integer pos=state.getVariables().pos;
        return ActionElevator.newValueDefaultRange(process(speed,pos));
    }

    public  Integer process(Integer speed, Integer pos) {

        List<BiFunction<Integer,Integer,Integer>> fcnList=decisionTable.entrySet().stream()
                .filter(e -> e.getKey().test(speed,pos))
                .map(e -> e.getValue())
                .collect(Collectors.toList());

        if (fcnList.size()>1) {
            log.warning("Multiple matching rules, nof ="+fcnList.size()+". Applying random.");
            return fcnList.get(RandomUtils.nextInt(0,fcnList.size())).apply(speed,pos);
        }

        if (fcnList.size()==0) {
            log.warning("No matching rule, using backup");
            return BACKUP;
        }

        return fcnList.get(0).apply(speed,pos);
    }
}
