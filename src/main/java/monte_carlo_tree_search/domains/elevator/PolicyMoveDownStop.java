package monte_carlo_tree_search.domains.elevator;

import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class PolicyMoveDownStop
    implements SimulationPolicyInterface<VariablesElevator, Integer>  {
    Map<BiPredicate<Integer,Integer>, BiFunction<Integer,Integer,Integer>> decisionTable;

    private static final int BOTTOM_POS = 0;
    private static final int SPEED_DOWN = -1;
    private static final int SPEED_STILL = 0;

    public PolicyMoveDownStop() {

        BiPredicate<Integer,Integer> isAtBottom = (s, p)-> p == BOTTOM_POS;
        BiPredicate<Integer,Integer> isNotAtBottom = isAtBottom.negate();

        decisionTable = new HashMap<>();
        decisionTable.put(isNotAtBottom,(s, p) -> SPEED_DOWN);
        decisionTable.put(isAtBottom,(s, p) -> SPEED_STILL);
    }

    @Override
    public ActionInterface<Integer> chooseAction(StateInterface<VariablesElevator> state) {
        Integer speed=state.getVariables().speed;
        Integer pos=state.getVariables().pos;
        DecisionTableReader reader=new DecisionTableReader(decisionTable);
        return ActionElevator.newValueDefaultRange(reader.readSingleActionChooseRandomIfMultiple(speed,pos));
    }
}
