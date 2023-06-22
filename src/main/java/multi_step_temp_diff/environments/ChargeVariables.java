package multi_step_temp_diff.environments;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class ChargeVariables {

    public static final double SOC = 1.0;
    public int posA, posB;
    @Builder.Default
    public double socA=SOC, socB= SOC;
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
