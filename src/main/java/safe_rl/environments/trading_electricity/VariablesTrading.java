package safe_rl.environments.trading_electricity;

import common.math.MathUtils;
import common.other.NumberFormatterUtil;
import lombok.Builder;
import lombok.With;

import java.text.DecimalFormat;

import static common.other.NumberFormatterUtil.SYMBOLS;

@Builder
public record VariablesTrading(
        @With double time,
        @With double soc,
        @With double socStart,
        @With double soh,
        @With double sohStart
) {

    public static final double TOL = 1e-5;
    public static final double SOC_DUMMY = 0.0;

    public static VariablesTrading newZeroSoCFullSoh() {
        return new VariablesTrading(0d,0.0,0.0,1.0,1.0);
    }

    public static VariablesTrading newSoc(double soc) {
        return VariablesTrading.newTimeSoc(0d,soc);
    }

    public static VariablesTrading newTimeSoc(double time, double soc) {
        return new VariablesTrading(time,soc,soc,1.0,1.0);
    }

    public static VariablesTrading newTime(double time) {
        return new VariablesTrading(time, SOC_DUMMY,SOC_DUMMY,1.0,1.0);
    }

    public  VariablesTrading copyWithDtimeDsocDsoh(double dt,double dSoc, double dSoh) {
        return new VariablesTrading(time+dt, soc+dSoc,socStart,soh+dSoh,sohStart);
    }

    public VariablesTrading copy() {
        return VariablesTrading.builder()
                .time(time).soc(soc).socStart(socStart).soh(soh).sohStart(sohStart)
                .build();
    }

    public int hashDiscrete() {
        return Integer.hashCode((int) time);
    }

    public boolean equalDiscrete(VariablesTrading v) {
            return MathUtils.isEqualDoubles(time,v.time, TOL);
    }

    @Override
    public String toString() {
        var f2= NumberFormatterUtil.formatterTwoDigits;
        var f5 = new DecimalFormat("#.#####", SYMBOLS);
        return "[t = "+ time+", soc = "+ f2.format(soc)+", dSoh="+f5.format(sohStart-soh)+"]";
    }


}
