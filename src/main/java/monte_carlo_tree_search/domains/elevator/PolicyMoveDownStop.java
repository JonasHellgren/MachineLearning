package monte_carlo_tree_search.domains.elevator;

import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.*;
import java.util.function.Supplier;

public class PolicyMoveDownStop
    implements SimulationPolicyInterface<VariablesElevator, Integer>  {
    Map<ElevatorTriPredicates.TriPredicate<Integer,Integer, Double>, Supplier<Integer>> decisionTable;

    private static final int BOTTOM_POS = 0;
    private static final int SPEED_DOWN = -1;
    private static final int SPEED_STILL = 0;

    public PolicyMoveDownStop() {

        decisionTable = new HashMap<>();
        decisionTable.put(ElevatorTriPredicates.isNotAtBottom,() -> SPEED_DOWN);
        decisionTable.put(ElevatorTriPredicates.isAtBottom,() -> SPEED_STILL);
    }

    @Override
    public ActionInterface<Integer> chooseAction(StateInterface<VariablesElevator> state) {
        Integer speed=state.getVariables().speed;
        Integer pos=state.getVariables().pos;
        Double SoE=state.getVariables().SoE;
        ElevatorDecisionTableReader reader=new ElevatorDecisionTableReader(decisionTable);
        return ActionElevator.newValueDefaultRange(reader.readSingleActionChooseRandomIfMultiple(speed,pos, SoE));
    }

    @Override
    public Set<Integer> availableActionValues(StateInterface<VariablesElevator> state) {
        ActionElevator actionElevator=ActionElevator.newValueDefaultRange(0);
        return actionElevator.applicableActions();
    }

}
