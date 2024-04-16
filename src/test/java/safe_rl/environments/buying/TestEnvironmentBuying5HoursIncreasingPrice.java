package safe_rl.environments.buying;

import lombok.Builder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.environments.buying_electricity.*;

public class TestEnvironmentBuying5HoursIncreasingPrice {

    public static final double REWARD_TOL = 1e-5;

    @Builder
    record ParArg(
            double power,
            double timeNew,
            double socNew,
            double reward,
            boolean isFail
    )  {

        public static  ParArg newOf(ArgumentsAccessor arguments) {
            return ParArg.builder()
                    .power(arguments.getDouble(0))
                    .timeNew(arguments.getDouble(1))
                    .socNew(arguments.getDouble(2))
                    .reward(arguments.getDouble(3))
                    .isFail(arguments.getBoolean(4))
                    .build();
        }
    }

    public static final double SOC_TOL = 1e-5;
    EnvironmentBuying environment;
    StateBuying stateAllZero;
    StateBuying stateSoC0d9;
    StateBuying stateTime4;


    @BeforeEach
    void init() {
        environment=new EnvironmentBuying(BuySettings.new5HoursIncreasingPrice());
        stateAllZero=StateBuying.newZero();
        stateSoC0d9=StateBuying.of(VariablesBuying.builder().soc(0.9).build());
        stateTime4 =StateBuying.of(VariablesBuying.builder().soc(0.1).time(4.0).build());
    }

    @ParameterizedTest
    @CsvSource({
            "0,     1,0.0,0,false",
            "1,     1,0.1,-1,false",
            "3.0,     1,0.3,-3,false",
            "3.1,     1,0.31,-13.1,true",  //power exceeded
            "-1,    1,-0.1,-10,true"})
    void givenZeroState_thenCorrect(ArgumentsAccessor arguments) {
        var pa=ParArg.newOf(arguments);
        var sr=environment.step(stateAllZero, Action.ofDouble(pa.power));
        assertParamTest(pa, sr);
    }

    @ParameterizedTest
    @CsvSource({
            "0,     1,0.9,0,false",
            "1,     1,1.0,-1,false",
            "2.0,     1,1.1,-12,true",
            "2.1,     1,1.11,-12.1,true",
            "-1,    1,0.8,-10.0,true"})
    void givenSoC0d9State_thenCorrect(ArgumentsAccessor arguments) {
        var pa=ParArg.newOf(arguments);
        var sr=environment.step(stateSoC0d9, Action.ofDouble(pa.power));
        assertParamTest(pa, sr);
    }

    @ParameterizedTest
    @CsvSource({
            "0,     5,0.0,10,false",   //time end -> soc <- socStart, high reward
            "1,     5,0.0,10,false"})  //power does not matter in end step
    void givenTime4State_thenCorrect(ArgumentsAccessor arguments) {
        var pa=ParArg.newOf(arguments);
        var sr=environment.step(stateTime4, Action.ofDouble(pa.power));
        System.out.println("sr = " + sr);
        assertParamTest(pa, sr);
    }


    private static void assertParamTest(ParArg pa, StepReturn<VariablesBuying> sr) {
        Assertions.assertEquals(pa.timeNew, sr.state().getVariables().time());
        Assertions.assertEquals(pa.socNew, sr.state().getVariables().soc(), SOC_TOL);
        Assertions.assertEquals(pa.reward, sr.reward(), REWARD_TOL);
        Assertions.assertEquals(pa.isFail, sr.isFail());
    }

}
