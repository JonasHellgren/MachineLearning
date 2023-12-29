package policy_gradient_problems.abstract_classes;

import lombok.extern.java.Log;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_value_classes.ExperienceOld;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.common.TrainingTracker;
import policy_gradient_problems.the_problems.twoArmedBandit.VariablesBandit;

import java.util.List;

/**
 * The mother of all trainer classes
 * All trainers have a tracker for plotting measures and parameters
 */

@Log
public abstract class TrainerA<V> {

    protected TrainingTracker tracker=new TrainingTracker();
    protected TrainerParameters parameters;

    public abstract void train();
    public abstract List<Experience<V>> getExperiences(AgentI<V> agent);

    public TrainingTracker getTracker() {
        logIfEmptyTracker();
        return tracker;
    }

    private void logIfEmptyTracker() {
        if (tracker.isEmpty()) {
            log.warning("Need to train first");
        }
    }

    private void logging(ExperienceOld experience, RealVector changeInThetaVector) {
        System.out.println("experience = " + experience +
                ", changeInThetaVector = " + changeInThetaVector);
    }

}
