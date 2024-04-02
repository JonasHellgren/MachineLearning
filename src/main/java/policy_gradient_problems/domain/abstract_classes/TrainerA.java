package policy_gradient_problems.domain.abstract_classes;

import common.Conditionals;
import lombok.Getter;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.agent_interfaces.AgentI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.helpers.RecorderActionProbabilities;
import policy_gradient_problems.helpers.TrainingTracker;

import java.util.List;

import static common.Conditionals.executeIfTrue;

/**
 * The mother of all trainer classes
 * All trainers have a tracker for plotting measures and parameters
 */

@Log
@Getter
public abstract class TrainerA<V> {

    protected TrainingTracker tracker=new TrainingTracker(); //todo remove
    protected RecorderActionProbabilities recorderActionProbabilities=new RecorderActionProbabilities();
    protected TrainerParameters parameters;

    public abstract void train();
    protected abstract List<Experience<V>> getExperiences(AgentI<V> agent);

    public TrainingTracker getTracker() {  //todo remove
        logIfEmptyTracker();
        return tracker;
    }




    private void logIfEmptyTracker() {
        if (tracker.isEmpty()) {
            log.warning("Need to train first");
        }
    }

}
