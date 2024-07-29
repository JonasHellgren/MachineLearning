package monte_carlo_search.mcts_energy_trading;

import monte_carlo_tree_search.domains.energy_trading.StateEnergyTrading;
import monte_carlo_tree_search.domains.energy_trading.VariablesEnergyTrading;
import monte_carlo_tree_search.interfaces.StateInterface;
import org.junit.Assert;
import org.junit.Test;

public class TestStateEnergyTrading {

    private static final double DELTA = 0.01;

    @Test
    public void givenStateDefaultVariables_thenCorrect() {
        StateInterface<VariablesEnergyTrading> state=StateEnergyTrading.newFromVariables(VariablesEnergyTrading.newDefault());
        System.out.println("stateNew = " + state);
        Assert.assertEquals(0,state.getVariables().time);
        Assert.assertEquals(VariablesEnergyTrading.DEFAULT_SOE,state.getVariables().SoE, DELTA);
    }


    @Test(expected = IllegalArgumentException.class)
    public void givenStateNegativeTimeVariables_thenThrow() {
        StateEnergyTrading.newFromVariables(VariablesEnergyTrading.builder()
                .time(-1).build());
    }

}
