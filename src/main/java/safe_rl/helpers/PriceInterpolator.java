package safe_rl.helpers;

import common.list_arrays.ArrayUtil;
import common.list_arrays.ListUtils;
import common.math.Interpolator;
import common.math.MathUtils;
import lombok.Getter;

import java.util.Arrays;

public class PriceInterpolator {

    Interpolator interpolator;

    @Getter
    double[] times;

    double timeMin;
    double timeMax;

    public PriceInterpolator(double dt, double[] prices) {
        int nPrices= prices.length;
        times= ArrayUtil.createArrayInRange(0,dt,dt*(nPrices-1));
        this.interpolator=new Interpolator(times,prices);
        this.timeMin= Arrays.stream(times).min().orElseThrow();
        this.timeMax= Arrays.stream(times).max().orElseThrow();
    }

    public double priceAtTime(double time) {
        double timeClipped= MathUtils.clip(time,timeMin,timeMax);
        return interpolator.interpLinear(new double[]{timeClipped})[0];
    }

}
