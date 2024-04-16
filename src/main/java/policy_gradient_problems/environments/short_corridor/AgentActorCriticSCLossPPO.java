package policy_gradient_problems.environments.short_corridor;

import lombok.SneakyThrows;
import org.apache.commons.math3.util.Pair;
import policy_gradient_problems.domain.abstract_classes.AgentA;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;

import java.util.List;

import static common.list_arrays.ListUtils.arrayPrimitiveDoublesToList;

public class AgentActorCriticSCLossPPO extends AgentA<VariablesSC>
        implements AgentNeuralActorNeuralCriticI<VariablesSC> {

    NeuralActorMemorySCLossPPO actor;
    NeuralCriticMemorySC critic;

    public static AgentActorCriticSCLossPPO newDefault() {
        return new AgentActorCriticSCLossPPO(StateSC.newFromObsPos(0));
    }

    public AgentActorCriticSCLossPPO(StateI<VariablesSC> state) {
        super(state);
        this.actor= NeuralActorMemorySCLossPPO.newDefault();
        this.critic=NeuralCriticMemorySC.newDefault();
    }

    @Override
    public List<Double> actionProbabilitiesInPresentState() {
        StateSC state=(StateSC) getState();
        return actorOut(state);
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
    public List<Double> actorOut(StateI<VariablesSC> state) {
        return arrayPrimitiveDoublesToList(actor.getOutValue(new double[]{state.getVariables().posObserved()}));
    }

    @Override
    public void fitCritic(List<List<Double>> stateValuesList, List<Double> valueTarList) {
        critic.fit(stateValuesList,valueTarList);
    }

    @Override
    public double criticOut(StateI<VariablesSC> state) {
        return critic.getOutValue(state);
    }

}
