package policy_gradient_problems.helpers;

import policy_gradient_problems.domain.value_classes.TrainingMeasures;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RecorderTrainingMeasures {

    List<TrainingMeasures> measuresList;

    public RecorderTrainingMeasures() {
        this.measuresList=new ArrayList<>();
    }

    public void clear() {
        measuresList.clear();
    }

    public void addMeasures(TrainingMeasures measures) {
        measuresList.add(measures);
    }

    public int size() {
        return measuresList.size();
    }

    public List<Integer> nStepsTraj() {
        return measuresList.stream().map(tm -> tm.nSteps()).toList();
    }

    public List<Double> sumRewardsTraj() {
        return getMeasure(TrainingMeasures::sumRewards);
    }

    public List<Double> criticLossTraj() {
        return getMeasure(TrainingMeasures::criticLoss);
    }

    public List<Double> actorLossTraj() {
        return getMeasure(TrainingMeasures::actorLoss);
    }

    List<Double> getMeasure(Function<TrainingMeasures, Double> function)  {
        return measuresList.stream().map(function).toList();
    }

}
