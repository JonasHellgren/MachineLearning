package policy_gradient_problems.common_generic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import policy_gradient_problems.abstract_classes.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Every item in lists is for specific time step
 */

@Builder
public record MultiStepResults(
        int nofSteps,
        List<List<Double>> stateValuesList,
        List<Action> actionList,
        List<Double> valuePresentList,
        List<Double> valueTarList
) {


    public static MultiStepResults create(int nofSteps) {
       return MultiStepResults.builder()
                .nofSteps(nofSteps)
                .stateValuesList(new ArrayList<>())
                .actionList(new ArrayList<>())
                .valuePresentList(new ArrayList<>())
                .valueTarList(new ArrayList<>()).build();
    }

    public void addStateValues(List<Double> values) {
        stateValuesList.add(values);
    }

    public void addAction(Action action) {
        actionList.add(action);
    }

    public void addValue(Double value) {
        valuePresentList.add(value);
    }

    public void addValueTarget(Double value) {
        valueTarList.add(value);
    }

}
