package policy_gradient_problems.environments.maze;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.domain.abstract_classes.StateI;
import java.awt.geom.Point2D;
import java.util.List;

@AllArgsConstructor
public class StateMaze implements StateI<VariablesMaze> {

    VariablesMaze variables;

    public static StateMaze newFromPoint(Point2D pos) {
        return new StateMaze(new VariablesMaze(pos));
    }

    public Point2D point() {
        return variables.pos();
    }

    public void setPoint(Point2D point) {
        setVariables(new VariablesMaze(point));
    }


    @Override
    public VariablesMaze getVariables() {
        return variables;
    }

    @Override
    public void setVariables(VariablesMaze variables) {
        this.variables=variables;
    }

    @Override
    public StateI<VariablesMaze> copy() {
        return newFromPoint(variables.copy().pos());
    }

    @Override
    public List<Double> asList() {
        return List.of(variables.pos().getX(),variables.pos().getY());
    }

    @SneakyThrows
    @Override
    public RealVector asRealVector() {
        throw new NoSuchMethodException();
    }

    @Override
    public String toString() {
        return variables.pos().toString();
    }

}
