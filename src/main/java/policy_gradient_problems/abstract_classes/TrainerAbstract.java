package policy_gradient_problems.abstract_classes;

import lombok.extern.java.Log;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.common.Experience;
import policy_gradient_problems.common.TrainerParameters;
import policy_gradient_problems.common.TrainingTracker;
import policy_gradient_problems.sink_the_ship.StepReturnShip;

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

    private void logging(Experience experience, RealVector changeInThetaVector) {
        System.out.println("experience = " + experience +
                ", changeInThetaVector = " + changeInThetaVector);
    }

}
