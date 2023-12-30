package policy_gradient_problems.the_problems.cart_pole;

import common_dl4j.NetSettings;
import lombok.Builder;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.abstract_classes.*;
import policy_gradient_problems.common.ParamFunction;
import java.util.List;

public class AgentParamActorNeuralCriticPole extends AgentA<VariablesPole> implements AgentParamActorNeuralCriticI<VariablesPole> {

    ParamFunction actor;
    NeuralMemoryPole critic;
    AgentParamActorPoleHelper helper;
    NetSettings criticSettings;
    ParametersPole parametersPole;

    public static AgentParamActorNeuralCriticPole newDefaultCritic(StateI<VariablesPole> stateStart) {
        NetSettings netSettings = NetSettings.builder()
                .nHidden(10)
                .learningRate(1e-3).build();
        return  AgentParamActorNeuralCriticPole.builder()
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
        this.critic = new NeuralMemoryPole(criticSettings, parametersPole);
        this.helper=new AgentParamActorPoleHelper(actor);
        this.criticSettings=criticSettings;
        this.parametersPole=parametersPole;
    }


    @Override
    public void fitCritic(List<List<Double>> in, List<Double> out, int nofFits) {
        critic.fit(in,out,nofFits);
    }

    @Override
    public double getCriticOut(StateI<VariablesPole> state) {
        return critic.getOutValue(state);
    }

    public AgentParamActorNeuralCriticPole copy() {
        return new AgentParamActorNeuralCriticPole(
                getState().copy(),
                actor.copy().asRealVector(),
                criticSettings,
                parametersPole);
    }

    @Override
    public void changeActor(RealVector change) {
        actor.change(change);
    }

    @Override
    public ArrayRealVector calcGradLogVector(StateI<VariablesPole> state, Action action) {
        return (ArrayRealVector) helper.calcGradLogVector(state,action.asInt());
    }

    @Override
    public List<Double> getActionProbabilities() {
        return helper.calcActionProbabilitiesInState(getState());
    }

}
