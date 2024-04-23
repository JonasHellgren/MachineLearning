package safe_rl.helpers;

import com.google.common.base.Preconditions;
import common.list_arrays.ListUtils;
import lombok.Builder;
import lombok.NonNull;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.value_classes.Experience;
import safe_rl.domain.value_classes.MultiStepResults;
import safe_rl.domain.value_classes.TrainerParameters;

import java.util.List;

import static java.lang.Math.max;

/***
 *  Pushes the actor memory to not repeat actions needing correction
 *  The idea is that penalizing takes place only of dev>std, can be interpreted as first
 *  when std is smaller penalizing kicks in.
 *  aNominalForNormalization is needed because action and critic loss (values) can be
 *  on very different scales
 *  Used relations are:
 *  dev=|aCorrected-aPolicy|
 *  excess=max(0,dev-std)
 *  pen=ratioPenCorrectedAction*excess/aNom*avgCriticLoss
 *  where ratioPenCorrectedAction is a hyperparameter in trainer parameters
 */

@Builder
public class CorrectedActionPenalizer<V> {

    @NonNull AgentACDiscoI<V> agent;
    @NonNull TrainerParameters parameters;
    @NonNull List<Double> lossCriticList;
    MultiStepResults<V> multiStepResults;

    public void maybePenalize(int step) {
        Preconditions.checkNotNull(multiStepResults,"multiStepResults is null");
        if (!multiStepResults.isSafeCorrectedAtStep(step)) {
            return;
        }
        Action actionPolicy = multiStepResults.actionPolicyAtStep(step);
        var state = multiStepResults.stateAtStep(step);
        Action actionApplied = multiStepResults.actionAppliedAtStep(step);

        maybePenalize(actionPolicy, state, actionApplied);
    }

    public void maybePenalize(Experience<V> exp) {
        Preconditions.checkNotNull(exp,"experience is null");
        if (!exp.isSafeCorrected()) {
            return;
        }
        maybePenalize(exp.ars().action(), exp.state(), exp.actionApplied());
    }



    void maybePenalize(Action actionPolicy, StateI<V> state, Action actionApplied) {
        double avgCriticLoss= ListUtils.findAverage(lossCriticList).orElse(0);
        double dev = Math.abs(actionApplied.asDouble() -
                actionPolicy.asDouble());
        Double std = agent.readActor(state).getSecond();
        double excessFromStd = max(0, dev - std);
        double aNominalForNormalization = agent.chooseActionNominal(state).asDouble();
        double penalty = excessFromStd / aNominalForNormalization * avgCriticLoss;
        agent.fitActor(
                state,
                actionPolicy,
                -parameters.ratioPenCorrectedAction() * penalty);
    }
}
