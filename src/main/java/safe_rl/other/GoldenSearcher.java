package safe_rl.other;

import com.google.common.base.Preconditions;
import common.other.Conditionals;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

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
        return goldenSectionSearchMax(true);
    }

    public double searchMax() {
        return goldenSectionSearchMax(false);
    }

    double goldenSectionSearchMax(boolean isMinSearch) {
        double a=settings.xMin();
        double b=settings.xMax();
        double tol=settings.tol();
        Preconditions.checkArgument(a<b,"Bad interval");

        double c = calcC(a, b);
        double d = calcD(a, b);

        int i=0;
        while (isTolViolated(a, b, tol) && isNofIterationsNotExceeded(i)) {
            double fC = isMinSearch?function.f(c):-function.f(c);
            double fD = isMinSearch?function.f(d):-function.f(d);
            if (fC < fD) {
                b = d;
            } else {
                a = c;
            }
            log.fine("New interval, (a,b)=" + "(" + a + "," + b + ")");
            c = calcC(a, b);
            d = calcD(a, b);
            i++;
        }

        log.info("Gold search finished in "+i+" iterations");
        Conditionals.executeIfTrue(isTolViolated(a, b, tol), () -> log.warning("Tolerance not fulfilled"));
        return (b + a) / 2;
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
