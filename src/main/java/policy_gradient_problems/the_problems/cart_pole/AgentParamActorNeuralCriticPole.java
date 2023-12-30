package policy_gradient_problems.the_problems.cart_pole;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.abstract_classes.*;

import java.util.List;

public class AgentParamActorNeuralCriticPole extends AgentA<VariablesPole> implements AgentParamActorNeuralCriticI<VariablesPole> {


    public AgentParamActorNeuralCriticPole(StateI<VariablesPole> state) {
        super(state);
    }

    @Override
    public void fitCritic(List<List<Double>> stateValuesList, List<Double> valueTarList, int nofFits) {

    }

    @Override
    public void changeActor(RealVector change) {

    }

    @Override
    public ArrayRealVector calcGradLogVector(StateI<VariablesPole> state, Action action) {
        return null;
    }

    @Override
    public List<Double> getActionProbabilities() {
        return null;
    }
}
