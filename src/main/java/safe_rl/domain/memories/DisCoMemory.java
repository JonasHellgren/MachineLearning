package safe_rl.domain.memories;

import com.google.common.base.Preconditions;
import common.list_arrays.ArrayUtil;
import common.math.LinearDecoder;
import common.math.LinearFitter;
import safe_rl.domain.abstract_classes.StateI;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/***
 * Using map requires that StateI has hashCode and equals
 */

public class DisCoMemory<V> {

    public static final double VALUE_PAR_DEFAULT = 0d;
    public static final double ALPHA_LEARNING = 1e-2;
    public static final double DELTA_BETA_MAX = 1d;
    public static final double LOSS_INIT = 0;
    Map<StateI<V>, double[]> stateParMap;
    int nThetaPerKey;  //one more theta than nContFeatures, one theta is bias
    LinearDecoder decoder;
    LinearFitter fitter;
    Double alphaLearning;
    double lossLastUpdate = LOSS_INIT;

    public DisCoMemory(int nThetaPerKey) {
        this(nThetaPerKey, ALPHA_LEARNING, DELTA_BETA_MAX);
    }

    public DisCoMemory(int nThetaPerKey, Double alphaLearning, double deltaBetaMax) {
        Preconditions.checkArgument(nThetaPerKey > 0, "To small nThetaPerKey");
        this.stateParMap = new HashMap<>();
        this.nThetaPerKey = nThetaPerKey;
        this.decoder = new LinearDecoder(nThetaPerKey - 1);
        this.fitter = new LinearFitter(alphaLearning, deltaBetaMax, nThetaPerKey - 1);
        this.alphaLearning = alphaLearning;
    }

    public double lossLastUpdate() {
        return lossLastUpdate;
    }

    /**
     * copy to not mess up keys in map if state changes outside
     */
    public void save(StateI<V> state, double[] thetas) {
        stateParMap.put(state.copy(), thetas);
    }

    public double read(StateI<V> state) {
        return decoder.read(state.continuousFeatures(), readThetas(state));
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
        double[] features = state.continuousFeatures();
        IntStream.range(0, nFits).forEach(i -> fitter.fit(targetValue, features));
        save(state, fitter.getTheta());
    }

    public void fitFromError(StateI<V> state, double error) {
        fitFromError(state, error, 1);
    }

    public void fitFromError(StateI<V> state, double error, int nFits) {
        fitter.setTheta(readThetas(state));
        lossLastUpdate = 0;
        IntStream.range(0, nFits).forEach(i -> {
            double loss = fitter.fitFromError(state.continuousFeatures(), error);
            lossLastUpdate += loss;
        });
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
        return toStringWithValueMapper(arr -> arr);
    }

    public String toStringWithValueMapper(UnaryOperator<double[]> valueMapper) {
        return "\n" + stateParMap.entrySet().stream()
                .map(e -> Arrays.toString(e.getKey().discreteFeatures()) +
                        " = " + Arrays.toString(valueMapper.apply(e.getValue())))
                .collect(Collectors.joining("\n"));
    }

}
