package safe_rl.domain.simulator.value_objects;

import common.list_arrays.ListUtils;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.environment.aggregates.StateI;

import java.util.List;

/**
 * Used by simulator
 */

public record SimulationResult<V>(
        StateI<V> state,
        double reward,
        Action action,
        Action actionCorrected

) {


    public static <V> double sumRewards(List<SimulationResult<V>> simResList) {
        return simResList.stream().map(sr -> sr.reward).reduce(0d,Double::sum);
    }

    public static <V> double meanActionChange(List<SimulationResult<V>> simResList) {
        var acList= simResList.stream().map(sr -> sr.getActionChange()).toList();
        return ListUtils.findAverage(acList).orElseThrow();
    }


    public  double getActionChange() {
        return Math.abs(actionCorrected.asDouble()- action.asDouble());
    }


    public static <V> void print(List<SimulationResult<V>> simResList) {
        simResList.forEach(System.out::println);
        System.out.println("sumRew = " + sumRewards(simResList));
    }

}
