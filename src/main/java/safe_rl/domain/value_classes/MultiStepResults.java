package safe_rl.domain.value_classes;

import lombok.Builder;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.StateI;

import java.util.ArrayList;
import java.util.List;

/**
 * Data container for episode results
 * Every item in lists is for specific time step
 */

@Builder
public record MultiStepResults<V>(
        int nExperiences,  //equal to length of below lists
        List<ExperienceMultiStep<V>> experienceList
/*        List<StateI<V>> stateList,
        List<Boolean> isFutureOutsideOrTerminal,
        List<Double> valueTargetList,
        List<Double> advantageList,
        List<Action> actionAppliedList,
        List<Action> actionPolicyList,
        List<Boolean> isSafeCorrectedList*/
) {

    public static <V> MultiStepResults<V> create(int nExp) {
        return MultiStepResults.<V>builder()
                .nExperiences(nExp)
                .experienceList(new ArrayList<>(nExp))
                .build();
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


/*

    public void addState(StateI<V> state) {
        stateList.add(state);
    }

    public void addIsFutureOutsideOrTerminal(boolean isOut) {
        isFutureOutsideOrTerminal.add(isOut);
    }

    public void addValueTarget(double valTar) {
        valueTargetList.add(valTar);
    }

    public void addAdvantage(double adv) {
        advantageList.add(adv);
    }

    public void addActionApplied(Action action) {
        actionAppliedList.add(action);
    }

    public void addActionPolicy(Action action) {
        actionPolicyList.add(action);
    }

    public void addIsSafeCorrect(boolean isCorrected) {
        isSafeCorrectedList.add(isCorrected);
    }

    public boolean isEqualListLength() {
        List<Integer> lengthList=List.of(
                stateList.size(),
                valueTargetList.size(),
                isFutureOutsideOrTerminal.size(),
                advantageList.size(),
                actionAppliedList.size(),
                actionPolicyList.size(),
                isSafeCorrectedList.size());
        return lengthList.stream().distinct().limit(2).count() <= 1;
    }
*/

}
