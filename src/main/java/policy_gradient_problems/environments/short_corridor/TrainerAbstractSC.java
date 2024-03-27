package policy_gradient_problems.environments.short_corridor;

import lombok.NonNull;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.agent_interfaces.AgentI;
import policy_gradient_problems.domain.abstract_classes.TrainerA;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.StepReturn;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public abstract class TrainerAbstractSC extends TrainerA<VariablesSC> {
    public static final int PROB_ACTION = 1;
    EnvironmentSC environment;

    public TrainerAbstractSC(@NonNull EnvironmentSC environment,
                             @NonNull TrainerParameters parameters) {
        this.environment = environment;
        super.parameters = parameters;
    }

    void updateTracker(int ei, Function<Integer,List<Double>> apFcn) {
        for (int s : EnvironmentSC.SET_OBSERVABLE_STATES_NON_TERMINAL) {
            tracker.addMeasures(ei, s, apFcn.apply(s));
        }
    }

    protected List<Experience<VariablesSC>> getExperiences(AgentI<VariablesSC> agent) {
        List<Experience<VariablesSC>> experienceList = new ArrayList<>();
        int si = 0;
        StepReturn<VariablesSC> sr;
        do {
            var action = agent.chooseAction();
            sr = environment.step(agent.getState(), action);
            experienceList.add(createExperience(agent.getState(),sr, action));
            si++;
            agent.setState(sr.state());
        } while (isNotTerminalAndNofStepsNotExceeded(si, sr));
        return experienceList;
    }

    private Experience<VariablesSC> createExperience(StateI<VariablesSC> state,
                                                     StepReturn<VariablesSC> sr,
                                                     Action action) {
        return Experience.ofWithIsTerminal(asObserved(state), action, sr.reward(), asObserved(sr.state()), PROB_ACTION,
                environment.isTerminalObserved(asObserved(sr.state()).getPos()));

    }

    private static StateSC asObserved(StateI<VariablesSC> state) {
        StateSC stateCasted= (StateSC) state;
        return stateCasted.asObserved();
    }

    private boolean isNotTerminalAndNofStepsNotExceeded(int si, StepReturn<VariablesSC> sr) {
        return !sr.isTerminal() && si < parameters.nofStepsMax();
    }



}
