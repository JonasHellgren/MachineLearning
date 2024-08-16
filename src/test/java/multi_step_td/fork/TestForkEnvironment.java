package multi_step_td.fork;

import common.other.RandUtilsML;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironmentSettings;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironment;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestForkEnvironment {

    private static final double DELTA = 0.1;
    private static final StateInterface<ForkVariables> POS0 =ForkState.newFromPos(0);
    private static final StateInterface<ForkVariables> POS5 =ForkState.newFromPos(5) ;
    private static final StateInterface<ForkVariables> POS6 =ForkState.newFromPos(6) ;
    private static final StateInterface<ForkVariables> POS14 =ForkState.newFromPos(14) ;

    Function<StateInterface<ForkVariables>,Integer> getPos=(s) -> s.getVariables().position;
    EnvironmentInterface<ForkVariables> environment;
    StepReturn<ForkVariables> stepReturn;
    ForkEnvironmentSettings envSettings;

    @BeforeEach
    public void init() {
        environment=new ForkEnvironment();
        ForkEnvironment envCasted=(ForkEnvironment) environment;
        envSettings=envCasted.envSettings;
    }

    @Test
    public void whenActionIs0InState0_thenState1() {
        stepReturn=environment.step(POS0,0);
        assertEquals(1,(int) getPos.apply(stepReturn.newState));
    }

    @Test
    public void whenActionIs0InState5_thenState6() {
        stepReturn=environment.step(POS5,0);
        assertEquals(6,(int) getPos.apply(stepReturn.newState));
    }

    @Test
    public void whenActionIs0Or1InState0_thenState1() {
        stepReturn=environment.step(POS0, RandUtilsML.getRandomIntNumber(0,2));
        assertEquals(1,(int) getPos.apply(stepReturn.newState));
    }

    @Test
    public void whenActionIs0Or1InState6_thenState11() {
        stepReturn=environment.step(POS6, RandUtilsML.getRandomIntNumber(0,2));
        assertEquals(11,(int) getPos.apply(stepReturn.newState));
    }

    @Test
    public void whenActionIs0Or1InState14_thenState15AndTerminalAndRewardHell() {
        stepReturn=environment.step(POS14, RandUtilsML.getRandomIntNumber(0,2));
        System.out.println("stepReturn = " + stepReturn);
        assertEquals(15,(int) getPos.apply(stepReturn.newState));
        assertTrue(stepReturn.isNewStateTerminal);
        assertEquals(envSettings.rewardHell(),stepReturn.reward, DELTA);
    }

    @Test
    public void whenActionIs100InState5_thenThrow() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            stepReturn = environment.step(ForkState.newFromPos(100), 0);
        });
    }

}
