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
            tracker.addMeasures(ei,s, agent.calcActionProbsInObsState(s));
        }
    }

    public void setAgent(@NotNull AgentSC agent) {
        this.agent = agent;
    }

    public List<Experience<VariablesSC>> getExperiences(AgentI<VariablesSC> agent) {
        List<Experience<VariablesSC>> experienceList=new ArrayList<>();
        int si = 0;
        StepReturn<VariablesSC> sr;
        do  {
            Action action=agent.chooseAction();
            int  observerdPosOld = EnvironmentSC.getObservedPos(agent.getState());
            sr=environment.step(agent.getState(),action);
            agent.setState(sr.state());
            int observerdPosNew= EnvironmentSC.getObservedPos(sr.state());
            experienceList.add(new Experience<>(
                    StateSC.newFromPos(observerdPosOld),
                    action,
                    sr.reward(),
                    StateSC.newFromPos(observerdPosNew),
                    DUMMY_VALUE));
            si++;
        } while(isNotTerminalAndNofStepsNotExceeded(si, sr));
        return experienceList;
    }

    private boolean isNotTerminalAndNofStepsNotExceeded(int si, StepReturn sr) {
        return !sr.isTerminal() && si < parameters.nofStepsMax();
    }


    @Override
    public void train() {

    }

/*    @Override
    public List<Experience<VariablesSC>> getExperiences(AgentI agent) {
        return null;
    }*/
}
