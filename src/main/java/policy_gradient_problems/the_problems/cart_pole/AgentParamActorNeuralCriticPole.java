package policy_gradient_problems.the_problems.cart_pole;

import common_dl4j.NetSettings;
import lombok.Builder;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.abstract_classes.*;
import policy_gradient_problems.agent_interfaces.AgentParamActorNeuralCriticI;
import policy_gradient_problems.common_helpers.ParamFunction;

import java.util.List;

public class AgentParamActorNeuralCriticPole extends AgentA<VariablesPole> implements AgentParamActorNeuralCriticI<VariablesPole> {

    ParamFunction actor;
    NeuralCriticMemoryPole critic;
    AgentParamActorPoleHelper helper;

    public static AgentParamActorNeuralCriticPole newDefaultCritic(StateI<VariablesPole> stateStart) {
        var netSettings= NeuralCriticMemoryPole.getDefaultNetSettings();
        return AgentParamActorNeuralCriticPole.builder()
                .stateStart(stateStart).actorParam(AgentParamActorPoleHelper.getInitThetaVector())
                .criticSettings(netSettings).parametersPole(ParametersPole.newDefault()).build();
    }

    @Builder
    public AgentParamActorNeuralCriticPole(StateI<VariablesPole> stateStart,
                                           RealVector actorParam,
                                           NetSettings criticSettings,
                                           ParametersPole parametersPole) {
        super(stateStart);
        this.actor = new ParamFunction(actorParam);
        this.critic = new NeuralCriticMemoryPole(criticSettings, parametersPole);
        this.helper = new AgentParamActorPoleHelper(actor);
    }


    @Override
    public void fitCritic(List<List<Double>> in, List<Double> out, int nofFits) {
        critic.fit(in, out, nofFits);
    }

    @Override
    public double getCriticOut(StateI<VariablesPole> state) {
        return critic.getOutValue(state);
    }

    @Override
    public void changeActor(RealVector change) {
        actor.change(change);
    }

    @Override
    public ArrayRealVector calcGradLogVector(StateI<VariablesPole> state, Action action) {
        return (ArrayRealVector) helper.calcGradLogVector(state, action.asInt());
    }

    @Override
    public List<Double> getActionProbabilities() {
        return helper.calcActionProbabilitiesInState(getState());
    }

}
