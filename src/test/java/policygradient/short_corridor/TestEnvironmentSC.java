package policygradient.short_corridor;

import common.RandUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import policy_gradient_problems.short_corridor.EnvironmentSC;
import policy_gradient_problems.short_corridor.StepReturnSC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestEnvironmentSC {

    public static final int PROB_DIRECT_TO_TERMINAL = 0;
    EnvironmentSC environment;

    @BeforeEach
    public void init() {
        environment=new EnvironmentSC(PROB_DIRECT_TO_TERMINAL);
    }

    @ParameterizedTest
    @CsvSource({
            "2,0, 1,0,true,-1.0",
            "2,1, 3,2,false,-0.1",
            "3,0, 2,1,false,-0.1",
            "3,1, 4,0,true,1.0",
            "4,0, 3,2,false,-0.1",
            "4,1, 5,2,false,-0.1",
            "5,0, 4,0,true,1.0",
            "5,1, 6,3,false,-0.1",
            "6,0, 5,2,false,-0.1",
            "6,1, 7,0,true,-1.0",

    })
    public void whenStep_thenCorrect (ArgumentsAccessor arguments) {
        int s = arguments.getInteger(0);
        int a = arguments.getInteger(1);
        int sNew = arguments.getInteger(2);
        int sNewObserved = arguments.getInteger(3);
        boolean isTerminal= arguments.getBoolean(4);
        double r = arguments.getDouble(5);

        StepReturnSC sr=environment.step(s,a);

        assertEquals(sNew,sr.state());
        assertEquals(sNewObserved,sr.observedState());
        assertEquals(isTerminal,sr.isTerminal());
        assertEquals(r,sr.reward());
    }


    @Test
    public void givenHighProbDirectToTerminal_whenStep_thenCorrect() {
        environment=new EnvironmentSC(1);
        int stateRandom= RandUtils.getRandomIntNumber(2,7);
        int actionRandom= RandUtils.getRandomIntNumber(0,2);

        StepReturnSC sr=environment.step(stateRandom,actionRandom);
        System.out.println("stateRandom = " + stateRandom+", sr = " + sr);

        assertTrue(sr.isTerminal());

    }

}
