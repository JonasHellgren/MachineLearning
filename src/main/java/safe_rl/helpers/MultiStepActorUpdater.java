package safe_rl.helpers;

import common.list_arrays.ListUtils;
import common.other.Conditionals;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.value_classes.MultiStepResults;
import safe_rl.domain.value_classes.TrainerParameters;

import java.util.List;

import static java.lang.Math.max;

@AllArgsConstructor
public class MultiStepActorUpdater<V> {

    @NonNull AgentACDiscoI<V> agent;
    @NonNull TrainerParameters parameters;


    @SneakyThrows
    public void update(MultiStepResults<V> msr, List<Double> lossCriticList) {
        double avgCriticLoss = ListUtils.findAverage(lossCriticList).orElse(0);

        for (int step = 0; step < msr.nExperiences(); step++) {
            penalizeAgentProposedActionIfSafeCorrected(avgCriticLoss, msr, step);
            agent.fitActor(msr.stateAtStep(step), msr.actionAppliedAtStep(step), msr.advantageAtStep(step));
        }


    }

    private void penalizeAgentProposedActionIfSafeCorrected(double avgCriticLoss,
                                                            MultiStepResults<V> msr,
                                                            int step) {
        Conditionals.executeIfTrue(msr.isSafeCorrectedAtStep(step),
                () -> {
                    double dev = Math.abs(msr.actionAppliedAtStep(step).asDouble() - msr.actionPolicyAtStep(step).asDouble());
                    var state = msr.stateAtStep(step);
                    Double std = agent.readActor(state).getSecond();
                    double excessFromStd = max(0, dev - std);
                    double aNominalForNormalization = agent.chooseActionNominal(state).asDouble();
                    double penalty = excessFromStd/ aNominalForNormalization *avgCriticLoss;
                    agent.fitActor(
                            msr.stateAtStep(step),
                            msr.actionPolicyAtStep(step),
                            -parameters.ratioPenCorrectedAction()*penalty);
                });
    }


}
