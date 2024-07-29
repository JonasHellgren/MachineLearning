package policy_gradient_problems.environments.short_corridor;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.AgentA;
import policy_gradient_problems.domain.agent_interfaces.AgentParamActorTabCriticI;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.param_memories.ActorMemoryParam;
import policy_gradient_problems.domain.param_memories.CriticMemoryParamOneHot;

import java.util.List;

import static org.apache.commons.lang3.ArrayUtils.subarray;
import static common.other.SoftMaxEvaluator.getProbabilities;

/***
 * See shortCorridor.md for description
 *
 * State in AgentA is real stateNew, stateNew in an Experience is observed stateNew, what training is based on
 * Action probabilities are based on observed stateNew
 */

@Getter
@Setter
public class AgentParamActorTabCriticSC extends AgentA<VariablesSC> implements AgentParamActorTabCriticI<VariablesSC> {
    public static final int NOF_ACTIONS = EnvironmentSC.NOF_ACTIONS;

    ActorMemoryParam actor;
    CriticMemoryParamOneHot critic;
    AgentParamActorSCHelper helper;

    public static AgentParamActorTabCriticSC newRandomStartStateDefaultThetas() {
        return newWithRandomStartStateAndGivenThetas(AgentParamActorSCHelper.getArrayWithEqualThetas());
    }

    public static AgentParamActorTabCriticSC newWithRandomStartStateAndGivenThetas(double[] thetaArray) {
        return new AgentParamActorTabCriticSC(EnvironmentSC.getRandomNonTerminalState(), thetaArray);
    }

    public AgentParamActorTabCriticSC(int posStart, double[] thetaArray) {
        super(StateSC.newFromRealPos(posStart));
        this.actor = new ActorMemoryParam(thetaArray);
        this.critic = new CriticMemoryParamOneHot(EnvironmentSC.OBSERVABLE_NON_TERMINAL.size());
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
        StateSC state=(StateSC) getState();
        return helper.calcActionProbsInObsState(state.variables.posObserved());
    }

    @Override
    public Action chooseAction() {
        return helper.chooseAction(getState());
    }


    @Override
    public void changeCritic(int key, double change) {
        critic.setValue(key,getCriticValue(key)+change);
    }

    @Override
    public double getCriticValue(int key) {
        return critic.getValue(key);
    }

    @SneakyThrows
    @Override
    public Pair<Double, Double> meanAndStd(int state) {
        throw new NoSuchMethodException();
    }
}
