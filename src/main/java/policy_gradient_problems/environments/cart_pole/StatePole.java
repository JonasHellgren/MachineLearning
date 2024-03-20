package policy_gradient_problems.environments.cart_pole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.domain.abstract_classes.StateI;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class StatePole implements StateI<VariablesPole> {

    VariablesPole variables;

    @Builder
    public StatePole(double angle,
                     double x,
                     double angleDot,
                     double xDot,
                     int nofSteps
    ) {
        variables = VariablesPole.builder().angle(angle).x(x).angleDot(angleDot).xDot(xDot).nofSteps(nofSteps).build();
    }

    public static StatePole newAllRandom(ParametersPole p) {
        return new StatePole(VariablesPole.newAllRandom(p));
    }

    public static StatePole newUprightAndStill() {
        return new StatePole(VariablesPole.newUprightAndStill());
    }

    public static StatePole newAngleAndPosRandom(ParametersPole p) {
        return new StatePole(VariablesPole.newAngleAndPosRandom(p));
    }

    public static int nofActions() {
        return 2;
    }

    public static int nofStates() {
        return 4;
    }

    public static StatePole newFromVariables(VariablesPole v) {
        return new StatePole(v);
    }

    public StatePole newWithAngle(double angle) {
        return StatePole.newFromVariables(VariablesPole.builder()
                .angle(angle).x(x()).angleDot(angleDot()).xDot(xDot()).nofSteps(nofSteps()).build());
    }

    public StatePole calcNew(int action, ParametersPole parameters) {
        return new StatePole(variables.calcNew(action, parameters));
    }

    public double angle() {
        return variables.angle();
    }

    public double angleDot() {
        return variables.angleDot();
    }

    public double x() {
        return variables.x();
    }

    public double xDot() {
        return variables.xDot();
    }

    public int nofSteps() {
        return variables.nofSteps();
    }

    @Override
    public StateI<VariablesPole> copy() {
        return new StatePole(variables);
    }

    @Override
    public List<Double> asList() {
        return variables.asList();
    }

    @Override
    public RealVector asRealVector() {
        return variables.asRealVector();
    }

    @Override
    public String toString() {
        return variables.toString();
    }

}
