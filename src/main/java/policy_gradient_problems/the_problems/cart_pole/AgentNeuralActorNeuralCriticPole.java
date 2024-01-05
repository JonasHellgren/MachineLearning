package policy_gradient_problems.the_problems.cart_pole;

import common_dl4j.NetSettings;
import lombok.Builder;
import policy_gradient_problems.abstract_classes.AgentA;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.agent_interfaces.AgentNeuralActorNeuralCriticI;
import java.util.List;
import static common.ListUtils.arrayPrimitiveDoublesToList;

public class AgentNeuralActorNeuralCriticPole extends AgentA<VariablesPole>
        implements AgentNeuralActorNeuralCriticI<VariablesPole> {

    NeuralActorMemoryPole actor;
    NeuralCriticMemoryPole critic;
    //AgentParamActorPoleHelper helper;

    public static AgentNeuralActorNeuralCriticPole newDefault(StateI<VariablesPole> stateStart) {
        var netSettings= NeuralCriticMemoryPole.getDefaultNetSettings();
        return AgentNeuralActorNeuralCriticPole.builder()
                .stateStart(stateStart)
                .criticSettings(netSettings)
                .parametersPole(ParametersPole.newDefault())
                .build();
    }

    @Builder
    public AgentNeuralActorNeuralCriticPole(StateI<VariablesPole> stateStart,
                                           NetSettings criticSettings,
                                           ParametersPole parametersPole) {
        super(stateStart);
        this.actor = NeuralActorMemoryPole.newDefault(parametersPole);
        this.critic = new NeuralCriticMemoryPole(criticSettings, parametersPole);
      //  this.helper = new AgentParamActorPoleHelper(actor);
    }

    @Override
    public List<Double> getActionProbabilities() {
        return actor.getOutValue(getState().asList());
    }

    @Override
    public void fitActor(List<Double> in, List<Double> out) {
        actor.fit(in,out);
    }


    @Override
    public void fitCritic(List<List<Double>> in, List<Double> out, int nofFits) {
        critic.fit(in, out, nofFits);
    }

    @Override
    public double getCriticOut(StateI<VariablesPole> state) {
        return critic.getOutValue(state);
    }

}
