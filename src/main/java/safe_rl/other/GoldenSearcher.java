package safe_rl.other;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

@Log
@AllArgsConstructor
public class GoldenSearcher {
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    private static final double RESPHI = 2 - PHI;

    FunctionWrapperI function;
    SearchSettings settings;

    public double search() {
        return goldenSectionSearchMax(settings.xMin(),settings.xMax(), settings.tol());
    }

    double goldenSectionSearchMax(double a, double b, double tol) {
        Preconditions.checkArgument(a<b,"Bad interval");

        log.info("First interval, (a,b)=" + "(" + a + "," + b + ")");

        double c = a + RESPHI * (b - a);  //0+0.4*20=8
        double d = b - RESPHI * (b - a);  //22-0.4*20=14

        System.out.println("RESPHI = " + RESPHI);

        while (Math.abs(b - a) > tol) {
            Preconditions.checkArgument(c<d,"Bad interval"+", c="+c+", d="+d);

            double fC = function.f(c);
            double fD = function.f(d);
            log.info("c = " + c+", fC = " + fC);
            log.info("d = " + d+", fD = " + fD);
            if (fC > fD) {
                b = d;
            } else {
                a = c;
            }
            log.info("New interval, (a,b)=" + "(" + a + "," + b + ")");

            c = a+ RESPHI * (b - a);
            d = b- RESPHI * (b - a);
        }

        return (b + a) / 2;
    }

}
