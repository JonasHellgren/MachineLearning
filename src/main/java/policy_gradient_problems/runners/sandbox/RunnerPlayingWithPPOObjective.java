package policy_gradient_problems.runners.sandbox;

import common.BestPairFinder;
import common.ListUtils;
import org.apache.commons.lang3.tuple.Pair;
import plotters.PlotterMultiplePanelsPairs;
import policy_gradient_problems.helpers.PPOHelper;
import policy_gradient_problems.helpers.SoftMaxEvaluator;

import java.util.*;

public class RunnerPlayingWithPPOObjective {

    public static final double EPSILON = 0.2;
    public static final double A0 = -0.5, A1 = -0.3;
    public static final double THETA0OLD = 0d, THETA1OLD=THETA0OLD;
    static  List<Double> theta0List=ListUtils.createDoubleListStartEndStep(-1d,1d,0.25);
    static  PPOHelper helper=PPOHelper.newDefault();

    public static void main(String[] args) {

        var prob01Old= SoftMaxEvaluator.getProbabilities(List.of(THETA0OLD,-THETA1OLD));
        List<Pair<Double,Double>> pairList=new ArrayList<>();
        var advantages=List.of(A0, A1);
        for (double theta0:theta0List) {
            double objective = getObjective(prob01Old, advantages, theta0);
            pairList.add(Pair.of(theta0, objective));
        }

        printNewProbabilities(pairList);
        plotTheta0VsObjective(pairList);

    }

    private static void printNewProbabilities(List<Pair<Double, Double>> pairList) {
        Optional<Pair<Double, Double>> bestPair= BestPairFinder.maxPairDoubles(pairList);
        Double theta0 = bestPair.orElseThrow().getLeft();
        var prob01= SoftMaxEvaluator.getProbabilities(List.of(theta0,-theta0));
        System.out.println("prob01 = " + prob01);
    }

    private static void plotTheta0VsObjective(List<Pair<Double, Double>> pairList) {
        Optional<Pair<Double, Double>> bestPair= BestPairFinder.maxPairDoubles(pairList);
        String xLabel = "theta0 ,best=" + bestPair.orElseThrow().getLeft();

        var settings=PlotterMultiplePanelsPairs.Settings.builder()
                .width(300).height(200)
                .titleList(List.of("Eps="+EPSILON+", A0 ="+A0+", A1 ="+A1))
                .xLabelList(List.of(xLabel)).yLabelList(List.of("PPO obj"))
                .build();

        var plotter=new PlotterMultiplePanelsPairs(settings); //xLabel,"PPO obj"
        plotter.plot(List.of(pairList));
    }

    private static double getObjective(List<Double> prob01Old, List<Double> advantages, double theta0) {

        double theta1=-theta0;

        List<Double> prob01= SoftMaxEvaluator.getProbabilities(List.of(theta0,theta1));
        List<Double> ratios = PPOHelper.divideListElements(prob01Old, prob01);
        List<Double> objectives=new ArrayList<>();
        var advantageIterator= advantages.iterator();
        for (double ratio:ratios) {
            objectives.add(helper.getPPOObjective(ratio, advantageIterator.next(), EPSILON));
        }
        return ListUtils.sumList(objectives);
    }


}
