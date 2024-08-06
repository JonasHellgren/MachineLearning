package safe_rl.environments.trading;

import com.google.common.collect.Range;
import common.math.MathUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.environments.factories.SettingsTradingFactory;
import safe_rl.environments.trading_electricity.EnvironmentTrading;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.StateTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;
import java.util.List;
import java.util.stream.IntStream;

public class TestEnvironmentTrading5hWithFCR {

    public static final double SOC_TOL = 1e-10;
    public static final double SOC_TERMINAL_MIN = 0.5;
    public static final double DSOH_LARGE = 1e-3;
    EnvironmentTrading environmentZeroPC, environmentNonZeroPC;
    EnvironmentTrading environmentZeroPriceBatt, environmentNonZeroPriceBatt;
    StateTrading stateAllZero, stateTime3SoC0d5,stateTime2SoC0d5,stateTime4SoC0d5;
    Action actionPower1;

    @BeforeEach
    void init() {
        var settingsZeroPC = SettingsTradingFactory.new5HoursIncreasingPrice()
                .withPowerCapacityFcrRange(Range.closed(0d,0d))
                .withPriceBattery(0);
        var settingsNonZeroPC = SettingsTradingFactory.new5HoursIncreasingPrice()
                .withPowerCapacityFcrRange(Range.closed(0.1d,0.1d))
                .withPriceBattery(0).withSocTerminalMin(SOC_TERMINAL_MIN);

        var settingsZeroPriceBatt = SettingsTradingFactory.new5HoursIncreasingPrice().withPriceBattery(0);
        var settingsNonZeroPriceBatt = SettingsTradingFactory.new5HoursIncreasingPrice();
        actionPower1 =Action.ofDouble(1d);
        environmentZeroPC=new EnvironmentTrading(settingsZeroPC);
        environmentNonZeroPC=new EnvironmentTrading(settingsNonZeroPC);
        environmentZeroPriceBatt=new EnvironmentTrading(settingsZeroPriceBatt);
        environmentNonZeroPriceBatt=new EnvironmentTrading(settingsNonZeroPriceBatt);
        stateAllZero=StateTrading.allZero();
        stateTime3SoC0d5 =StateTrading.of(VariablesTrading.newTimeSoc(3,0.5));
        stateTime2SoC0d5 =StateTrading.of(VariablesTrading.newTimeSoc(2,0.5));
    }

    @Test
    void whenNonZeroPC_thenSoCSmallerOrLargerComparedToZeroPC() {
        var sr0=environmentZeroPC.step(stateAllZero, actionPower1);
        int nSocsGenerated = 100;
        List<Double> socList = IntStream.range(0, nSocsGenerated)
                .mapToObj(i -> environmentNonZeroPC.step(stateAllZero, actionPower1))
                .map(sr -> sr.state().getVariables().soc())
                .toList();
        double soc0=sr0.state().getVariables().soc();
        int nSmaller= (int) socList.stream().filter(s -> s < soc0).count();
        int nlarger= (int) socList.stream().filter(s -> s > soc0).count();
        int nEqual= (int) socList.stream().filter(s -> MathUtils.isEqualDoubles(s,soc0, SOC_TOL)).count();
        Assertions.assertEquals(nSocsGenerated,nlarger+nSmaller);
        Assertions.assertTrue(nlarger>0);
        Assertions.assertEquals(0,nEqual);
    }

    @Test  //c4 makes this fail
    void givenAboveSoCTerminal_whenNonZeroPC_thenSocFailInEndDueToPowerFCR() {
        stateTime4SoC0d5 =StateTrading.of(VariablesTrading.newTimeSoc(4,SOC_TERMINAL_MIN+1e-3));
        var sr=environmentNonZeroPC.step(stateTime4SoC0d5, Action.ofDouble(0d));
        Assertions.assertTrue(sr.isFail());
    }


    @Test
    void whenNonZeroPC_thenLargerRewardComparedToZeroPC() {
        var sr0PC = environmentZeroPC.step(stateAllZero, actionPower1);
        var srNon0PC = environmentNonZeroPC.step(stateAllZero, actionPower1);
        Assertions.assertTrue(srNon0PC.reward()>sr0PC.reward());
    }

    @Test
    void whenNonZeroPriceBatter_thenSmallerRewardComparedToZero() {
        var sr0Price = environmentZeroPriceBatt.step(stateAllZero, actionPower1);
        var srNon0Price = environmentNonZeroPriceBatt.step(stateAllZero, actionPower1);
        StateTrading state = (StateTrading) sr0Price.state();
        Assertions.assertTrue(srNon0Price.reward()<sr0Price.reward());
        Assertions.assertTrue(state.soh()<1);
        Assertions.assertTrue(state.dSohSinceStart()<0);
        Assertions.assertTrue(Math.abs(state.dSohSinceStart())<DSOH_LARGE);
    }



}
