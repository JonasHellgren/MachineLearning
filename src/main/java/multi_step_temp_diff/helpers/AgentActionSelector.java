package multi_step_temp_diff.helpers;

import common.RandUtils;
import lombok.Builder;
import lombok.NonNull;
import multi_step_temp_diff.interfaces_and_abstract.EnvironmentInterface;
import multi_step_temp_diff.models.StepReturn;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Builder
public class AgentActionSelector {

    int nofActions;
    @NonNull  EnvironmentInterface environment;
    final double discountFactor;
    Function<Integer,Double> readFunction;

    public int chooseRandomAction() {
        return RandUtils.getRandomIntNumber(0, nofActions);
    }

    public int chooseBestAction(int state) {
        List<Pair<Integer, Double>> pairs=new ArrayList<>();
        for (int a:environment.actionSet()) {
            StepReturn sr=environment.step(state,a);
            double value=sr.reward+discountFactor*readFunction.apply(sr.newState);
            pairs.add(new Pair<>(a,value));
        }
        Optional<Pair<Integer, Double>> bestPair=getPairWithHighestValue(pairs);
        return bestPair.orElseThrow().getFirst();
    }

    private  Optional<Pair<Integer, Double>> getPairWithHighestValue(List<Pair<Integer, Double>> pairs) {
        return pairs.stream().
                reduce((res, item) -> res.getSecond() > item.getSecond() ? res : item);
    }

    public int chooseAction(double probRandom,int state) {
        return (RandUtils.getRandomDouble(0, 1) < probRandom)
                ? chooseRandomAction()
                : chooseBestAction(state);
    }

}
