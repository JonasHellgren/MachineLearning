package safe_rl.domain.memories;

import common.list_arrays.ArrayUtil;
import safe_rl.domain.abstract_classes.StateI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/***
 * Using map requires that StateI has hashCode and equals
 */

public class DisCoMemory<V> {

    public static final double VALUE_PAR_DEFAULT = 0d;
    Map<StateI<V>, double[]> stateParMap;
    int nParams;

    public DisCoMemory(int nThetaPerKey) {
        this.stateParMap = new HashMap<>();
        this.nParams = nThetaPerKey;
    }

    public void save(StateI<V> state, double[] thetas) {
        stateParMap.put(state, thetas);
    }

    public double[] read(StateI<V> state) {
        return contains(state)
                ? stateParMap.get(state)
                : ArrayUtil.createArrayWithSameDoubleNumber(nParams, VALUE_PAR_DEFAULT);
    }

    public boolean contains(StateI<V> state) {
        return stateParMap.containsKey(state);
    }

    public int size() {
        return stateParMap.size();
    }

    @Override
    public String toString() {
        return "\n" + stateParMap.entrySet().stream()
                .map(e -> Arrays.toString(e.getKey().discretFeatures()) +
                        " = " + Arrays.toString(e.getValue()))
                .collect(Collectors.joining("\n"));

    }

}
