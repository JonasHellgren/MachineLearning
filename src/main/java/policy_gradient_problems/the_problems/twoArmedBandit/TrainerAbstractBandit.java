package policy_gradient_problems.the_problems.twoArmedBandit;

import lombok.extern.java.Log;
import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.abstract_classes.AgentI;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.abstract_classes.TrainerA;
import policy_gradient_problems.common.TrainingTracker;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_generic.StepReturn;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.ArrayList;
import java.util.List;

@Log
public class TrainerAbstractBandit extends TrainerA {

    final double DUMMY_VALUE = 0d;
    final StateI<VariablesBandit> STATE_DUMMY = StateBandit.newDefault();

     EnvironmentBandit environment;

    public TrainerAbstractBandit(EnvironmentBandit environment,
                                 TrainerParameters parameters) {
        this.environment = environment;
        super.parameters=parameters;
    }


    public List<Experience<VariablesBandit>> getExperiences(AgentI<VariablesBandit> agent) {
        List<Experience<VariablesBandit>> experienceList=new ArrayList<>();
        for (int si = 0; si < parameters.nofStepsMax() ; si++) {
            Action action=agent.chooseAction();
            StepReturn<VariablesBandit> sr =environment.step(action);
            experienceList.add(new Experience<>(STATE_DUMMY,action, sr.reward(), STATE_DUMMY, DUMMY_VALUE));
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
