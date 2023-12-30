package policy_gradient_problems.the_problems.short_corridor;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.abstract_classes.AgentA;
import policy_gradient_problems.abstract_classes.AgentParamActorTabCriticI;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.common.ParamFunction;
import policy_gradient_problems.common.TabularValueFunction;

import java.util.List;

import static common.ArrayUtil.createArrayWithSameDoubleNumber;
import static org.apache.commons.lang3.ArrayUtils.subarray;
import static policy_gradient_problems.common.SoftMaxEvaluator.getProbabilities;

/***
 * See shortCorridor.md for description
 *
 * State in AgentA is real state, state in an Experience is observed state, what training is based on
 * Action probabilities are based on observed state
 */

@Getter
@Setter
public class AgentParamActorTabCriticSC extends AgentA<VariablesSC> implements AgentParamActorTabCriticI<VariablesSC> {
    public static final int NOF_ACTIONS = EnvironmentSC.NOF_ACTIONS;

    ParamFunction actor;
    TabularValueFunction critic;
    AgentParamActorSCHelper helper;

    public static AgentParamActorTabCriticSC newRandomStartStateDefaultThetas() {
        return newWithRandomStartStateAndGivenThetas(
                createArrayWithSameDoubleNumber(
                        AgentParamActorSCHelper.getThetaLength(),
                        AgentParamActorSCHelper.THETA));
    }

    public static AgentParamActorTabCriticSC newWithRandomStartStateAndGivenThetas(double[] thetaArray) {
        return new AgentParamActorTabCriticSC(AgentParamActorSCHelper.getRandomNonTerminalState(), thetaArray);
    }

    public AgentParamActorTabCriticSC(int posStart, double[] thetaArray) {
        super(StateSC.newFromPos(posStart));
        this.actor = new ParamFunction(thetaArray);
        this.critic = new TabularValueFunction(EnvironmentSC.SET_OBSERVABLE_STATES_NON_TERMINAL.size());
        this.helper=new AgentParamActorSCHelper(actor);
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

    public void setStateAsRandomNonTerminal() {  //todo remove
        setState(StateSC.newFromPos(AgentParamActorSCHelper.getRandomNonTerminalState()));
    }

}
