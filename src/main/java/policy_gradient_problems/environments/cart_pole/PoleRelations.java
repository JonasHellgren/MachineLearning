package policy_gradient_problems.environments.cart_pole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.With;

import static common.MyFunctions.sqr2;

/**
 * TempVariables is inner record to decrease nof arguments
 */

@AllArgsConstructor
public class PoleRelations {

    ParametersPole parameters;

    @Builder
    record TempVariables(int action, double sinTheta, double cosTheta, @With double temp) {
    }

    public VariablesPole calcNew(int action, VariablesPole s) {
        var tvOld = TempVariables.builder()
                .action(action).sinTheta(Math.sin(s.angle())).cosTheta(Math.cos(s.angle())).build();
        var tv = tvOld.withTemp(getTempVariable(tvOld, s));
        double angleAcc = getAngleAcc(tv, s);
        double xAcc = getXAcc(tv, s);
        double tau = parameters.tau();
        return VariablesPole.builder()
                .angle(s.angle() + tau * s.angleDot())
                .x(s.x() + tau * s.xDot())
                .angleDot(s.angleDot() + tau * angleAcc)
                .xDot(s.xDot() + tau * xAcc)
                .nofSteps(s.nofSteps() + 1)
                .build();
    }

    private double getTempVariable(TempVariables tv, VariablesPole s) {
        double force = (tv.action == 0) ? -parameters.forceMagnitude() : parameters.forceMagnitude();
        return force + parameters.massPoleTimesLength() * sqr2.apply(s.angleDot()) * tv.sinTheta;
    }

    private double getXAcc(TempVariables tv, VariablesPole s) {
        return tv.temp - parameters.massPoleTimesLength() * s.angleDot() * tv.cosTheta / parameters.massTotal();
    }

    private double getAngleAcc(TempVariables tv, VariablesPole s) {
        double denom = parameters.length() * (4.0 / 3.0 - parameters.massPole() * sqr2.apply(tv.cosTheta) /
                parameters.massTotal());
        return (parameters.g() * tv.sinTheta - tv.cosTheta * tv.temp) / denom;
    }

}
