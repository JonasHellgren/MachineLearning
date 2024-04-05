package policy_gradient_problems.environments.short_corridor;

import lombok.Getter;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.agent_interfaces.AgentParamActorI;
import policy_gradient_problems.domain.param_memories.ActorMemoryParam;
import policy_gradient_problems.domain.abstract_classes.AgentA;
import policy_gradient_problems.domain.abstract_classes.StateI;

import java.util.List;

@Getter
public class AgentParamActorSC extends AgentA<VariablesSC> implements AgentParamActorI<VariablesSC> {

    ActorMemoryParam actor;
    AgentParamActorSCHelper helper;

    public static AgentParamActorSC newRandomStartStateDefaultThetas() {
        return newWithRandomStartStateAndGivenThetas(AgentParamActorSCHelper.getArrayWithEqualThetas());
    }

    public static AgentParamActorSC newWithRandomStartStateAndGivenThetas(double[] thetaArray) {
        return new AgentParamActorSC(AgentParamActorSCHelper.getRandomNonTerminalState(), thetaArray);
    }

    public AgentParamActorSC(int posStart, double[] thetaArray) {
        super(StateSC.newFromRealPos(posStart));
        this.actor = new ActorMemoryParam(thetaArray);
        this.helper = new AgentParamActorSCHelper(actor);
    }

    @Override
    public void changeActor(RealVector change) {
        actor.change(change);
    }

    @Override
    public ArrayRealVector calcGradLogVector(StateI<VariablesSC> stateInExperience, Action action) {
        int stateObserved = EnvironmentSC.getObservedPos(stateInExperience);
        return helper.calcGradLogVector(stateObserved, action.asInt());
    }

    @Override
    public List<Double> actionProbabilitiesInPresentState() {
        return helper.calcActionProbsInObsState(EnvironmentSC.getObservedPos(getState()));
    }

    @Override
    public Action chooseAction() {
        return helper.chooseAction(getState());
    }


}
