package safe_rl.environments.trading;

import lombok.Builder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.environment.value_objects.StepReturn;
import safe_rl.environments.trading_electricity.EnvironmentTrading;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.StateTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;

class TestEnvironmentTrading5h {
     public static final double SOC_TOL = 1e-5;
     public static final double REWARD_TOL = 1e-5;

    @Builder
    record ParArg(
            double power,
            double timeNew,
            double socNew,
            double reward,
            boolean isFail
    )  {

        public static TestEnvironmentTrading5h.ParArg newOf(ArgumentsAccessor arguments) {
            return TestEnvironmentTrading5h.ParArg.builder()
                    .power(arguments.getDouble(0))
                    .timeNew(arguments.getDouble(1))
                    .socNew(arguments.getDouble(2))
                    .reward(arguments.getDouble(3))
                    .isFail(arguments.getBoolean(4))
                    .build();
        }
    }

    EnvironmentTrading environment;
    SettingsTrading settingsZeroPC;
    StateTrading stateAllZero, stateTime3SoC0d5,stateTime2SoC0d5,stateTime4SoC0d5;

    @BeforeEach
    void init() {
        settingsZeroPC = SettingsTrading.new5HoursIncreasingPrice()
                .withPowerCapacityFcr(0).withPriceBattery(0);
        stateAllZero=StateTrading.allZero();
        stateTime3SoC0d5 =StateTrading.of(VariablesTrading.newTimeSoc(3,0.5));
        stateTime2SoC0d5 =StateTrading.of(VariablesTrading.newTimeSoc(2,0.5));
        stateTime4SoC0d5 =StateTrading.of(VariablesTrading.newTimeSoc(4,0.5));
    }

     @ParameterizedTest
     @CsvSource({
             "0,     1,0.0,0,false",   //power,    time,soc,reward,isFail
             "1,     1,0.1,-0.1,false",
             "3.0,     1,0.3,-0.3,false",
             "3.1,     1,0.31,-10.31,true",  //power exceeded
             "-3.0,    1,-0.3,-9.7,true",  //socMin exceeded
             "-3.1,    1,-0.31,-9.69,true",  //power exceeded
     })
    void givenZeroPC_whenTime0_thenCorrect(ArgumentsAccessor arguments) {
        environment=new EnvironmentTrading(settingsZeroPC);
         var pa= ParArg.newOf(arguments);
         var sr=environment.step(stateAllZero, Action.ofDouble(pa.power));
         assertParamTest(pa, sr);
    }

    @ParameterizedTest
    @CsvSource({
            "0,     3,0.5,0,false",   //power,    time,soc,reward,isFail
            "-2,      3,0.3,-9.4,true",   //  ...>socTerminalMin violated already at time 2
    })
    void givenZeroPC_whenTime2_thenCorrect(ArgumentsAccessor arguments) {
        environment=new EnvironmentTrading(settingsZeroPC.withSocTerminalMin(0.9));
        var pa= ParArg.newOf(arguments);
        var sr=environment.step(stateTime2SoC0d5, Action.ofDouble(pa.power));
        assertParamTest(pa, sr);
    }

    @ParameterizedTest
    @CsvSource({
            "0,     4,0.5,0,false",   //power,    time,soc,reward,isFail
            "1,     4,0.6,-0.4,false",
            "3.0,     4,0.8,-1.2,false",
            "3.1,     4,0.81,-11.24,true",  //power exceeded
            "-3.0,    4,0.2,-8.8,true",  //socMin exceeded
            "-3.1,    4,0.19,-8.76,true",  //power exceeded
    })
    void givenZeroPC_whenTime3_thenCorrect(ArgumentsAccessor arguments) {
        environment=new EnvironmentTrading(settingsZeroPC);
        var pa= ParArg.newOf(arguments);
        var sr=environment.step(stateTime3SoC0d5, Action.ofDouble(pa.power));
        assertParamTest(pa, sr);
    }

    @Test
    void givenZeroPC_whenTimeEnd_thenCorrect() {
        environment=new EnvironmentTrading(settingsZeroPC);
        var sr=environment.step(stateTime4SoC0d5, Action.ofDouble(0d));
        Assertions.assertTrue(sr.isTerminal());
        Assertions.assertFalse(sr.isFail());
    }

    private static void assertParamTest(ParArg pa, StepReturn<VariablesTrading> sr) {
        Assertions.assertEquals(pa.timeNew, sr.state().getVariables().time());
        Assertions.assertEquals(pa.socNew, sr.state().getVariables().soc(), SOC_TOL);
        Assertions.assertEquals(pa.reward, sr.reward(), REWARD_TOL);
        Assertions.assertEquals(pa.isFail, sr.isFail());
    }

}
