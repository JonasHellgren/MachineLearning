package monte_carlo_tree_search.domains.elevator;

import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class PolicyMoveDownStop
    implements SimulationPolicyInterface<VariablesElevator, Integer>  {
    Map<ElevatorTriPredicates.TriPredicate<Integer,Integer, Double>, BiFunction<Integer,Integer,Integer>> decisionTable;

    private static final int BOTTOM_POS = 0;
    private static final int SPEED_DOWN = -1;
    private static final int SPEED_STILL = 0;

    public PolicyMoveDownStop() {

        decisionTable = new HashMap<>();
        decisionTable.put(ElevatorTriPredicates.isNotAtBottom,(s, p) -> SPEED_DOWN);
        decisionTable.put(ElevatorTriPredicates.isAtBottom,(s, p) -> SPEED_STILL);
    }

    @Override
    public ActionInterface<Integer> chooseAction(StateInterface<VariablesElevator> state) {
        Integer speed=state.getVariables().speed;
        Integer pos=state.getVariables().pos;
        Double SoE=state.getVariables().SoE;
        DecisionTableReader reader=new DecisionTableReader(decisionTable);
        return ActionElevator.newValueDefaultRange(reader.readSingleActionChooseRandomIfMultiple(speed,pos, SoE));
    }

    @Override
    public Set<Integer> availableActionValues(StateInterface<VariablesElevator> state) {
        ActionElevator actionElevator=ActionElevator.newValueDefaultRange(0);
        return actionElevator.applicableActions();
    }

}
