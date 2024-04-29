package safe_rl.domain.value_classes;

import lombok.Builder;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.StateI;

import java.util.ArrayList;
import java.util.List;

/**
 * Data container for episode results
 * Every item in experience list is for specific time step
 */

@Builder
public record MultiStepResults<V>(
        int nExperiences,  //equal to length of below lists
        List<ExperienceMultiStep<V>> experienceList
) {

    public static <V> MultiStepResults<V> create(int nExp) {
        return MultiStepResults.<V>builder()
                .nExperiences(nExp)
                .experienceList(new ArrayList<>(nExp))
                .build();
    }

    public List<ExperienceMultiStep<V>> experienceList() {
        return experienceList;
    }

    public boolean isEmpty() {
        return nExperiences==0;
    }

    public ExperienceMultiStep<V> experienceAtStep(int step) {
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

    public void add(ExperienceMultiStep<V> experience) {
        experienceList.add(experience);
    }


}
