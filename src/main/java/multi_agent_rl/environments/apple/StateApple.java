package multi_agent_rl.environments.apple;

import com.google.common.base.Preconditions;
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

    public static StateApple of(Discrete2DPos posApple, Discrete2DPos posA, Discrete2DPos posB, AppleSettings settings) {
        VariablesApple variablesApple = VariablesApple.builder()
                .posApple(posApple).posA(posA).posB(posB)
                .build();
        Preconditions.checkArgument(variablesApple.isInBounds(settings),"Non correct position");
        return new StateApple(variablesApple);
    }

    public static boolean posInBounds(Discrete2DPos pos, AppleSettings settings) {
        return VariablesApple.isPosInBounds(pos,settings);
    }


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
