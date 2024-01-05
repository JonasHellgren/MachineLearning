package policy_gradient_problems.the_problems.short_corridor;

import lombok.NonNull;
import org.apache.arrow.flatbuf.Int;
import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.agent_interfaces.AgentI;
import policy_gradient_problems.abstract_classes.TrainerA;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_generic.StepReturn;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public abstract class TrainerAbstractSC extends TrainerA<VariablesSC> {
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
        return Experience.of(asObserved(state), action, sr.reward(), asObserved(sr.state()));
    }

    private static StateSC asObserved(StateI<VariablesSC> state) {
        StateSC stateCasted= (StateSC) state;
        return stateCasted.asObserved();
    }

    private boolean isNotTerminalAndNofStepsNotExceeded(int si, StepReturn<VariablesSC> sr) {
        return !sr.isTerminal() && si < parameters.nofStepsMax();
    }



}
