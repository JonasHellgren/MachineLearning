package policy_gradient_problems.the_problems.short_corridor;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.abstract_classes.AgentI;
import policy_gradient_problems.abstract_classes.TrainerA;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_generic.StepReturn;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.ArrayList;
import java.util.List;


public class TrainerAbstractSC extends TrainerA<VariablesSC> {

    public static final double DUMMY_VALUE = 0d;
    public static final boolean FAIL_DUMMY = false;
    @NonNull EnvironmentSC environment;

    public TrainerAbstractSC(@NonNull EnvironmentSC environment,
                             @NonNull TrainerParameters parameters) {
        this.environment = environment;
        super.parameters = parameters;
    }

    void updateTracker(int ei,AgentSC agent) {
        for (int s : EnvironmentSC.SET_OBSERVABLE_STATES_NON_TERMINAL) {
            tracker.addMeasures(ei, s, agent.getHelper().calcActionProbsInObsState(s));
        }
    }

    public List<Experience<VariablesSC>> getExperiences(AgentI<VariablesSC> agent) {
        List<Experience<VariablesSC>> experienceList = new ArrayList<>();
        int si = 0;
        StepReturn<VariablesSC> sr;
        do {
            var action = agent.chooseAction();
            sr = environment.step(agent.getState(), action);
            experienceList.add(createExperience(sr, action, agent));
            si++;
            agent.setState(sr.state());
        } while (isNotTerminalAndNofStepsNotExceeded(si, sr));
        return experienceList;
    }

    private Experience<VariablesSC> createExperience(StepReturn<VariablesSC> sr,
                                                     Action action,
                                                     AgentI<VariablesSC> agent) {
        return new Experience<>(
                StateSC.newFromPos(EnvironmentSC.getObservedPos(agent.getState())),
                action,
                sr.reward(),
                StateSC.newFromPos(EnvironmentSC.getObservedPos(sr.state())),
                FAIL_DUMMY,
                DUMMY_VALUE);
    }

    private boolean isNotTerminalAndNofStepsNotExceeded(int si, StepReturn<VariablesSC> sr) {
        return !sr.isTerminal() && si < parameters.nofStepsMax();
    }

    @Override
    public void train() {
    }


}
