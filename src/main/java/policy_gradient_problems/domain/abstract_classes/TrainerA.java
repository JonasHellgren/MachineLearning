package policy_gradient_problems.domain.abstract_classes;

import lombok.extern.java.Log;
import policy_gradient_problems.domain.agent_interfaces.AgentI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.helpers.TrainingTracker;

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
    protected abstract List<Experience<V>> getExperiences(AgentI<V> agent);

    public TrainingTracker getTracker() {
        logIfEmptyTracker();
        return tracker;
    }

    private void logIfEmptyTracker() {
        if (tracker.isEmpty()) {
            log.warning("Need to train first");
        }
    }

}
