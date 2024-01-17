package policy_gradient_problems.the_problems.cart_pole;

import common_dl4j.NetSettings;
import lombok.Builder;
import lombok.SneakyThrows;
import policy_gradient_problems.abstract_classes.AgentA;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.agent_interfaces.AgentNeuralActorNeuralCriticI;
import java.util.List;

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
      //  System.out.println("actor.getOutValue(getState().asList()) = " + actor.getOutValue(getState().asList()));
        return actor.getOutValue(getState().asList());
    }

    @Override
    public void fitActorOld(List<Double> in, List<Double> out) {
        actor.fit(in,out);
    }

    @SneakyThrows
    @Override
    public void fitActor(List<List<Double>> inList, List<List<Double>> outList) {
        throw new NoSuchMethodException();
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