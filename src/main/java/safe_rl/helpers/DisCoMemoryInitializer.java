package safe_rl.helpers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import common.list_arrays.ListUtils;
import common.math.LinearFitter;
import common.other.Counter;
import common.other.MyFunctions;
import common.other.NormDistributionSampler;
import common.other.RandUtils;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.memories.DisCoMemory;

import java.util.ArrayList;
import java.util.List;

public class DisCoMemoryInitializer<V> {

    public static final int N_ITER_MAX = 1000;
    public static final double TOL_VAL = 0.1;
    public static final double ALPHA = 1e-3;
    DisCoMemory<V> memory;
    List<List<Double>> discreteFeatSet;
    Pair<List<Double>, List<Double>> contFeatMinMax;
    Pair<Double,Double> valTarMeanStd;
    StateI<V> state;  //state to play with
    Integer nIterMax;
    Double tolValueFitting;

    int nContFeat;
    LinearFitter fitter;
    NormDistributionSampler sampler;

    public DisCoMemoryInitializer(@NonNull  DisCoMemory<V> memory,
                                  @NonNull List<List<Double>> discreteFeatSet,
                                  @NonNull Pair<List<Double>, List<Double>> contFeatMinMax,
                                  @NonNull Pair<Double, Double> valTarMeanStd,
                                  @NonNull StateI<V> state,
                                  Integer nIterMax,
                                  Double tolValueFitting,
                                  Double alphaLearning) {
        Preconditions.checkArgument(discreteFeatSet.size()>0,"Empty discreteFeatSet");
        Preconditions.checkArgument(contFeatMinMax.getFirst().size()==contFeatMinMax.getSecond().size(),
                "Not equal length contFeatMinMax");

        this.memory = memory;
        this.discreteFeatSet = discreteFeatSet;
        this.contFeatMinMax = contFeatMinMax;
        this.valTarMeanStd = valTarMeanStd;
        this.state=state;
        this.nIterMax = MyFunctions.defaultIfNullInteger.apply(nIterMax, N_ITER_MAX);
        this.tolValueFitting = MyFunctions.defaultIfNullDouble.apply(tolValueFitting, TOL_VAL);
        double alpha=MyFunctions.defaultIfNullDouble.apply(alphaLearning, ALPHA);
        this.nContFeat =contFeatMinMax.getFirst().size();
        this.fitter=new LinearFitter(alpha, nContFeat);
        this.sampler=new NormDistributionSampler();
    }

    public void fit() {
        List<List<Double>> products = Lists.cartesianProduct(discreteFeatSet);

        for (List<Double> combo:products) {
            System.out.println("combo = " + combo);

            List<Double> minContFeat=contFeatMinMax.getFirst();
            List<Double> maxContFeat=contFeatMinMax.getFirst();


            state.setDiscretFeatures(combo.stream().mapToInt(Number::intValue).toArray());
            double[] theta=memory.read(state);
            System.out.println("theta = " + theta);
            fitter.setTheta(theta);

            double error=Double.MAX_VALUE;
            Counter counter=new Counter();
            while (error>tolValueFitting && counter.getCount()> nIterMax) {
                List<Double> contFeat = getContFeat(minContFeat, maxContFeat);
                System.out.println("contFeat = " + contFeat);
                double[] x = ListUtils.toArray(contFeat);
                fitter.fit(sampler.sampleFromNormDistribution(valTarMeanStd), x);
                error=valTarMeanStd.getFirst()-fitter.predict(x);
                counter.increase();
            }
            System.out.println("error = " + error);

            memory.save(state, fitter.getTheta());
        }

    }

    @NotNull
    private List<Double> getContFeat(List<Double> minContFeat, List<Double> maxContFeat) {
        List<Double> contFeat=new ArrayList<>(nContFeat);
        for (int i = 0; i < nContFeat; i++) {
            double val= RandUtils.getRandomDouble(minContFeat.get(i), maxContFeat.get(i));
            contFeat.add(val);
        }
        return contFeat;
    }


}
