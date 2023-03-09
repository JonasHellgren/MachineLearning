package monte_carlo_tree_search.domains.elevator;

import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class PolicyRandomDirectionAfterFloorIfOkSoENotUpIfLowSoE
        implements SimulationPolicyInterface<VariablesElevator, Integer> {
    private static final int SPEED_STILL = ActionElevator.STILL_ACTION;
    private static final int SPEED_UP = ActionElevator.MAX_ACTION_DEFAULT;
    private static final int SPEED_DOWN = ActionElevator.MIN_ACTION_DEFAULT;

    Map<ElevatorTriPredicates.TriPredicate<Integer, Integer, Double>, Supplier<Integer>> decisionTable;

    public PolicyRandomDirectionAfterFloorIfOkSoENotUpIfLowSoE() {

        decisionTable = new HashMap<>();
        decisionTable.put((s, p, soe) -> ElevatorTriPredicates.isMovingUp.and(ElevatorTriPredicates.isNotAtFloor).test(s, p, soe), () -> SPEED_UP);
        decisionTable.put((s, p, soe) -> ElevatorTriPredicates.isMovingDown.and(ElevatorTriPredicates.isNotAtFloor).test(s, p, soe), () -> SPEED_DOWN);

        decisionTable.put( (s,p,soe) -> ElevatorTriPredicates.isStill.and(ElevatorTriPredicates.isNotAtFloor).test(s,p,soe) ,() -> SPEED_UP);
        decisionTable.put( (s,p,soe) -> ElevatorTriPredicates.isStill.and(ElevatorTriPredicates.isNotAtFloor).test(s,p,soe) ,() -> SPEED_DOWN);

        decisionTable.put((s, p, soe) -> ElevatorTriPredicates.isAtFloor.and(ElevatorTriPredicates.isSoEOk).test(s, p, soe), () -> SPEED_UP);
        decisionTable.put((s, p, soe) -> ElevatorTriPredicates.isAtFloor.and(ElevatorTriPredicates.isSoEOk).test(s, p, soe), () -> SPEED_STILL);
        decisionTable.put((s, p, soe) -> ElevatorTriPredicates.isAtFloor.and(ElevatorTriPredicates.isSoEOk).test(s, p, soe), () -> SPEED_DOWN);

        decisionTable.put((s, p, soe) -> ElevatorTriPredicates.isAtFloor.and(ElevatorTriPredicates.isSoENotOk).test(s, p, soe), () -> SPEED_STILL);
        decisionTable.put((s, p, soe) -> ElevatorTriPredicates.isAtFloor.and(ElevatorTriPredicates.isSoENotOk).test(s, p, soe), () -> SPEED_DOWN);

        decisionTable.put((s, p, soe) -> ElevatorTriPredicates.isStill.and(ElevatorTriPredicates.isAtTop).test(s, p, soe), () -> SPEED_STILL);
        decisionTable.put((s, p, soe) -> ElevatorTriPredicates.isStill.and(ElevatorTriPredicates.isAtTop).test(s, p, soe), () -> SPEED_DOWN);

        decisionTable.put((s, p, soe) -> ElevatorTriPredicates.isStill.and(ElevatorTriPredicates.isAtFloor).and(ElevatorTriPredicates.isSoEOk).test(s, p, soe), () -> SPEED_UP);
        decisionTable.put((s, p, soe) -> ElevatorTriPredicates.isStill.and(ElevatorTriPredicates.isAtFloor).test(s, p, soe), () -> SPEED_STILL);
    }

    @Override
    public ActionInterface<Integer> chooseAction(StateInterface<VariablesElevator> state) {
        Integer speed = state.getVariables().speed;
        Integer pos = state.getVariables().pos;
        Double SoE = state.getVariables().SoE;
        ElevatorDecisionTableReader reader = new ElevatorDecisionTableReader(decisionTable);
        return ActionElevator.newValueDefaultRange(reader.readSingleActionChooseRandomIfMultiple(speed, pos, SoE));
    }

    public Set<Integer> availableActionValues(StateInterface<VariablesElevator> state) {
        Integer speed = state.getVariables().speed;
        Integer pos = state.getVariables().pos;
        Double SoE = state.getVariables().SoE;
        ElevatorDecisionTableReader reader = new ElevatorDecisionTableReader(decisionTable);
        return reader.readAllAvailableActions(speed, pos, SoE);
    }

}
