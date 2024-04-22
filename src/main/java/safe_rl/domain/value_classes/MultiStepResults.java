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
        List<StateI<V>> stateList,
        List<Double> valueTargetList,
        List<Double> advantageList,
        List<Action> actionAppliedList,
        List<Action> actionPolicyList,
        List<Boolean> isSafeCorrectedList
) {

    public static <V> MultiStepResults<V> create(int nExp) {
        return MultiStepResults.<V>builder()
                .nExperiences(nExp)
                .stateList(new ArrayList<>(nExp))
                .valueTargetList(new ArrayList<>(nExp))
                .advantageList(new ArrayList<>(nExp))
                .actionAppliedList(new ArrayList<>(nExp))
                .actionPolicyList(new ArrayList<>(nExp))
                .isSafeCorrectedList(new ArrayList<>(nExp))
                .build();
    }

    public StateI<V> stateAtStep(int step) {return stateList.get(step); }

    public double valueTarAtStep(int step) {return valueTargetList.get(step); }

    public double advantageAtStep(int step) {return advantageList.get(step); }

    public Action actionAppliedAtStep(int step) {return actionAppliedList.get(step); }

    public Action actionPolicyAtStep(int step) {return actionPolicyList.get(step); }

    public boolean isSafeCorrectedAtStep(int step) {return isSafeCorrectedList.get(step); }

    public void addState(StateI<V> state) {
        stateList.add(state);
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
                advantageList.size(),
                actionAppliedList.size(),
                actionPolicyList.size(),
                isSafeCorrectedList.size());
        return lengthList.stream().distinct().limit(2).count() <= 1;
    }

}
