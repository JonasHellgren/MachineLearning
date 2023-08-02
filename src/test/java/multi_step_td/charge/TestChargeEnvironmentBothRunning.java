package multi_step_td.charge;

import lombok.Builder;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class TestChargeEnvironmentBothRunning {
    public static final double SOC_INIT = 0.5;
    public static final int TIME = 10;

    EnvironmentInterface<ChargeVariables> environment;
    ChargeEnvironment environmentCasted;
    ChargeEnvironmentSettings settings;

    @Builder
    record ArgumentReader(int posA, int posB, int action,
                          boolean isFailState, int posANew, int posBNew) {

        public static ArgumentReader of(ArgumentsAccessor args) {
            return  ArgumentReader.builder()
                    .posA(args.getInteger(0)).posB(args.getInteger(1)).action(args.getInteger(2))
                    .isFailState(args.getBoolean(3)).posANew(args.getInteger(4)).posBNew(args.getInteger(5))
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
            "0,1,0, false,1,2","0,1,1, false,1,2","0,1,3, false,1,2",
            "7,8,0, false,7,9","7,8,3, false,8,9",
            "10,11,0, false,11,12","10,11,2, false,20,12",
            "20,40,3, true,30,50","20,40,0, false,20,50",
            "11,22,0, false,12,22","11,22,3, true,12,12",
            "19,29,0, false,0,29"
    })

    public void whenNoObstacle_thenCorrectNewPosAndSoCChange(ArgumentsAccessor arguments) {
        ArgumentReader reader= ArgumentReader.of(arguments);
        StateInterface<ChargeVariables> state = setState(reader);
        environmentCasted.setObstacle(false);
        StepReturn<ChargeVariables> stepReturn=environment.step(state,reader.action);
        assertStepReturn(reader, stepReturn);
    }

    @ParameterizedTest
    @CsvSource({
            "0,1,0, false,1,2","0,1,1, false,1,2","0,1,3, false,1,2",
            "7,8,0, false,7,8","7,8,3, true,8,8"
    })

    public void whenObstacle_thenCorrectNewPosAndSoCChange(ArgumentsAccessor arguments) {
        ArgumentReader reader= ArgumentReader.of(arguments);
        StateInterface<ChargeVariables> state = setState(reader);
        environmentCasted.setObstacle(true);
        StepReturn<ChargeVariables> stepReturn=environment.step(state,reader.action);
        assertStepReturn(reader, stepReturn);
    }


    private static void assertStepReturn(ArgumentReader reader, StepReturn<ChargeVariables> stepReturn) {
        assertEquals(reader.isFailState, stepReturn.isNewStateFail);
        assertEquals(reader.posANew, stepReturn.newState.getVariables().posA);
        assertEquals(reader.posBNew, stepReturn.newState.getVariables().posB);

    }

    @NotNull
    private static StateInterface<ChargeVariables> setState(ArgumentReader reader) {
        return new ChargeState(ChargeVariables.builder()
                .posA(reader.posA).posB(reader.posB)
                .socA(SOC_INIT).socB(SOC_INIT)
                .time(TIME)
                .build());
    }

}
