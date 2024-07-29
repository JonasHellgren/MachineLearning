package safe_rl.domain.trainer.value_objects;

import com.google.common.collect.Lists;
import lombok.Builder;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.environment.aggregates.StateI;

import java.util.List;

/**
 * Data container for episode results
 * Every item in experience list is for specific time step
 */

@Builder
public record MultiStepResults<V>(
        int nExperiences,  //equal to length of below lists
        List<MultiStepResultItem<V>> experienceList
) {

    public static <V> MultiStepResults<V> create(int nExp) {
        return MultiStepResults.<V>builder()
                .nExperiences(nExp)
                .experienceList(Lists.newArrayListWithCapacity(nExp))
                .build();
    }


    public boolean isEmpty() {
        return nExperiences==0;
    }

    public MultiStepResultItem<V> experienceAtStep(int step) {
        return experienceList.get(step);
    }

    public StateI<V> stateAtStep(int step) {return experienceAtStep(step).state(); }

    public boolean isFutureOutsideOrTerminalAtStep(int step) {
        return experienceAtStep(step).isStateFutureTerminalOrNotPresent();
    }

    public double valueTarAtStep(int step) {return experienceAtStep(step).valueTarget(); }

    public double advantageAtStep(int step) {return experienceAtStep(step).advantage(); }

    public Action actionAppliedAtStep(int step) {return experienceAtStep(step).actionApplied(); }

    public Action actionPolicyAtStep(int step) {return experienceAtStep(step).actionPolicy(); }

    public boolean isSafeCorrectedAtStep(int step) {
        return experienceAtStep(step).isSafeCorrected(); }

    public void add(MultiStepResultItem<V> experience) {
        experienceList.add(experience);
    }


}
