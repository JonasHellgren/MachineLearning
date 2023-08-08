package multi_step_td.test_helpers;

import lombok.Builder;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;

@Builder
public record ArgumentReaderInputSetter (int posA, int posB, double socA, double socB, double record ,
                                        double checkSum) {
    public static final int TIME = 0;
    public static ArgumentReaderInputSetter of(ArgumentsAccessor args) {
        return  ArgumentReaderInputSetter.builder()
                .posA(args.getInteger(0)).posB(args.getInteger(1))
                .socA(args.getDouble(2)).socB(args.getDouble(3))
                .checkSum(args.getDouble(4))
                .build();
    }

    @NotNull
    public ChargeState createState() {
        return new ChargeState(ChargeVariables.builder()
                .posA(posA).posB(posB)
                .socA(socA).socB(socB)
                .time(TIME)
                .build());
    }
}
