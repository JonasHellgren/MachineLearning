package policy_gradient_problems.abstract_classes;

import lombok.extern.java.Log;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.common_value_classes.ExperienceDiscreteAction;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.common.TrainingTracker;

/**
 * The mother of all trainer classes
 * All trainers have a tracker for plotting measures and parameters
 */

@Log
public abstract class TrainerAbstract {

    protected TrainingTracker tracker=new TrainingTracker();
    protected TrainerParameters parameters;

    public TrainingTracker getTracker() {
        logIfEmptyTracker();
        return tracker;
    }

    private void logIfEmptyTracker() {
        if (tracker.isEmpty()) {
            log.warning("Need to train first");
        }
    }

    private void logging(ExperienceDiscreteAction experience, RealVector changeInThetaVector) {
        System.out.println("experience = " + experience +
                ", changeInThetaVector = " + changeInThetaVector);
    }

}
