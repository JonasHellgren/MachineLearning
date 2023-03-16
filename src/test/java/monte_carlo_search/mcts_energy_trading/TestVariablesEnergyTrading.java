package monte_carlo_search.mcts_energy_trading;

import monte_carlo_tree_search.domains.energy_trading.VariablesEnergyTrading;
import org.junit.Assert;
import org.junit.Test;

public class TestVariablesEnergyTrading {

    private static final double DELTA = 0.01;

    @Test
    public void givenDefaultValues_thenCorrect() {
        VariablesEnergyTrading vars = VariablesEnergyTrading.newDefault();
        System.out.println("vars = " + vars);
        Assert.assertEquals(VariablesEnergyTrading.DEFAULT_TIME, vars.time);
        Assert.assertEquals(VariablesEnergyTrading.DEFAULT_SOE, vars.SoE, DELTA);
    }

    @Test
    public void givenValues_thenCorrect() {
        VariablesEnergyTrading vars = VariablesEnergyTrading.builder().time(0).SoE(0.7).build();
        System.out.println("vars = " + vars);
        Assert.assertEquals(0, vars.time);
        Assert.assertEquals(0.7, vars.SoE, DELTA);
    }

    @Test
    public void givenDefault_whenCopy_thenIsEqual() {
        VariablesEnergyTrading vars = VariablesEnergyTrading.newDefault();

        VariablesEnergyTrading varsCopy = vars.copy();
        System.out.println("vars = " + vars);
        System.out.println("varsCopy = " + varsCopy);

        Assert.assertEquals(VariablesEnergyTrading.DEFAULT_TIME, vars.time);
        Assert.assertEquals(VariablesEnergyTrading.DEFAULT_SOE, vars.SoE, DELTA);
        Assert.assertEquals(vars, varsCopy);
    }


}
