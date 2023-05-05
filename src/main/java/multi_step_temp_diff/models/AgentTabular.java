package multi_step_temp_diff.models;

import common.Conditionals;
import common.RandUtils;
import lombok.Builder;
import lombok.NonNull;
import multi_step_temp_diff.interfaces.AgentInterface;
import multi_step_temp_diff.interfaces.EnvironmentInterface;
import org.apache.commons.math3.util.Pair;

import java.util.*;

@Builder
public class AgentTabular implements AgentInterface {

    static final double DISCOUNT_FACTOR=1;
    static final Map<Integer,Double> MEMORY=new HashMap<>();
    private static final double VALUE_IF_NOT_PRESENT = 0;

    @NonNull  EnvironmentInterface environment;
    int state;
    @Builder.Default
    Map<Integer,Double> memory=MEMORY;
    @Builder.Default
    final double discountFactor=DISCOUNT_FACTOR;

    @Override
    public int getState() {
        return state;
    }

    @Override
    public int chooseAction(int state, double probRandom) {
        return (RandUtils.getRandomDouble(0,1)<probRandom)
                ? chooseRandomAction()
                : chooseBestAction(state);
    }

    @Override
    public int chooseRandomAction() {
        return environment.actionSet().stream().findAny().orElseThrow();
    }

    @Override
    public int chooseBestAction(int state) {
        List<Pair<Integer, Double>> pairs=new ArrayList<>();
        for (int a:environment.actionSet()) {
            StepReturn sr=environment.step(state,a);
            double value=sr.reward+discountFactor*readValue(sr.newState);
            pairs.add(new Pair<>(a,value));
        }
        Optional<Pair<Integer, Double>> bestPair=getPairWithHighestValue(pairs);
        return bestPair.orElseThrow().getFirst();
    }

    private  Optional<Pair<Integer, Double>> getPairWithHighestValue(List<Pair<Integer, Double>> pairs) {
        return pairs.stream().
                reduce((res, item) -> res.getSecond() > item.getSecond() ? res : item);
    }

    @Override
    public void updateState(StepReturn stepReturn) {
        state=stepReturn.newState;
    }

    @Override
    public double readValue(int state) {
        return memory.getOrDefault(state, VALUE_IF_NOT_PRESENT);
    }

    public void writeValue(int state, double value) {
        memory.put(state,value);
    }
}
