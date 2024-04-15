package policy_gradient_problems.environments.cart_pole;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.agent_interfaces.AgentParamActorI;
import policy_gradient_problems.domain.param_memories.ActorMemoryParam;
import policy_gradient_problems.domain.abstract_classes.AgentA;
import policy_gradient_problems.domain.abstract_classes.StateI;

import java.util.List;

/***
 */

@Setter
@Getter
    public class AgentParamActorPole extends AgentA<VariablesPole> implements AgentParamActorI<VariablesPole> {

    ActorMemoryParam actor;
    AgentParamActorPoleHelper helper;

    public static AgentParamActorPole newRandomStartStateDefaultThetas(ParametersPole parameters) {
        return new AgentParamActorPole(
                StatePole.newAngleAndPosRandom(parameters),
                AgentParamActorPoleHelper.getInitThetaVector());
    }

    public static AgentParamActorPole newAllZeroStateDefaultThetas(ParametersPole parameters) {
        return new AgentParamActorPole(
                StatePole.newUprightAndStill(parameters),
                AgentParamActorPoleHelper.getInitThetaVector());
    }

    public AgentParamActorPole(StateI<VariablesPole> stateStart, RealVector thetaVector) {
        this(stateStart,new ActorMemoryParam(thetaVector),null);
        this.helper=new AgentParamActorPoleHelper(actor);  //fills null above
    }

    public AgentParamActorPole(StateI<VariablesPole> state,
                               ActorMemoryParam actor,
                               AgentParamActorPoleHelper helper) {
        super(state);
        this.actor = actor;
        this.helper = helper;
    }

    public AgentParamActorPole copy() {
        return new AgentParamActorPole(getState().copy(), actor.copy().asRealVector());
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
    public List<Double> actionProbabilitiesInPresentState() {
        return helper.calcActionProbabilitiesInState(getState());
    }

    @SneakyThrows
    @Override
    public Pair<Double, Double> meanAndStd(int state) {
        throw new NoSuchMethodException();
    }
}
