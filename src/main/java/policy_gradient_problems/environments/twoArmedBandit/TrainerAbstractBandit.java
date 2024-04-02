package policy_gradient_problems.environments.twoArmedBandit;

import lombok.extern.java.Log;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.agent_interfaces.AgentI;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.abstract_classes.TrainerA;
import policy_gradient_problems.helpers.RecorderActionProbabilities;
import policy_gradient_problems.helpers.TrainingTracker;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.StepReturn;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

import java.util.ArrayList;
import java.util.List;

@Log
public abstract class TrainerAbstractBandit extends TrainerA<VariablesBandit> {

    final StateI<VariablesBandit> STATE_DUMMY = StateBandit.newDefault();
    EnvironmentBandit environment;

    TrainerAbstractBandit(EnvironmentBandit environment,
                                 TrainerParameters parameters) {
        this.environment = environment;
        super.parameters = parameters;
    }

    public List<Experience<VariablesBandit>> getExperiences(AgentI<VariablesBandit> agent) {
        List<Experience<VariablesBandit>> experienceList = new ArrayList<>();
        for (int si = 0; si < parameters.nofStepsMax(); si++) {
            Action action = agent.chooseAction();
            StepReturn<VariablesBandit> sr = environment.step(STATE_DUMMY, action);
            experienceList.add(Experience.of(STATE_DUMMY, action, sr.reward(), STATE_DUMMY));
        }
        return experienceList;
    }

    @Override
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
