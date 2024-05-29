package multi_agent_rl.environments.apple;

import common.math.Discrete2DPos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import multi_agent_rl.domain.abstract_classes.StateI;
import org.bytedeco.libfreenect._freenect_context;

@AllArgsConstructor
public class StateApple implements StateI<VariablesApple> {

    @Setter
    @Getter
    VariablesApple variables;

    @Override
    public StateI<VariablesApple> copy() {
        return new StateApple(variables.copy());
    }

    public Discrete2DPos posA() {
        return variables.posA();
    }

    public Discrete2DPos posB() {
        return variables.posB();
    }

    public Discrete2DPos posApple() {
        return variables.posApple();
    }

}
