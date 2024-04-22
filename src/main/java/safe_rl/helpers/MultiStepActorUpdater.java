package safe_rl.helpers;

import common.list_arrays.ListUtils;
import common.other.Conditionals;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.value_classes.Experience;
import safe_rl.domain.value_classes.MultiStepResults;
import safe_rl.domain.value_classes.TrainerParameters;

import java.util.List;
import java.util.zip.DataFormatException;

@AllArgsConstructor
public class MultiStepActorUpdater<V> {

    @NonNull AgentACDiscoI<V> agent;
    @NonNull TrainerParameters parameters;


    @SneakyThrows
    public void update(MultiStepResults<V> msr, List<Double> lossCriticList) {
        double avgCriticLoss= ListUtils.findAverage(lossCriticList)
                .orElse(0)*parameters.ratioPenCorrectedAction();

        for (int step = 0; step < msr.nExperiences() ; step++) {
            penalizeAgentProposedActionIfSafeCorrected(avgCriticLoss, msr,step);
            agent.fitActor(msr.stateAtStep(step),msr.actionAppliedAtStep(step),msr.advantageAtStep(step));
        }


    }


    //todo ändra logik ev egen klass
    private  void penalizeAgentProposedActionIfSafeCorrected(double avgCriticLoss,
                                                             MultiStepResults<V> msr,
                                                             int step) {
        Conditionals.executeIfTrue(msr.isSafeCorrectedAtStep(step),
                () -> agent.fitActor(msr.stateAtStep(step),msr.actionPolicyAtStep(step), -avgCriticLoss));
    }


}
