package policy_gradient_problems.the_problems.cart_pole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.bytedeco.opencv.presets.opencv_core;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.the_problems.short_corridor.VariablesSC;

import java.util.List;

import static common.MyFunctions.sqr2;
import static common.RandUtils.getRandomDouble;

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
