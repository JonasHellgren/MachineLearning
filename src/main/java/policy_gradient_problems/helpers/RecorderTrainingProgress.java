package policy_gradient_problems.helpers;

import policy_gradient_problems.domain.value_classes.ProgressMeasures;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * During training, it is good to keep track of nof steps, accumulated rewards per episode, etc
 * This class is for recording, and later enabling plotting, of such measures
 */

public class RecorderTrainingProgress {

    List<ProgressMeasures> measuresList;

    public RecorderTrainingProgress() {
        this.measuresList=new ArrayList<>();
    }

    public void clear() {
        measuresList.clear();
    }

    public void add(ProgressMeasures measures) {
        measuresList.add(measures);
    }

    public int size() {
        return measuresList.size();
    }

    public boolean isEmpty() {
        return measuresList.isEmpty();
    }

    public List<Integer> nStepsTraj() {
        return measuresList.stream().map(tm -> tm.nSteps()).toList();
    }

    public List<Double> sumRewardsTraj() {
        return getMeasure(ProgressMeasures::sumRewards);
    }

    public List<Double> criticLossTraj() {
        return getMeasure(ProgressMeasures::criticLoss);
    }

    public List<Double> actorLossTraj() {
        return getMeasure(ProgressMeasures::actorLoss);
    }

    List<Double> getMeasure(Function<ProgressMeasures, Double> function)  {
        return measuresList.stream().map(function).toList();
    }

}
