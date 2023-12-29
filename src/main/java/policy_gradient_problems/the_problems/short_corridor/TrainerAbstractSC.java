package policy_gradient_problems.the_problems.short_corridor;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.abstract_classes.AgentI;
import policy_gradient_problems.abstract_classes.TrainerA;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_value_classes.ExperienceOld;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import java.util.ArrayList;
import java.util.List;


public class TrainerAbstractSC extends TrainerA {

    public static final double DUMMY_VALUE = 0d;
    @NonNull EnvironmentSC environment;
    @NonNull AgentSC agent;

    public TrainerAbstractSC(@NonNull EnvironmentSC environment,
                             @NonNull AgentSC agent,
                             @NonNull TrainerParameters parameters) {
        this.environment = environment;
        this.agent = agent;
        super.parameters=parameters;
    }

    void updateTracker(int ei) {
        for (int s: EnvironmentSC.SET_OBSERVABLE_STATES_NON_TERMINAL) {
            tracker.addMeasures(ei,s, agent.calcActionProbabilitiesInState(s));
        }
    }

    public void setAgent(@NotNull AgentSC agent) {
        this.agent = agent;
    }

    protected List<ExperienceOld> getExperiences() {
        List<ExperienceOld> experienceList=new ArrayList<>();
        int si = 0;
        StepReturnSC sr;
        do  {
            int observedStateOld = environment.getObservedState(agent.getState());
            int action=agent.chooseAction(observedStateOld);
            sr=environment.step(agent.getState(),action);
            agent.setState(sr.state());
            int observerdStateNew=environment.getObservedState(sr.state());
            experienceList.add(new ExperienceOld(observedStateOld, action, sr.reward(), observerdStateNew, DUMMY_VALUE));
            si++;
        } while(isNotTerminalAndNofStepsNotExceeded(si, sr));
        return experienceList;
    }

    private boolean isNotTerminalAndNofStepsNotExceeded(int si, StepReturnSC sr) {
        return !sr.isTerminal() && si < parameters.nofStepsMax();
    }


    @Override
    public List<Experience> getExperiences(AgentI agent) {
        return null;
    }
}
