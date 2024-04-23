package common.math;

import lombok.Builder;

/***
 *  Argument grad0 is potentially changed
 *  It is always bounded to [gradMin,gradMax]
 *  If motivated the bound can be more drastic
 *  If for example value is 10 and valueMax is 11, then the upper gradient bound is 1
 *  If value is -10 and valueMax is -11, then the upper gradient bound is -11-10=-1
 *  When value is below valueMin or above valueMax then gradient is modified to zero
 */

@Builder
public class SafeGradientClipper {

    double valueMax;
    double valueMin;
    double gradMax;
    double gradMin;

    public double modify(double grad0, double value) {
        double deltaValueMax=valueMax-value;
        double deltaValueMin=valueMin-value;
        double gMin= (value<=valueMin) ? 0 : Math.max(gradMin,deltaValueMin);
        double gMax= (value>=valueMax) ? 0 : Math.min(gradMax,deltaValueMax);
        return MathUtils.clip(grad0, gMin,gMax);
    }
}
