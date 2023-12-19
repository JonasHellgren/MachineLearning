package policy_gradient_problems.the_problems.twoArmedBandit;

import lombok.extern.java.Log;
import policy_gradient_problems.abstract_classes.AgentInterface;
import policy_gradient_problems.abstract_classes.TrainerAbstract;
import policy_gradient_problems.common.TrainingTracker;
import policy_gradient_problems.common_value_classes.ExperienceDiscreteAction;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.ArrayList;
import java.util.List;

@Log
public class TrainerBanditAbstract extends TrainerAbstract {

    public static final double DUMMY_VALUE = 0d;
    public static final int STATE_DUMMY = 0;

     EnvironmentBandit environment;

    public TrainerBanditAbstract(EnvironmentBandit environment,
                                 TrainerParameters parameters) {
        this.environment = environment;
        super.parameters=parameters;
    }


    public List<ExperienceDiscreteAction> getExperiences(AgentInterface agent) {
        List<ExperienceDiscreteAction> experienceList=new ArrayList<>();
        for (int si = 0; si < parameters.nofStepsMax() ; si++) {
            int action=agent.chooseAction();
            double reward=environment.step(action);
            experienceList.add(new ExperienceDiscreteAction(STATE_DUMMY,action,reward, STATE_DUMMY, DUMMY_VALUE));
        }
        return experienceList;
    }

    public TrainingTracker getTracker() {
        logIfEmptyTracker();
        return super.tracker;
    }


    private void logIfEmptyTracker() {
        if (tracker.isEmpty()) {
            log.warning("Need to train first");
        }
    }

}
