package policygradient.short_corridor;

import common.other.RandUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.environments.short_corridor.EnvironmentSC;
import policy_gradient_problems.environments.short_corridor.StateSC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

 class TestEnvironmentSC {

     static final int PROB_DIRECT_TO_TERMINAL = 0;
    EnvironmentSC environment;

    @BeforeEach
     void init() {
        environment=new EnvironmentSC(PROB_DIRECT_TO_TERMINAL);
    }

    @ParameterizedTest
    @CsvSource({
            "2,0, 1,-1,true,-1.0",
            "2,1, 3,1,false,-0.1",
            "3,0, 2,0,false,-0.1",
            "3,1, 4,-1,true,1.0",
            "4,0, 3,1,false,-0.1",
            "4,1, 5,1,false,-0.1",
            "5,0, 4,-1,true,1.0",
            "5,1, 6,2,false,-0.1",
            "6,0, 5,1,false,-0.1",
            "6,1, 7,-1,true,-1.0",
    })
     void whenStep_thenCorrect (ArgumentsAccessor arguments) {
        int s = arguments.getInteger(0);
        int a = arguments.getInteger(1);
        int sNew = arguments.getInteger(2);
        int sNewObserved = arguments.getInteger(3);
        boolean isTerminal= arguments.getBoolean(4);
        double r = arguments.getDouble(5);

        var sr=environment.step(StateSC.newFromRealPos(s), Action.ofInteger(a));

        assertEquals(sNew,EnvironmentSC.getPos(sr.state()));
        StateSC stateNew = (StateSC) sr.state();
        assertEquals(sNewObserved,stateNew.getVariables().posObserved());
        assertEquals(isTerminal,sr.isTerminal());
        assertEquals(r,sr.reward());
    }


    @Test
     void givenHighProbDirectToTerminal_whenStep_thenCorrect() {
        environment=new EnvironmentSC(1);
        int stateRandom= RandUtils.getRandomIntNumber(2,7);
        int actionRandom= RandUtils.getRandomIntNumber(0,2);

        var sr=environment.step(StateSC.newFromRealPos(stateRandom), Action.ofInteger(actionRandom));
        assertTrue(sr.isTerminal());

    }

}
