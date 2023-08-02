package multi_step_td.charge;

import lombok.Builder;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.*;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/***
 * action=0 -> command A = 0, action=2 -> command A = 1
 */

public class TestChargeEnvironmentBTrapped {

    public static final double SOC_B = 0.9;
    public static final int TRAP_POS = 29, TIME = 10;

    EnvironmentInterface<ChargeVariables> environment;
    ChargeEnvironment environmentCasted;
    ChargeEnvironmentSettings settings;

    @Builder
    record ArgumentReader(int posA, double socA, int action,
                          boolean isFailState, int posANew, boolean isSoCIncreased) {

        public static ArgumentReader of(ArgumentsAccessor args) {
            return  ArgumentReader.builder()
                    .posA(args.getInteger(0)).socA(args.getDouble(1)).action(args.getInteger(2))
                    .isFailState(args.getBoolean(3)).posANew(args.getInteger(4)).isSoCIncreased(args.getBoolean(5))
                    .build();
        }
    }

    @BeforeEach
    public void init() {
        settings=ChargeEnvironmentSettings.newDefault();
        environment = new ChargeEnvironment(settings);
        environmentCasted=(ChargeEnvironment) environment;
    }

    @ParameterizedTest
    @CsvSource({
            "0,0.9,0, false,1,false","0,0.9,2, false,1,false",
            "7,0.9,0, false,7,false","7,0.9,2, false,8,false",
            "10,0.9,0, false,11,false","10,0.9,2, false,20,false","10,0.2,1, true,11,false",
            "20,0.9,0, false,20,false","20,0.9,2, false,30,true",  //moving to charge node <=> soc increase
            "30,0.9,0, false,40,true","30,0.9,2, false,40,true",
            "32,0.9,0, false,22,false","32,0.9,2, false,22,false",  //moving from charge node <=> no soc increase
            "22,0.9,0, false,22,false", "22,0.9,2, false,12,false"
    })

    public void whenNoObstacle_thenCorrectNewPosAndSoCChange(ArgumentsAccessor arguments) {
        ArgumentReader reader=ArgumentReader.of(arguments);
        StateInterface<ChargeVariables> state = setState(reader);
        environmentCasted.setObstacle(false);
        StepReturn<ChargeVariables> stepReturn=environment.step(state,reader.action);
        System.out.println("stepReturn.newState = " + stepReturn.newState);
        assertStepReturn(reader, stepReturn);
    }


    @ParameterizedTest
    @CsvSource({
            "0,0.9,0, false,1,false",
            "7,0.9,0, false,7,false","7,0.9,2, false,8,false",
            "8,0.9,0, false,8,false", "8,0.9,2, false,8,false"  //obstacle <=> trapped
    })

    public void whenObstacle_thenCorrectNewPosAndSoCChange(ArgumentsAccessor arguments) {
        ArgumentReader reader=ArgumentReader.of(arguments);
        StateInterface<ChargeVariables> state = setState(reader);
        environmentCasted.setObstacle(true);
        StepReturn<ChargeVariables> stepReturn=environment.step(state,reader.action);
        System.out.println("stepReturn.newState = " + stepReturn.newState);
        assertStepReturn(reader, stepReturn);
    }

    private static void assertStepReturn(ArgumentReader reader, StepReturn<ChargeVariables> stepReturn) {
        assertEquals(reader.isFailState, stepReturn.isNewStateFail);
        assertEquals(reader.posANew, stepReturn.newState.getVariables().posA);
        boolean isNewSoCALarger = stepReturn.newState.getVariables().socA > reader.socA;
        assertEquals(reader.isSoCIncreased, isNewSoCALarger);
    }

    @NotNull
    private static StateInterface<ChargeVariables> setState(ArgumentReader reader) {
        return new ChargeState(ChargeVariables.builder()
                .posA(reader.posA).posB(TRAP_POS)
                .socA(reader.socA).socB(SOC_B)
                .time(TIME)
                .build());
    }


}
