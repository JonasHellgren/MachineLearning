package policy_gradient_problems.environments.cart_pole;

import common.dl4j.NetSettings;
import lombok.Builder;
import org.apache.commons.math3.util.Pair;
import policy_gradient_problems.domain.abstract_classes.AgentA;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;

import java.util.List;

public class AgentActorICriticPolePPO extends AgentA<VariablesPole>
        implements AgentNeuralActorNeuralCriticI<VariablesPole> {

    NeuralActorMemoryPolePPOLoss actor;
    NeuralCriticMemoryPole critic;

    public static AgentActorICriticPolePPO newDefault(StateI<VariablesPole> stateStart) {
        var netSettings= NeuralCriticMemoryPole.getDefaultNetSettings();
        return AgentActorICriticPolePPO.builder()
                .stateStart(stateStart)
                .criticSettings(netSettings)
                .parametersPole(ParametersPole.newDefault())
                .build();
    }

    @Builder
    public AgentActorICriticPolePPO(StateI<VariablesPole> stateStart,
                                    NetSettings criticSettings,
                                    ParametersPole parametersPole) {
        super(stateStart);
        this.actor = NeuralActorMemoryPolePPOLoss.newDefault(parametersPole);
        this.critic = new NeuralCriticMemoryPole(criticSettings, parametersPole);
    }

    @Override
    public List<Double> actionProbabilitiesInPresentState() {
        return actor.getOutValue(getState().asList());
        //return actorOut(getState());  //todo
    }


    @Override
    public void fitActor(List<List<Double>> inList, List<List<Double>> outList) {
        actor.fit(inList,outList);
    }

    @Override
    public List<Double> actorOut(StateI<VariablesPole> state) {
        return actor.getOutValue(state.asList());
    }


    @Override
    public void fitCritic(List<List<Double>> in, List<Double> out) {
        critic.fit(in, out);
    }

    @Override
    public double criticOut(StateI<VariablesPole> state) {
        return critic.getOutValue(state);
    }

    @Override
    public Pair<Double, Double> lossActorAndCritic() {
        return Pair.create(actor.getError(), critic.getError());
    }
}
