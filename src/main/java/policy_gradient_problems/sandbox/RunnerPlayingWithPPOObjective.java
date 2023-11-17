package policy_gradient_problems.sandbox;

import EDU.oswego.cs.dl.util.concurrent.SyncList;
import common.ListUtils;
import common.MathUtils;
import org.apache.commons.math3.util.Pair;
import policy_gradient_problems.common.SoftMaxEvaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunnerPlayingWithPPOObjective {

    static  List<Double> theta0List=List.of(-1d,-0.5,0d,0.5,1d);

    public static void main(String[] args) {

        List<Double> prob01Old= SoftMaxEvaluator.getProbabilities(List.of(0d,0d));

        List<List<Double>> probsList=new ArrayList<>();
        List<List<Double>> ratiosList=new ArrayList<>();
        Map<Double,Double> theta0ObjectivesMap=new HashMap<>();

        double epsilon = 0.2;
        List<Double> advantages=List.of(0.5,-0.3);

        for (double theta0:theta0List) {
            double theta1=-theta0;
            List<Double> prob01= SoftMaxEvaluator.getProbabilities(List.of(theta0,theta1));
            probsList.add(prob01);

            List<Double> ratios=prob01.stream()
                    .map(p -> { int i= prob01.indexOf(p); return  prob01.get(i)/prob01Old.get(i); })
                    .toList();

            ratiosList.add(ratios);

            List<Double> objectives=new ArrayList<>();
            var advantageIterator=advantages.iterator();
            for (double ratio:ratios) {
                objectives.add(getPPOObjective(ratio, advantageIterator.next(), epsilon));
            }
            theta0ObjectivesMap.put(theta0,ListUtils.sumList(objectives));

        }

        probsList.forEach(System.out::println);
        ratiosList.forEach(System.out::println);
        theta0ObjectivesMap.entrySet().forEach(System.out::println);

    }

    static  double getPPOObjective(double ratio, double advantage, double epsilon) {
        return Math.min(ratio*advantage, MathUtils.clip(ratio,1-epsilon,1+epsilon)*advantage);
    }

}
