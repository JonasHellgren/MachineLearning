package policy_gradient_problems.environments.short_corridor;

import lombok.Getter;
import lombok.SneakyThrows;
import policy_gradient_problems.domain.abstract_classes.AgentA;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;
import java.util.List;

import static common.ListUtils.arrayPrimitiveDoublesToList;

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
        StateSC stateAsObs=(StateSC) getState();
        return calcActionProbabilitiesInObsState(stateAsObs.asObserved().getPos());
    }

    public List<Double> calcActionProbabilitiesInObsState(int stateObserved) {
        double[] outArr = actor.getOutValue(new double[]{stateObserved});
        return arrayPrimitiveDoublesToList(outArr);
    }


    @SneakyThrows
    @Override
    public void fitActor(List<List<Double>> inList, List<List<Double>> outList) {
        actor.fit(inList,outList);
    }

    @Override
    public void fitCritic(List<List<Double>> stateValuesList, List<Double> valueTarList) {
        critic.fit(stateValuesList,valueTarList);
    }

    @Override
    public double getCriticOut(StateI<VariablesSC> state) {
        return critic.getOutValue(state);
    }


}
