package policy_gradient_problems.the_problems.cart_pole;

import lombok.Builder;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import static common.MyFunctions.sqr2;
import static common.RandUtils.randomNumberBetweenZeroAndOne;

@Builder
public record StatePole(
        double angle,
        double x,
        double angleDot,
        double xDot,
        int nofSteps) {

    @Builder
    record Variables(int action, double sinTheta, double cosTheta) {   //inner record to decrease nof arguments
    }

    public StatePole copy() {
        return StatePole.builder()
                .angle(angle).x(x).angleDot(angleDot).xDot(xDot).nofSteps(nofSteps)
                .build();
    }

    public RealVector asRealVector() {
        return new ArrayRealVector(new double[]{angle, x, angleDot, xDot});
    }

    public static StatePole newUprightAndStill() {
        return StatePole.builder()
                .angle(0).x(0).angleDot(0).xDot(0).nofSteps(0)
                .build();
    }

    public static StatePole newAngleAndPosRandom(ParametersPole p) {
        return StatePole.builder()
                .angle(-p.angleMax() + randomNumberBetweenZeroAndOne() * 2 * p.angleMax())
                .x(-p.xMax() + randomNumberBetweenZeroAndOne() * 2 * p.xMax())
                .angleDot(0)
                .xDot(0)
                .nofSteps(0)
                .build();
    }


    public StatePole calcNew(int action, ParametersPole parameters) {
        var variables = Variables.builder()
                .action(action).sinTheta(Math.sin(angle)).cosTheta(Math.cos(angle)).build();
        double temp = getTempVariable(variables, parameters);
        double angleDotAcc = getAngleAcc(temp, variables, parameters);
        double xDotAcc = getXAcc(temp, variables, parameters);
        double tau = parameters.tau();
        return StatePole.builder()
                .angle(angle + tau * angleDot)
                .x(x + tau * xDot)
                .angleDot(angleDot + tau * angleDotAcc)
                .xDot(xDot + tau * xDotAcc)
                .nofSteps(nofSteps + 1)
                .build();
    }

    private double getTempVariable(Variables v, ParametersPole p) {
        double force = (v.action == 0) ? -p.forceMagnitude() : p.forceMagnitude();
        return force + p.massPoleTimesLength() * sqr2.apply(angleDot) * v.sinTheta;
    }

    private double getXAcc(double temp, Variables v, ParametersPole p) {
        return temp - p.massPoleTimesLength() * angleDot * v.cosTheta / p.massTotal();
    }

    private double getAngleAcc(double temp, Variables v, ParametersPole p) {
        double denom = p.length() * (4.0 / 3.0 - p.massPole() * sqr2.apply(v.cosTheta) /
                p.massTotal());
        return (p.g() * v.sinTheta - v.cosTheta * temp) / denom;
    }


}
