package common;

import lombok.ToString;

/***
 *
 * https://en.wikipedia.org/wiki/Exponential_decay
 *
 * out(t)=e^(C)*e^(-gamma*t)
 *
 * the parameters C and gamma are set from start and end values of out, i.e. outStart=out(0) and outEnd=out(timeEnd)
 */

@ToString
public class LogarithmicDecay {

    private final double outStart, outEnd, timeEnd;

    private double C, gamma;


    public LogarithmicDecay(double outStart, double outEnd, double timeEnd) {
        this.outStart = outStart;
        this.outEnd = outEnd;
        this.timeEnd = timeEnd;

        defineParameters(outStart, outEnd);
    }

    private void defineParameters(double outStart, double outEnd) {
        this.C=Math.log(outStart);
        System.out.println("Math.max(Double.MIN_VALUE, outEnd)/Math.exp(C) = " + Math.max(Double.MIN_VALUE, outEnd) / Math.exp(C));
        double minusGammaTime=Math.log(Math.max(1e-20, outEnd)/Math.exp(C));
        System.out.println("minusGammaTime = " + minusGammaTime);
        this.gamma=-minusGammaTime/this.timeEnd;
    }

    public double calcOut(double time) {

        if (time<0 || time>timeEnd) {
            throw  new IllegalArgumentException("Time outside bounds, time = "+time);
        }

        return Math.exp(C)*Math.exp(-gamma*time);
    }

}
