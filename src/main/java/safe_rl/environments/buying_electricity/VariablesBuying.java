package safe_rl.environments.buying_electricity;

import common.math.MathUtils;
import lombok.Builder;
import lombok.With;
import common.other.NumberFormatterUtil;

@Builder
public record VariablesBuying (
        @With double time,
        @With double soc,
        @With double socStart
) {

    public static final double TOL = 1e-5;

    public static VariablesBuying newZero() {
        return new VariablesBuying(0d,0.0,0.0);
    }

    public static VariablesBuying newSoc(double soc) {
        return VariablesBuying.newTimeSoc(0d,soc);
    }

    public static VariablesBuying newTimeSoc(double time, double soc) {
        return new VariablesBuying(time,soc,soc);
    }

    public VariablesBuying copy() {
        return VariablesBuying.builder()
                .time(time).soc(soc).socStart(socStart)
                .build();
    }

    public int hashDiscrete() {
        return Integer.hashCode((int) time);
    }

    public boolean equalDiscrete(VariablesBuying v) {
            return MathUtils.isEqualDoubles(time,v.time, TOL);
    }

    @Override
    public String toString() {
        var f= NumberFormatterUtil.formatterOneDigit;
        return "[t = "+ time+", soc = "+ f.format(soc)+", socStart = "+ socStart+"]";
    }


}
