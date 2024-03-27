package policy_gradient_problems.domain.value_classes;

import lombok.Builder;
import policy_gradient_problems.domain.abstract_classes.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Data container for episode results
 * Every item in lists is for specific time step
 */

@Builder
public record MultiStepResults(
        int tEnd,
        int nofSteps,
        List<List<Double>> stateValuesList,
        List<Action> actionList,
        List<Double> probActionList,
        List<Double> valuePresentList,
        List<Double> valueTarList
) {


    public static MultiStepResults create(int tEnd, int nofSteps) {
        return MultiStepResults.builder()
                .tEnd(tEnd)
                .nofSteps(nofSteps)
                .stateValuesList(new ArrayList<>())
                .actionList(new ArrayList<>())
                .probActionList(new ArrayList<>())
                .valuePresentList(new ArrayList<>())
                .valueTarList(new ArrayList<>()).build();
    }

    public void addStateValues(List<Double> values) {
        stateValuesList.add(values);
    }

    public void addAction(Action action) {
        actionList.add(action);
    }

    public void addProbAction(Double probAction) {
        probActionList.add(probAction);
    }


    public void addPresentValue(Double value) {
        valuePresentList.add(value);
    }

    public void addValueTarget(Double value) {
        valueTarList.add(value);
    }

}
