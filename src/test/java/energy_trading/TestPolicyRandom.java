package energy_trading;

import monte_carlo_tree_search.domains.energy_trading.ActionEnergyTrading;
import monte_carlo_tree_search.domains.energy_trading.PoliciesEnergyTrading;
import monte_carlo_tree_search.domains.energy_trading.StateEnergyTrading;
import monte_carlo_tree_search.domains.energy_trading.VariablesEnergyTrading;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class TestPolicyRandom {

    @Test
    public void  givenRandomPolicy_trenCorrectActionSetAndValue () {

        SimulationPolicyInterface<VariablesEnergyTrading, Integer> policy= PoliciesEnergyTrading.newRandom();
        StateInterface<VariablesEnergyTrading> state= StateEnergyTrading.newFromVariables(VariablesEnergyTrading.newDefault());
        Set<Integer> actionSet= policy.availableActionValues(state);
        System.out.println("actionSet = " + actionSet);
        Assert.assertEquals(5,actionSet.size());

        for (int i = 0; i < 100 ; i++) {
            ActionInterface<Integer> action=policy.chooseAction(state);
            System.out.println("action = " + action);
            Assert.assertTrue(action.getValue() >= ActionEnergyTrading.MIN_ACTION_DEFAULT);
        }
    }


}
