package policy_gradient_problems.the_problems.short_corridor;

import common.ListUtils;
import common.RandUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.abstract_classes.AgentA;
import policy_gradient_problems.abstract_classes.AgentParamActorTabCriticI;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.common.ParamFunction;
import policy_gradient_problems.common.SubArrayExtractor;
import policy_gradient_problems.common.TabularValueFunction;

import java.util.ArrayList;
import java.util.List;

import static common.ArrayUtil.createArrayWithSameDoubleNumber;
import static common.IndexFinder.findBucket;
import static common.ListUtils.toArray;
import static common.RandUtils.randomNumberBetweenZeroAndOne;
import static org.apache.commons.lang3.ArrayUtils.subarray;
import static policy_gradient_problems.common.BucketLimitsHandler.getLimits;
import static policy_gradient_problems.common.BucketLimitsHandler.throwIfBadLimits;
import static policy_gradient_problems.common.GradLogCalculator.calculateGradLog;
import static policy_gradient_problems.common.SoftMaxEvaluator.getProbabilities;

/***
 * See shortCorridor.md for description
 *
 * State in AgentA is real state, state in an Experience is observed state, what training is based on
 * Action probabilities are based on observed state
 */

@Getter
@Setter
public class AgentSC extends AgentA<VariablesSC> implements AgentParamActorTabCriticI<VariablesSC> {
    public static final double THETA = 0.5;
    public static final int NOF_ACTIONS = EnvironmentSC.NOF_ACTIONS;

    ParamFunction actor;
    TabularValueFunction critic;
    AgentParamActorSCHelper helper;

    public static AgentSC newRandomStartStateDefaultThetas() {
        return newWithRandomStartStateAndGivenThetas(
                createArrayWithSameDoubleNumber(AgentParamActorSCHelper.getThetaLength(), THETA));
    }

    public static AgentSC newWithRandomStartStateAndGivenThetas(double[] thetaArray) {
        return new AgentSC(AgentParamActorSCHelper.getRandomNonTerminalState(), thetaArray);
    }

    public AgentSC(int posStart, double[] thetaArray) {
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
        int stateObserved =EnvironmentSC.getPos(stateInExperience);
        return helper.calcGradLogVector(stateObserved, action.asInt());
    }

    @Override
    public List<Double> getActionProbabilities() {
        return helper.calcActionProbsInObsState(EnvironmentSC.getPos(getState()));
    }

    @Override
    public Action chooseAction() {
        int observedStateOld = EnvironmentSC.getObservedPos(getState());
        helper.throwIfBadObsState(observedStateOld);
        var limits = getLimits(helper.calcActionProbsInObsState(observedStateOld));
        throwIfBadLimits(limits);
        return Action.ofInteger(findBucket(toArray(limits), randomNumberBetweenZeroAndOne()));
    }

    public void setStateAsRandomNonTerminal() {
        setState(StateSC.newFromPos(AgentParamActorSCHelper.getRandomNonTerminalState()));
    }



}
