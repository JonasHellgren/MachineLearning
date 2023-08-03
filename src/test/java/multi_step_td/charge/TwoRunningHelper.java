package multi_step_td.charge;

import lombok.Builder;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Builder
public record TwoRunningHelper(int posA, int posB, double socA, double socB,
                               boolean isFailState, int posANew, int posBNew) {

    public static final int TIME = 0;

    public static TwoRunningHelper of(ArgumentsAccessor args) {
        return  TwoRunningHelper.builder()
                .posA(args.getInteger(0)).posB(args.getInteger(1))
                .socA(args.getDouble(2)).socB(args.getDouble(3))
                .isFailState(args.getBoolean(4)).posANew(args.getInteger(5)).posBNew(args.getInteger(6))
                .build();
    }

    static void assertAction(TwoRunningHelper reader, StepReturn<ChargeVariables> stepReturn) {
        assertEquals(reader.isFailState, stepReturn.isNewStateFail);
        assertEquals(reader.posANew, stepReturn.newState.getVariables().posA);
        assertEquals(reader.posBNew, stepReturn.newState.getVariables().posB);
    }

    @NotNull
    static StateInterface<ChargeVariables> setState(TwoRunningHelper reader) {
        return new ChargeState(ChargeVariables.builder()
                .posA(reader.posA).posB(reader.posB)
                .socA(reader.socA).socB(reader.socB)
                .time(TIME)
                .build());
    }
}
