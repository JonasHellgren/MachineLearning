package policy_gradient_problems.the_problems.short_corridor;

import lombok.Getter;
import policy_gradient_problems.abstract_classes.AgentA;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.agent_interfaces.AgentNeuralActorNeuralCriticI;
import java.util.List;

import static common.ListUtils.arrayPrimitiveDoublesToList;
import static common.ListUtils.toArray;

@Getter
public class AgentNeuralActorNeuralCriticSC extends AgentA<VariablesSC>
        implements AgentNeuralActorNeuralCriticI<VariablesSC> {

    NeuralActorMemorySC actor;
    NeuralCriticMemorySC critic;

    public static AgentNeuralActorNeuralCriticSC newDefault() {
        return new AgentNeuralActorNeuralCriticSC(StateSC.newFromPos(0));
    }

    public AgentNeuralActorNeuralCriticSC(StateI<VariablesSC> state) {
        super(state);
        this.actor=NeuralActorMemorySC.newDefault();
        this.critic=NeuralCriticMemorySC.newDefault();
    }

    @Override
    public List<Double> getActionProbabilities() {
        double[] outArr = actor.getOutValue(toArray(getState().asList()));
        return arrayPrimitiveDoublesToList(outArr);
    }

    public List<Double> calcActionProbsInObsState(int stateObserved) {
        double[] outArr = actor.getOutValue(toArray(List.of((double) stateObserved)));
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
    }


}
