package monte_carlo_search.mcts_energy_trading;

import monte_carlo_tree_search.models_and_support_classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.energy_trading.ActionEnergyTrading;
import monte_carlo_tree_search.domains.energy_trading.EnvironmentEnergyTrading;
import monte_carlo_tree_search.domains.energy_trading.StateEnergyTrading;
import monte_carlo_tree_search.domains.energy_trading.VariablesEnergyTrading;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestEnvironmentEnergyTrading {

    private static final double DELTA = 0.001;
    EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment;
    StateInterface<VariablesEnergyTrading> state;

    @Before
    public void init() {
        environment=EnvironmentEnergyTrading.newDefault();
        state= StateEnergyTrading.newDefault();
    }

    @Test
    public void givenDefault_whenStep0_thenTimeIncreasesSameSoE() {
        StepReturnGeneric<VariablesEnergyTrading> sr=environment.step(ActionEnergyTrading.newValue(0),state);
        System.out.println("sr.newState = " + sr.newState);
        Assert.assertEquals(1,sr.newState.getVariables().time);
        Assert.assertEquals(VariablesEnergyTrading.DEFAULT_SOE,sr.newState.getVariables().SoE, DELTA);
        Assert.assertEquals(0,sr.reward,DELTA);
        Assert.assertFalse(sr.isFail);
        Assert.assertFalse(sr.isTerminal);
    }

    @Test
    public void givenDefault_whenStep1_thenTimeIncreasesIncreasedSoE() {
        StepReturnGeneric<VariablesEnergyTrading> sr = environment.step(ActionEnergyTrading.newValue(1), state);
        System.out.println("sr.newState = " + sr.newState);
        Assert.assertEquals(1, sr.newState.getVariables().time);
        Assert.assertTrue(sr.newState.getVariables().SoE>VariablesEnergyTrading.DEFAULT_SOE);
    }


    @Test
    public void givenDefault_whenStep2and1_thenStepLargerIncreaseSoE() {
        StepReturnGeneric<VariablesEnergyTrading> sr1 = environment.step(ActionEnergyTrading.newValue(1), state);
        StepReturnGeneric<VariablesEnergyTrading> sr2 = environment.step(ActionEnergyTrading.newValue(2), state);

        System.out.println("sr1 = " + sr1);
        System.out.println("sr2 = " + sr2);

        Assert.assertTrue(sr2.newState.getVariables().SoE>sr1.newState.getVariables().SoE);
        Assert.assertTrue(sr1.reward<0);
        Assert.assertTrue(sr2.reward<0);

    }

    @Test
    public void givenLowSoEState_whenStepMinus2_thenFailSoE() {
        state= StateEnergyTrading.newFromVariables(VariablesEnergyTrading.builder()
                .SoE(EnvironmentEnergyTrading.SOE_MIN+DELTA).build());
        StepReturnGeneric<VariablesEnergyTrading> sr = environment.step(ActionEnergyTrading.newValue(-2), state);

        System.out.println("sr = " + sr);

        Assert.assertTrue(sr.isFail);
        Assert.assertTrue(sr.isTerminal);
    }


    @Test
    public void givenHighSoEState_whenStepPlus2_thenFailSoE() {
        state= StateEnergyTrading.newFromVariables(VariablesEnergyTrading.builder()
                .SoE(EnvironmentEnergyTrading.SOE_MAX+DELTA).build());
        StepReturnGeneric<VariablesEnergyTrading> sr = environment.step(ActionEnergyTrading.newValue(2), state);

        System.out.println("sr = " + sr);

        Assert.assertTrue(sr.isFail);
        Assert.assertTrue(sr.isTerminal);
    }

    @Test
    public void givenTimeEnd_whenStep_thenTerminalNonFail() {
        state= StateEnergyTrading.newFromVariables(VariablesEnergyTrading.builder()
                .time(EnvironmentEnergyTrading.MAX_TIME).build());
        StepReturnGeneric<VariablesEnergyTrading> sr = environment.step(ActionEnergyTrading.newValue(0), state);

        System.out.println("sr = " + sr);

        Assert.assertFalse(sr.isFail);
        Assert.assertTrue(sr.isTerminal);
    }

    @Test
    public void givenTimeEndLowSoE_whenStepNegPower_thenTerminalAndFail() {
        state= StateEnergyTrading.newFromVariables(VariablesEnergyTrading.builder()
                .time(EnvironmentEnergyTrading.MAX_TIME)
                .SoE(EnvironmentEnergyTrading.SOE_MIN_END+DELTA)
                .build());
        StepReturnGeneric<VariablesEnergyTrading> sr = environment.step(ActionEnergyTrading.newValue(-2), state);

        System.out.println("sr = " + sr);

        Assert.assertTrue(sr.isFail);
        Assert.assertTrue(sr.isTerminal);
    }

    @Test
    public void givenTimeEndHighSoE_whenStepSmallNegPower_thenTerminalAndNoFailAndPositiveReward() {
        state= StateEnergyTrading.newFromTimeAndSoE(EnvironmentEnergyTrading.MAX_TIME,0.7);
        StepReturnGeneric<VariablesEnergyTrading> sr = environment.step(ActionEnergyTrading.newValue(-1), state);
        System.out.println("sr = " + sr);
        Assert.assertFalse(sr.isFail);
        Assert.assertTrue(sr.isTerminal);
        Assert.assertTrue(sr.reward>0);
    }

    @Test
    public void givenTimeEndHighSoE_whenStep_thenBestIsSellLittle() {
        state= StateEnergyTrading.newFromTimeAndSoE(EnvironmentEnergyTrading.MAX_TIME,0.7);
        StepReturnGeneric<VariablesEnergyTrading> srSell2 = environment.step(ActionEnergyTrading.newValue(-2), state);
        StepReturnGeneric<VariablesEnergyTrading> srSell1 = environment.step(ActionEnergyTrading.newValue(-1), state);
        StepReturnGeneric<VariablesEnergyTrading> srBuy2 = environment.step(ActionEnergyTrading.newValue(2), state);
        StepReturnGeneric<VariablesEnergyTrading> srBuy1 = environment.step(ActionEnergyTrading.newValue(1), state);


        System.out.println("srSell2 = " + srSell2);
        System.out.println("srSell1 = " + srSell1);
        System.out.println("srBuy2 = " + srBuy2);
        System.out.println("srBuy1 = " + srBuy1);

        Assert.assertTrue(srSell2.isFail);
        Assert.assertTrue(srSell2.reward<srSell1.reward);
        Assert.assertTrue(srSell1.reward>srBuy1.reward);

    }

}
