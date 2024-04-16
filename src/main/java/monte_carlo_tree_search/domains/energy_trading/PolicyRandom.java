package monte_carlo_tree_search.domains.energy_trading;

import common.other.RandUtils;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PolicyRandom
        implements SimulationPolicyInterface<VariablesEnergyTrading, Integer> {

    private static final int DUMMY_VALUE = 0;
    RandUtils<Integer> randGenerator;
    List<Integer> actionList;

    public PolicyRandom() {
        randGenerator = new RandUtils<>();
        ActionEnergyTrading action = getDummyAction();
        actionList = new ArrayList<>(action.applicableActions());
    }

    @Override
    public ActionInterface<Integer> chooseAction(StateInterface<VariablesEnergyTrading> state) {

        int actionValue = randGenerator.getRandomItemFromList(actionList);
        return ActionEnergyTrading.newValue(actionValue);
    }

    @Override
    public Set<Integer> availableActionValues(StateInterface<VariablesEnergyTrading> state) {
        ActionEnergyTrading action = getDummyAction();
        return action.applicableActions();
    }

    @NotNull
    private ActionEnergyTrading getDummyAction() {
        return ActionEnergyTrading.newValue(DUMMY_VALUE);
    }
}
