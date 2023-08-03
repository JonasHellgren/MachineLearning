package multi_step_temp_diff.domain.agent_parts;

import common.Conditionals;
import common.MathUtils;
import common.RandUtils;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Builder
@Log
public class AgentActionSelector<S> {

    public static final double TOLERANCE = 1e-5;
    public static final String LOG_MESSAGE_MULTIPLE_MATCHES =
            "Multiple matches in action selection, choosing random action = ";

    int nofActions;
    @Builder.Default
    double toleranceSameValue=TOLERANCE;
    @NonNull EnvironmentInterface<S> environment;
    final double discountFactor;
    Function<StateInterface<S>, Double> readMemoryFunction;

    public int chooseRandomAction() {
        return RandUtils.getRandomIntNumber(0, nofActions);
    }

    record ActionAndValue(int action, double value) {
    }
    public int chooseBestAction(StateInterface<S> state) {
        List<ActionAndValue> actionAndValueList = getActionAndValueList(state);
        ActionAndValue actionAndValue = getActionAndValueWithHighestValue(actionAndValueList);
        Predicate<ActionAndValue> condition =
                p -> MathUtils.compareDoubleScalars(p.value(), actionAndValue.value(), toleranceSameValue);
        long nofMatches = actionAndValueList.stream().filter(condition).count();
        ActionAndValue actionAndValueChosen = (nofMatches > 1)
                ? actionAndValueList.stream()
                .filter(condition).toList().get(RandUtils.getRandomIntNumber(0, (int) nofMatches))
                : actionAndValue;
        maybeLog(nofMatches, actionAndValueChosen);
        return actionAndValueChosen.action;
    }

    private static void maybeLog(long nofMatches, ActionAndValue actionAndValueChosen) {
        Conditionals.executeIfTrue(nofMatches > 1,
                () -> log.fine(LOG_MESSAGE_MULTIPLE_MATCHES + actionAndValueChosen.action));
    }

    @NotNull
    private List<ActionAndValue> getActionAndValueList(StateInterface<S> state) {
        List<ActionAndValue> actionAndValueList = new ArrayList<>();
        for (int a : environment.actionSet()) {
            StepReturn<S> sr = environment.step(state, a);
            double value = sr.reward + discountFactor * readMemoryFunction.apply(sr.newState);
            actionAndValueList.add(new ActionAndValue(a, value));
        }
        return actionAndValueList;
    }

    private ActionAndValue getActionAndValueWithHighestValue(List<ActionAndValue> actionAndValueList) {
        return actionAndValueList.stream()
                .max(Comparator.comparing(ActionAndValue::value)).orElseThrow();

    }

    public int chooseAction(double probRandom, StateInterface<S> state) {
        return (RandUtils.getRandomDouble(0, 1) < probRandom)
                ? chooseRandomAction()
                : chooseBestAction(state);
    }

}
