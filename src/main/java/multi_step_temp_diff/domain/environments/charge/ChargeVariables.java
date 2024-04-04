package multi_step_temp_diff.domain.environments.charge;

import lombok.Builder;
import monte_carlo_tree_search.interfaces.StateInterface;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.StringJoiner;

import static multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas.isAnyAtNode;

@Builder
public class ChargeVariables {

    public static final double SOC = 1.0;
    public int posA, posB;
    @Builder.Default
    public double socA = SOC, socB = SOC;
    @Builder.Default
    public int time = 0;

    public ChargeVariables copy() {
        return ChargeVariables.builder()
                .posA(posA).posB(posB)
                .socA(socA).socB(socB)
                .time(time)
                .build();
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ");
        DecimalFormat formatter = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US)); //US <=> only dots
        sj.add("posReal A = " + posA).add("posReal B = " + posB)
                .add("soc A = " + formatter.format(socA)).add("soc B = " + formatter.format(socB))
                .add("time = " + time);

        ChargeState state=new ChargeState(this);
        if (isAnyAtNode.test(state,10)) {
            sj.add("---- a vehicle is at split node -------");
        }

        if (isAnyAtNode.test(state,20)) {
            sj.add("---- a vehicle is at charge que node -------");
        }
        return sj.toString();
    }


}
