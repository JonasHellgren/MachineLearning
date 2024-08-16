package safe_rl.domain.trainer.helpers;

import com.google.common.base.Preconditions;
import common.list_arrays.ListUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.trainer.mediators.MediatorBaseI;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.MultiStepResults;
import safe_rl.domain.trainer.value_objects.TrainerParameters;

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

//@Builder
@AllArgsConstructor
public class CorrectedActionPenalizer<V> {

    MediatorBaseI<V> mediator;

    public void maybePenalize(int step,
                              @NonNull MultiStepResults<V> msr,
                              @NonNull List<Double> lossCritic) {
        if (!msr.isSafeCorrectedAtStep(step)) {
            return;
        }
        var actionPolicy = msr.actionPolicyAtStep(step);
        var state = msr.stateAtStep(step);
        var actionApplied = msr.actionAppliedAtStep(step);
        maybePenalize(actionPolicy, state, actionApplied, lossCritic);
    }

    public void maybePenalize(@NonNull  Experience<V> exp, @NonNull List<Double> lossCriticList1) {
        if (!exp.isSafeCorrected()) {
            return;
        }
        maybePenalize(exp.ars().action(), exp.state(), exp.actionApplied(), lossCriticList1);
    }

    void maybePenalize(Action actionPolicy,
                       StateI<V> state,
                       Action actionApplied,
                       @NonNull List<Double> lossCritic) {
        var agent=mediator.getExternal().agent();
        var parameters=mediator.getParameters();
        double avgCriticLoss= ListUtils.findAverage(lossCritic).orElse(0);
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
