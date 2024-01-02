package policy_gradient_problems.the_problems.short_corridor;

import common.ListUtils;
import org.nd4j.linalg.factory.Nd4j;
import policy_gradient_problems.abstract_classes.AgentA;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.agent_interfaces.AgentNeuralActorNeuralCriticI;

import java.util.Arrays;
import java.util.List;

import static common.ListUtils.arrayPrimitiveDoublesToList;
import static common.ListUtils.toArray;

public class AgentNeuralActorNeuralCriticSC extends AgentA<VariablesSC>
        implements AgentNeuralActorNeuralCriticI<VariablesSC> {

    //MultiLayerNetwork actor;
    //MultiLayerNetwork critic;

    NeuralActorMemorySC actor;
    NeuralCriticMemorySC critic;

    public AgentNeuralActorNeuralCriticSC(StateI<VariablesSC> state) {
        super(state);
    }

    @Override
    public List<Double> getActionProbabilities() {
        double[] outArr = actor.getOutValue(toArray(getState().asList()));
        return arrayPrimitiveDoublesToList(outArr);
    }

    @Override
    public void fitActor(List<Double> in, List<Double> out) {
        actor.fit(in,out,1);
    }

    @Override
    public void fitCritic(List<List<Double>> stateValuesList, List<Double> valueTarList, int nofFits) {
        critic.fit(stateValuesList,valueTarList,nofFits);
    }

    @Override
    public double getCriticOut(StateI<VariablesSC> state) {
        return critic.getOutValue(state);
      //  return 0;
    }


}
