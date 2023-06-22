package multi_step_temp_diff.environments;

import lombok.Builder;

import java.util.function.Function;

@Builder
public class ChargeVariables {

    public int posA, posB;
    public double socA, socB;
    @Builder.Default
    public int time=0;


    public ChargeVariables copy() {
        return  ChargeVariables.builder()
                .posA(posA).posB(posB)
                .socA(socA).socB(socB)
                .time(time)
                .build();
    }

}
