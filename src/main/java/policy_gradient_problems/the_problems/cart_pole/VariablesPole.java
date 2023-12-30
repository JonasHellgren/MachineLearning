package policy_gradient_problems.the_problems.cart_pole;

import lombok.Builder;
import lombok.ToString;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;

import static common.MyFunctions.sqr2;
import static common.RandUtils.getRandomDouble;

@Builder
public record VariablesPole(
                            double angle,
                            double x,
                            double angleDot,
                            double xDot,
                            int nofSteps) {

    @Builder
    record Variables(int action, double sinTheta, double cosTheta) {   //inner record to decrease nof arguments
    }

    public VariablesPole copy() {
        return VariablesPole.builder()
                .angle(angle).x(x).angleDot(angleDot).xDot(xDot).nofSteps(nofSteps)
                .build() ;
    }

    public RealVector asRealVector() {
        return new ArrayRealVector(new double[]{angle, x, angleDot, xDot});
    }

    public List<Double> asList() {
        return List.of(angle, x, angleDot, xDot);
    }


    public static VariablesPole newUprightAndStill() {
        return VariablesPole.builder()
                .angle(0).x(0).angleDot(0).xDot(0).nofSteps(0)
                .build();
    }

    public static VariablesPole newAngleAndPosRandom(ParametersPole p) {
        return VariablesPole.builder()
                .angle(getRandomDouble(-p.angleMax(),p.angleMax()))
                .x(getRandomDouble(-p.xMax(),p.xMax()))
                .angleDot(0)
                .xDot(0)
                .nofSteps(0)
                .build();
    }

    public static VariablesPole newAllRandom(ParametersPole p) {
        return VariablesPole.builder()
                .angle(getRandomDouble(-p.angleMax(),p.angleMax()))
                .x(getRandomDouble(-p.xMax(),p.xMax()))
                .angleDot(getRandomDouble(-1,1))
                .xDot(getRandomDouble(-1,1))
                .nofSteps(0)
                .build();
    }


    public VariablesPole calcNew(int action, ParametersPole parameters) {
        var variables = VariablesPole.Variables.builder()
                .action(action).sinTheta(Math.sin(angle)).cosTheta(Math.cos(angle)).build();
        double temp = getTempVariable(variables, parameters);
        double angleAcc = getAngleAcc(temp, variables, parameters);
        double xAcc = getXAcc(temp, variables, parameters);
        double tau = parameters.tau();
        return VariablesPole.builder()
                .angle(angle + tau * angleDot)
                .x(x + tau * xDot)
                .angleDot(angleDot + tau * angleAcc)
                .xDot(xDot + tau * xAcc)
                .nofSteps(nofSteps + 1)
                .build();
    }

    private double getTempVariable(VariablesPole.Variables v, ParametersPole p) {
        double force = (v.action == 0) ? -p.forceMagnitude() : p.forceMagnitude();
        return force + p.massPoleTimesLength() * sqr2.apply(angleDot) * v.sinTheta;
    }

    private double getXAcc(double temp, VariablesPole.Variables v, ParametersPole p) {
        return temp - p.massPoleTimesLength() * angleDot * v.cosTheta / p.massTotal();
    }

    private double getAngleAcc(double temp, VariablesPole.Variables v, ParametersPole p) {
        double denom = p.length() * (4.0 / 3.0 - p.massPole() * sqr2.apply(v.cosTheta) /
                p.massTotal());
        return (p.g() * v.sinTheta - v.cosTheta * temp) / denom;
    }

    @Override
    public String toString() {
        return asList().toString();
    }

}
