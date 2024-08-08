package common.math;

import com.google.common.base.Preconditions;
import common.other.Conditionals;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.math3.util.Pair;
import safe_rl.other.SearchSettings;

/**
 * https://en.wikipedia.org/wiki/Golden-section_search
 *     Parameters:
 *     - f: a strictly unimodal function on [a, b]
 *     - a: the left endpoint of the interval
 *     - b: the right endpoint of the interval
 *     - tolerance: the precision tolerance (default: 1e-5)
 *
 *     Returns:
 *     - The x-coordinate of the minimum point of f in the interval [a, b]
 *     """
 *     invphi = (sqrt(5) - 1) / 2  # Inverse of the golden ratio

 *     while abs(b - a) > tolerance:
 *         c = b - (b - a) * invphi  # Calculate the interior point c
 *         d = a + (b - a) * invphi  # Calculate the interior point d

 *         if f(c) < f(d):
 *             # Narrow the interval to [a, d] and skip points to the right of d
 *             b = d
 *         else:
 *             # Narrow the interval to [c, b] and skip points to the left of c
 *             a = c

 *     # Return the midpoint of the final interval as the best estimate
 *     return (b + a) / 2
 *
 */

@Log
@AllArgsConstructor
public class GoldenSearcher {
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    private static final double INVPHI = 1/PHI;

    FunctionWrapperI function;
    SearchSettings settings;

    public double searchMin() {
        return goldenSectionSearch(true).getFirst();
    }

    public double searchMax() {
        return goldenSectionSearch(false).getFirst();
    }

    public Pair<Double,Double> searchMinWithFunctionValue() {
        return goldenSectionSearch(true);
    }

    public Pair<Double,Double> searchMaxWithFunctionValue() {
        return goldenSectionSearch(false);
    }


    Pair<Double, Double> goldenSectionSearch(boolean isMinSearch) {
        double fBest=isMinSearch ? Double.MAX_VALUE: -Double.MAX_VALUE;
        double a=settings.xMin();
        double b=settings.xMax();
        Preconditions.checkArgument(a<b,"Bad interval");
        double tol=settings.tol();
        double c = calcC(a, b);
        double d = calcD(a, b);

        int i=0;
        while (isTolViolated(a, b, tol) && isNofIterationsNotExceeded(i)) {
            double fC0 = function.f(c);
            double fC1 = isMinSearch? fC0 :-fC0;
            double fD0 = function.f(d);
            double fD1 = isMinSearch? fD0 :-fD0;
            log.fine("Interval, (c,d)=" + "(" + c + "," + d + ")");
            log.fine("Function values, (fC,fD)=" + "(" + fC0 + "," + fD0 + ")");
            if (fC1 < fD1) {
                b = d;
            } else {
                a = c;
            }
            log.info("New interval, (a,b)=" + "(" + a + "," + b + ")");
            c = calcC(a, b);
            d = calcD(a, b);
            i++;
            fBest=isMinSearch?Math.min(fBest,Math.min(fC0,fD0)):Math.max(fBest,Math.max(fC0,fD0));
        }

        log.info("Gold search finished in "+i+" iterations");
        Conditionals.executeIfTrue(isTolViolated(a, b, tol), () -> log.warning("Tolerance not fulfilled"));
        return Pair.create((b + a) / 2,fBest);
    }

    private static boolean isTolViolated(double a, double b, double tol) {
        return Math.abs(b - a) > tol;
    }

    private boolean isNofIterationsNotExceeded(int i) {
        return i < settings.nIterMax();
    }

    private static double calcD(double a, double b) {
        return a + INVPHI * (b - a);
    }

    private static double calcC(double a, double b) {
        return b - INVPHI * (b - a);
    }


}
