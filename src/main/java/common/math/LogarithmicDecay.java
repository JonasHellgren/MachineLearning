package common.math;

import lombok.ToString;

/***
 *
 * https://en.wikipedia.org/wiki/Exponential_decay
 *
 * out(t)=e^(C)*e^(-gammas*t)
 *
 * the parameters C and gammas are set from start and end values of out, i.e. outStart=out(0) and outEnd=out(timeEnd)
 */

@ToString
public class LogarithmicDecay {

    private final double outStart, outEnd, timeEnd;
    private double C, gamma;

    public LogarithmicDecay(double outStart, double outEnd, double timeEnd) {
        this.outStart = outStart;
        this.outEnd = outEnd;
        this.timeEnd = timeEnd;
        defineParameters();
    }

    private void defineParameters() {
        this.C=Math.log(outStart);
        double minusGammaTime=Math.log(Math.max(1e-20, outEnd)/Math.exp(C));
        this.gamma=-minusGammaTime/this.timeEnd;
    }

    public double calcOut(double time) {

        if (time<0 || time>timeEnd) {
            throw  new IllegalArgumentException("Time outside bounds, time = "+time);
        }

        return Math.exp(C)*Math.exp(-gamma*time);
    }

}
