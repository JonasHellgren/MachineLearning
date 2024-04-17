package safe_rl.domain.memories;

import common.list_arrays.ArrayUtil;
import common.math.LinearDecoder;
import common.math.LinearFitter;
import safe_rl.domain.abstract_classes.StateI;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/***
 * Using map requires that StateI has hashCode and equals
 */

public class DisCoMemory<V> {

    public static final double VALUE_PAR_DEFAULT = 0d;
    public static final double ALPHA_LEARNING = 1e-2;
    Map<StateI<V>, double[]> stateParMap;
    int nThetaPerKey;  //one more theta than nContFeatures, one theta is bias
    LinearDecoder decoder;
    LinearFitter fitter;
    Double alphaLearning;

    public DisCoMemory(int nThetaPerKey) {
        this(nThetaPerKey, ALPHA_LEARNING);
    }

    public DisCoMemory(int nThetaPerKey, Double alphaLearning) {
        this.stateParMap = new HashMap<>();
        this.nThetaPerKey = nThetaPerKey;
        this.decoder = new LinearDecoder(nThetaPerKey - 1);
        this.fitter = new LinearFitter(alphaLearning, nThetaPerKey - 1);
        this.alphaLearning = alphaLearning;
    }

    /**
     * copy to not mess up keys in map if state changes outside
     */
    public void save(StateI<V> state, double[] thetas) {
        stateParMap.put(state.copy(), thetas);
    }

    public double read(StateI<V> state) {
        return decoder.read(state.continousFeatures(), readThetas(state));
    }

    public double[] readThetas(StateI<V> state) {
        return contains(state)
                ? stateParMap.get(state)
                : ArrayUtil.createArrayWithSameDoubleNumber(nThetaPerKey, VALUE_PAR_DEFAULT);
    }

    public void fit(StateI<V> state, double targetValue) {
        fit(state, targetValue, 1);
    }

    public void fit(StateI<V> state, double targetValue, int nFits) {
        double[] thetas = readThetas(state);
        fitter.setTheta(thetas);
        double[] features=state.continousFeatures();
        //System.out.println("thetas = " + Arrays.toString(thetas));
       // System.out.println("features = " + Arrays.toString(features));
        IntStream.range(0, nFits).forEach(i -> fitter.fit(targetValue, features));
        save(state, fitter.getTheta());
    }

    public boolean contains(StateI<V> state) {
        return stateParMap.containsKey(state);
    }

    public int size() {
        return stateParMap.size();
    }

    public Set<StateI<V>> keys() {
        return stateParMap.keySet();
    }

    @Override
    public String toString() {
        return "\n" + stateParMap.entrySet().stream()
                .map(e -> Arrays.toString(e.getKey().discretFeatures()) +
                        " = " + Arrays.toString(e.getValue()))
                .collect(Collectors.joining("\n"));

    }

}
