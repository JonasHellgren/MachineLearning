package safe_rl.domain.value_classes;

import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.StateI;

import java.util.List;

/**
 * Used by simulator
 */

public record SimulationResult<V>(
        StateI<V> state,
        double reward,
        Action action
) {


    public static <V> double sumRewards(List<SimulationResult<V>> simResList) {
        return simResList.stream().map(sr -> sr.reward).reduce(0d,Double::sum);
    }

    public static <V> void print(List<SimulationResult<V>> simResList) {
        simResList.forEach(System.out::println);
        System.out.println("sumRew = " + sumRewards(simResList));
    }

}
