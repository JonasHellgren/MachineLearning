package policy_gradient_problems.twoArmedBandit;

import common.IndexFinder;
import common.ListUtils;
import common.RandUtils;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.common.SoftMaxEvaluator;

import java.util.ArrayList;
import java.util.List;

@Builder
public class Agent {

    public static final double THETA0 = 0.5, THETA1 = 0.5;
    public static final int NOF_ACTIONS = 2;
    @Builder.Default
    List<Double> thetaList = List.of(THETA0, THETA1);
    @Builder.Default
    int nofActions = NOF_ACTIONS;

    public static Agent newDefault() {
        return Agent.builder().build();
    }


    public int chooseAction() {
        double[] piTheta = piTheta();
        List<Double> limits = getLimits(piTheta);
        throwIfBadLimits(limits);
        return IndexFinder.findBucket(ListUtils.toArray(limits), RandUtils.getRandomDouble(0, 1));
    }

    public void setThetaList(List<Double> thetaList) {
        this.thetaList = thetaList;
    }

    public double[] getThetasAsArray() {
        return ListUtils.toArray(thetaList);
    }

  public double[] piTheta() {  //action probabilities according to soft max
      return  SoftMaxEvaluator.getProbabilities(thetaList);
    }

    @NotNull
    private static List<Double> getLimits(double[] piTheta) {
        List<Double> limits = new ArrayList<>();
        limits.add(0d);
        double accumProb = 0;
        for (double prob : piTheta) {
            accumProb += prob;
            limits.add(accumProb);
        }
        return limits;
    }

    private static void throwIfBadLimits(List<Double> limits) {
        if (ListUtils.findMin(limits).orElseThrow() < 0d || ListUtils.findMin(limits).orElseThrow() > 1) {
            throw new RuntimeException("Bad action probabilities");
        }
    }


    public double[] getGradLogList(int action) {
        return (action==0)
                ? new double[]{piTheta()[1],-piTheta()[1]}
                : new double[]{-piTheta()[0],piTheta()[0]};
    }
}
