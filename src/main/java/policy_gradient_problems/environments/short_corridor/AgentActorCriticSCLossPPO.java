package policy_gradient_problems.environments.short_corridor;

import lombok.SneakyThrows;
import org.apache.commons.math3.util.Pair;
import policy_gradient_problems.domain.abstract_classes.AgentA;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;

import java.util.List;

import static common.ListUtils.arrayPrimitiveDoublesToList;

public class AgentActorCriticSCLossPPO extends AgentA<VariablesSC>
        implements AgentNeuralActorNeuralCriticI<VariablesSC> {

    NeuralActorMemorySCLossPPO actor;
    NeuralCriticMemorySC critic;

    public static AgentActorCriticSCLossCEM newDefault() {
        return new AgentActorCriticSCLossCEM(StateSC.newFromRealPos(0));
    }

    public AgentActorCriticSCLossPPO(StateI<VariablesSC> state) {
        super(state);
        this.actor= NeuralActorMemorySCLossPPO.newDefault();
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

    @Override
    public Pair<Double,Double> lossActorAndCritic() {
        return Pair.create(actor.getError(),critic.getError());
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
