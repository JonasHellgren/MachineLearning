package multi_step_temp_diff.models;

import common.Conditionals;
import common.ListUtils;
import common.RandUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import multi_step_temp_diff.interfaces.AgentInterface;
import multi_step_temp_diff.interfaces.EnvironmentInterface;
import org.apache.arrow.flatbuf.Int;
import org.apache.commons.math3.util.Pair;

import java.util.*;

@Builder
@Getter
public class AgentTabular implements AgentInterface {

    static final double DISCOUNT_FACTOR=1;
    static final Map<Integer,Double> MEMORY=new HashMap<>();
    private static final double VALUE_IF_NOT_PRESENT = 0;
    private static final int START_STATE = 0;

    @NonNull  EnvironmentInterface environment;
    @Builder.Default
    int state= START_STATE;
    @Builder.Default
    Map<Integer,Double> memory=MEMORY;
    @Builder.Default
    final double discountFactor=DISCOUNT_FACTOR;
    @Builder.Default
    private List<Pair<Integer, Double>> pairs=new ArrayList<>();

    public static AgentTabular newDefault() {
        return AgentTabular.builder()
                .environment(new ForkEnvironment())
                .build();
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public double getDiscountFactor() {
        return discountFactor;
    }

    @Override
    public void setState(int state) {
        this.state=state;
    }

    @Override
    public int chooseAction(double probRandom) {
        int state=getState();
        return (RandUtils.getRandomDouble(0,1)<probRandom)
                ? chooseRandomAction()
                : chooseBestAction(state);
    }

    @Override
    public int chooseRandomAction() {
        return RandUtils.getRandomIntNumber(0,ForkEnvironment.NOF_ACTIONS);
    }

    @Override
    public int chooseBestAction(int state) {
        pairs.clear();
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
