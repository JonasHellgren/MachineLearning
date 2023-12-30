package policy_gradient_problems.the_problems.short_corridor;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.abstract_classes.*;

import java.util.List;

public class AgentParamActorSC extends AgentA<VariablesSC> implements AgentParamActorI<VariablesSC> {
    public AgentParamActorSC(StateI<VariablesSC> state) {
        super(state);
    }

    @Override
    public void changeActor(RealVector change) {

    }

    @Override
    public ArrayRealVector calcGradLogVector(StateI<VariablesSC> state, Action action) {
        return null;
    }

    @Override
    public List<Double> getActionProbabilities() {
        return null;
    }
}
