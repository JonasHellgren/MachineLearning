package policy_gradient_problems.environments.cart_pole;

import common_dl4j.NetSettings;
import lombok.Builder;
import policy_gradient_problems.domain.abstract_classes.AgentA;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;
import java.util.List;

public class AgentNeuralActorNeuralCriticPoleEntropyLoss extends AgentA<VariablesPole>
        implements AgentNeuralActorNeuralCriticI<VariablesPole> {

    NeuralActorMemoryPoleCrossEntropyLoss actor;
    NeuralCriticMemoryPole critic;

    public static AgentNeuralActorNeuralCriticPoleEntropyLoss newDefault(StateI<VariablesPole> stateStart) {
        var netSettings= NeuralCriticMemoryPole.getDefaultNetSettings();
        return AgentNeuralActorNeuralCriticPoleEntropyLoss.builder()
                .stateStart(stateStart)
                .criticSettings(netSettings)
                .parametersPole(ParametersPole.newDefault())
                .build();
    }

    @Builder
    public AgentNeuralActorNeuralCriticPoleEntropyLoss(StateI<VariablesPole> stateStart,
                                                       NetSettings criticSettings,
                                                       ParametersPole parametersPole) {
        super(stateStart);
        this.actor = NeuralActorMemoryPoleCrossEntropyLoss.newDefault(parametersPole);
        this.critic = new NeuralCriticMemoryPole(criticSettings, parametersPole);
    }

    @Override
    public List<Double> getActionProbabilities() {
        return actor.getOutValue(getState().asList());
    }


    @Override
    public void fitActor(List<List<Double>> inList, List<List<Double>> outList) {
        actor.fit(inList,outList);
    }


    @Override
    public void fitCritic(List<List<Double>> in, List<Double> out) {
        critic.fit(in, out);
    }

    @Override
    public double getCriticOut(StateI<VariablesPole> state) {
        return critic.getOutValue(state);
    }

}
