package policy_gradient_problems.environments.maze;

import java.awt.geom.Point2D;

public record VariablesMaze(
        Point2D pos
) {


    public VariablesMaze copy() {
        return new VariablesMaze(pos);
    }



}
