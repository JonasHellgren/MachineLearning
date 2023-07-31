package multi_step_temp_diff.domain.agent_parts;

import common.RandUtils;
import lombok.Builder;
import lombok.NonNull;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Builder
public class AgentActionSelector<S> {

    record ActionAndValue(int action, double value) {  }

    int nofActions;
    @NonNull  EnvironmentInterface<S> environment;
    final double discountFactor;
    Function<StateInterface<S>,Double> readFunction;

    public int chooseRandomAction() {
        return RandUtils.getRandomIntNumber(0, nofActions);
    }

    public int chooseBestAction(StateInterface<S> state) {
        List<ActionAndValue> actionAndValueList=new ArrayList<>();
        for (int a:environment.actionSet()) {
            StepReturn<S> sr=environment.step(state,a);
            double value=sr.reward+discountFactor*readFunction.apply(sr.newState);
            actionAndValueList.add(new ActionAndValue(a,value));
        }
        Optional<ActionAndValue> bestPair= getActionAndValueWithHighestValue(actionAndValueList);
        return bestPair.orElseThrow().action;
    }

    private  Optional<ActionAndValue> getActionAndValueWithHighestValue(List<ActionAndValue> actionAndValueList) {
        return actionAndValueList.stream().
                reduce((res, item) -> res.value() > item.value() ? res : item);
    }

    public int chooseAction(double probRandom,StateInterface<S> state) {
        return (RandUtils.getRandomDouble(0, 1) < probRandom)
                ? chooseRandomAction()
                : chooseBestAction(state);
    }

}
