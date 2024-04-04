package policy_gradient_problems.helpers;

import policy_gradient_problems.domain.value_classes.ProgressMeasures;

import java.util.List;

/**
 * This class is for during training keep track of evaluation runs
 * These runs typically starts in a "stable" start state
 * Similar to RecorderTrainingProgress but another use case, hence wrapping RecorderTrainingProgress
 */

public class RecorderEvaluationProgress {

    RecorderTrainingProgress recorderTrainingProgress;

    public RecorderEvaluationProgress() {
        this.recorderTrainingProgress=new RecorderTrainingProgress();
    }

    public void add(ProgressMeasures measures) {
        recorderTrainingProgress.add(measures);
    }

    public int size() {
        return recorderTrainingProgress.size();
    }

    public boolean isEmpty() {
        return recorderTrainingProgress.isEmpty();
    }

    public List<Integer> nStepsTraj() {
        return recorderTrainingProgress.nStepsTraj();
    }

    public List<Double> sumRewardsTraj() {
        return recorderTrainingProgress.sumRewardsTraj();
    }


}
