package policy_gradient_problems.the_problems.short_corridor;

import lombok.Getter;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.abstract_classes.*;
import policy_gradient_problems.common_helpers.ParamFunction;
import java.util.List;

@Getter
public class AgentParamActorSC extends AgentA<VariablesSC> implements AgentParamActorI<VariablesSC> {

    ParamFunction actor;
    AgentParamActorSCHelper helper;

    public static AgentParamActorSC newRandomStartStateDefaultThetas() {
        return newWithRandomStartStateAndGivenThetas(AgentParamActorSCHelper.getArrayWithEqualThetas());
    }

    public static AgentParamActorSC newWithRandomStartStateAndGivenThetas(double[] thetaArray) {
        return new AgentParamActorSC(AgentParamActorSCHelper.getRandomNonTerminalState(), thetaArray);
    }

    public AgentParamActorSC(int posStart, double[] thetaArray) {
        super(StateSC.newFromPos(posStart));
        this.actor = new ParamFunction(thetaArray);
        this.helper = new AgentParamActorSCHelper(actor);
    }

    @Override
    public void changeActor(RealVector change) {
        actor.change(change);
    }

    @Override
    public ArrayRealVector calcGradLogVector(StateI<VariablesSC> stateInExperience, Action action) {
        int stateObserved = EnvironmentSC.getPos(stateInExperience);
        return helper.calcGradLogVector(stateObserved, action.asInt());
    }

    @Override
    public List<Double> getActionProbabilities() {
        return helper.calcActionProbsInObsState(EnvironmentSC.getPos(getState()));
    }

    @Override
    public Action chooseAction() {
        return helper.chooseAction(getState());
    }


}
