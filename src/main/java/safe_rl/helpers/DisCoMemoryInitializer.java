package safe_rl.helpers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import common.list_arrays.ListUtils;
import common.math.LinearFitter;
import common.math.MovingAverage;
import common.other.*;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.memories.DisCoMemory;
import java.util.ArrayList;
import java.util.List;

/**
 * Fills DiscoMemory with init values
 * MovingAverage/filteredError is to ensure not a single low error prematurely breaks the loop
 */

@Log
public class DisCoMemoryInitializer<V> {

    public static final int N_ITER_MAX = 1000;
    public static final double TOL_VAL = 1e-3;
    public static final double ALPHA = 1e-1;
    public static final int LENGTH_AVG_WINDOW = 100;
    DisCoMemory<V> memory;
    List<List<Double>> discreteFeatSet;
    Pair<List<Double>, List<Double>> contFeatMinMax;
    Pair<Double,Double> valTarMeanStd;
    StateI<V> state;  //state to play with
    Integer nIterMax;
    Double tolValueFitting;
    Integer lengthMeanAvgWindow;

    int nContinuousFeatures;
    LinearFitter fitter;
    NormDistributionSampler sampler;

    @Builder
    public DisCoMemoryInitializer(@NonNull  DisCoMemory<V> memory,
                                  @NonNull List<List<Double>> discreteFeatSet,
                                  @NonNull Pair<List<Double>, List<Double>> contFeatMinMax,
                                  @NonNull Pair<Double, Double> valTarMeanStd,
                                  @NonNull StateI<V> state,
                                  Integer nIterMax,
                                  Double tolValueFitting,
                                  Integer lengthMeanAvgWindow,
                                  Double alphaLearning) {
        Preconditions.checkArgument(!discreteFeatSet.isEmpty(),"Empty discreteFeatSet");
        Preconditions.checkArgument(contFeatMinMax.getFirst().size()==contFeatMinMax.getSecond().size(),
                "Not equal length contFeatMinMax");

        this.memory = memory;
        this.discreteFeatSet = discreteFeatSet;
        this.contFeatMinMax = contFeatMinMax;
        this.valTarMeanStd = valTarMeanStd;
        this.state=state;
        this.nIterMax = MyFunctions.defaultIfNullInteger.apply(nIterMax, N_ITER_MAX);
        this.tolValueFitting = MyFunctions.defaultIfNullDouble.apply(tolValueFitting, TOL_VAL);
        this.lengthMeanAvgWindow= MyFunctions.defaultIfNullInteger.apply(lengthMeanAvgWindow, LENGTH_AVG_WINDOW);
        double alpha=MyFunctions.defaultIfNullDouble.apply(alphaLearning, ALPHA);
        this.nContinuousFeatures =contFeatMinMax.getFirst().size();
        this.fitter=new LinearFitter(alpha, nContinuousFeatures);
        this.sampler=new NormDistributionSampler();
    }

    public void initialize() {
        var featureCombinations = Lists.cartesianProduct(discreteFeatSet);
        for (List<Double> combo:featureCombinations) {
            var stateCopy=state.copy();
            stateCopy.setDiscretFeatures(combo.stream().mapToInt(Number::intValue).toArray());
            fitter.setTheta(memory.readThetas(stateCopy));
            double[] thetas= identifyFittedTheta(fitter);
            memory.save(stateCopy, thetas);
        }
    }

    private double[] identifyFittedTheta(LinearFitter fitter) {
        double errorFiltered=Double.MAX_VALUE;
        var initErrorList = ListUtils.createListWithEqualElementValues(lengthMeanAvgWindow, getError(getFeatures()));
        var movingAverage=new MovingAverage(lengthMeanAvgWindow, initErrorList);
        var counter=new Counter();
        while (isPredictionErrorAboveToleranceAndNotToManyIterations(errorFiltered, counter)) {
            double[] features = getFeatures();
            fitThetasInFitter(fitter, features);
            movingAverage.addValue(getError(features));
            errorFiltered = movingAverage.average();
            counter.increase();
        }
        someLogging(errorFiltered, counter);
        return fitter.getTheta();
    }

    private void fitThetasInFitter(LinearFitter fitter, double[] features) {
        double targetValue = sampler.sampleFromNormDistribution(valTarMeanStd);
        fitter.fit(targetValue, features);
    }

    private boolean isPredictionErrorAboveToleranceAndNotToManyIterations(double errorFiltered, Counter counter) {
        return errorFiltered > tolValueFitting && counter.getCount() < nIterMax;
    }

    private void someLogging(double errorFiltered, Counter counter) {
        log.fine("Nof iter="+ counter);
        Conditionals.executeIfTrue(counter.getCount()==nIterMax,
                () -> log.warning("Nof iterations exceeded, probably tough tolValueFitting, error="+ errorFiltered));
    }

    private double[] getFeatures() {
        var minContFeat=contFeatMinMax.getFirst();
        var maxContFeat=contFeatMinMax.getSecond();
        return ListUtils.toArray(getContFeat(minContFeat, maxContFeat));
    }

    private double getError(double[] x) {
        return Math.abs(valTarMeanStd.getFirst() - fitter.predict(x));
    }

    @NotNull
    private List<Double> getContFeat(List<Double> minContFeat, List<Double> maxContFeat) {
        List<Double> contFeat=new ArrayList<>(nContinuousFeatures);
        for (int i = 0; i < nContinuousFeatures; i++) {
            double val= RandUtils.getRandomDouble(minContFeat.get(i), maxContFeat.get(i));
            contFeat.add(val);
        }
        return contFeat;
    }


}
