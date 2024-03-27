package policy_gradient_problems.environments.cart_pole;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.domain.abstract_classes.StateI;

import java.util.List;

@Getter
@Setter
public class StatePole implements StateI<VariablesPole> {

    VariablesPole variables;
    ParametersPole parameters;
    PoleRelations relations;

    @Builder
    public StatePole(double angle,
                     double x,
                     double angleDot,
                     double xDot,
                     int nofSteps,
                     ParametersPole parameters
    ) {
        variables = VariablesPole.builder().angle(angle).x(x).angleDot(angleDot).xDot(xDot).nofSteps(nofSteps).build();
        this.parameters=parameters;
        this.relations=new PoleRelations(parameters);
    }

    public static StatePole newFromVariables(VariablesPole v,ParametersPole p) {
        return new StatePole(v,p);
    }

    public static StatePole newAllRandom(ParametersPole p) {
        return new StatePole(VariablesPole.newAllRandom(p),p);
    }

    public static StatePole newUprightAndStill(ParametersPole p) {
        return new StatePole(VariablesPole.newUprightAndStill(),p);
    }

    public static StatePole newAngleAndPosRandom(ParametersPole p) {
        return new StatePole(VariablesPole.newAngleAndPosRandom(p),p);
    }

    public StatePole copyWithAngle(double angle) {
        return StatePole.newFromVariables(VariablesPole.builder()
                .angle(angle).x(x()).angleDot(angleDot()).xDot(xDot()).nofSteps(nofSteps()).build(),parameters);
    }

    public StatePole copyWithAngleDot(double angleDot) {
        return StatePole.newFromVariables(variables.withAngleDot(angleDot),parameters);
    }


    private StatePole(VariablesPole variables, ParametersPole parameters) {
        this.variables = variables;
        this.parameters = parameters;
        this.relations=new PoleRelations(parameters);
    }


    public static int nofActions() {
        return 2;
    }

    public  int nofStates() {
        return variables.asList().size();
    }


    public StatePole calcNew(int action, ParametersPole parameters) {
        return new StatePole(relations.calcNew(action,variables),parameters);
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
        return new StatePole(variables.copy(),parameters);
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
