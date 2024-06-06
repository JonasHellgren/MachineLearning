package multi_agent_rl.environments.apple;

import common.math.Discrete2DPos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import multi_agent_rl.domain.abstract_classes.ObservationI;
import multi_agent_rl.domain.abstract_classes.StateI;

@AllArgsConstructor
public class ObservationApple implements ObservationI<VariablesObservationApple> {

    @Setter
    @Getter
    VariablesObservationApple variables;

    @Override
    public ObservationI<VariablesObservationApple> copy() {
        return new ObservationApple(variables.copy());
    }

    public static ObservationI<VariablesObservationApple> of(StateI<VariablesStateApple,VariablesObservationApple> state,
                                                             boolean isAgentA) {
        VariablesStateApple vars = state.getVariables();
        Discrete2DPos posApple= vars.posApple();
        Discrete2DPos posThis=isAgentA ? vars.posA() : vars.posB();
        Discrete2DPos posOther= isAgentA ? vars.posB() : vars.posA();
        VariablesObservationApple variables=new VariablesObservationApple(
                posThis.vector(posOther),posThis.vector(posApple));
        return new ObservationApple(variables);
    }



}
