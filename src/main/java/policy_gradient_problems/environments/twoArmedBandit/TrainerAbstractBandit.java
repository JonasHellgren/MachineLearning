package policy_gradient_problems.environments.twoArmedBandit;

import lombok.extern.java.Log;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.agent_interfaces.AgentI;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.abstract_classes.TrainerA;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            var sr = environment.step(STATE_DUMMY, action);
            experienceList.add(Experience.of(STATE_DUMMY, action, sr.reward(), STATE_DUMMY));
        }
        return experienceList;
    }


    public void updateActionProbsRecorder(AgentI<VariablesBandit> agent) {
        var map = Map.of(agent.getState().getVariables().arm(), agent.getActionProbabilities());
        super.recorderActionProbabilities.addStateProbabilitiesMap(map);
    }


}
