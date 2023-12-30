package policy_gradient_problems.the_problems.cart_pole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.abstract_classes.AgentI;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.abstract_classes.TrainerA;
import policy_gradient_problems.common.ReturnCalculatorOld;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_generic.ReturnCalculator;
import policy_gradient_problems.common_generic.StepReturn;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@AllArgsConstructor
@Getter
public class TrainerAbstractPole extends TrainerA<VariablesPole> {

    public static final double DUMMY_VALUE = 0d;
    EnvironmentPole environment;
    AgentPole agent;
    ReturnCalculator<VariablesPole> returnCalculator=new ReturnCalculator<>();

    public TrainerAbstractPole(@NonNull EnvironmentPole environment,
                             @NonNull AgentPole agent,
                             @NonNull TrainerParameters parameters) {
        this.environment = environment;
        this.agent = agent;
        super.parameters=parameters;
    }

    void updateTracker(int ei, List<Experience<VariablesPole>> experienceList) {
        List<Double> nofSteps=List.of((double) experienceList.size());
        tracker.addMeasures(ei,0, nofSteps);
    }

    public void setAgent(@NotNull AgentPole agent) {
        this.agent = agent;
    }

    public List<Experience<VariablesPole>> getExperiences() {
        List<Experience<VariablesPole>> experienceList=new ArrayList<>();
        int si = 0;
        StepReturn<VariablesPole> sr;
        do  {
            StateI<VariablesPole> stateOld = agent.getState();
            Action action=agent.chooseAction();
            sr=environment.step(agent.getState(),action);
            agent.setState(sr.state());
            Experience<VariablesPole> exp = new Experience<>(stateOld, action, sr.reward(), sr.state(), sr.isFail(), DUMMY_VALUE);
            experienceList.add(exp);
            si++;
        } while(isNotTerminalAndNofStepsNotExceeded(si, sr));
        return experienceList;
    }

    private boolean isNotTerminalAndNofStepsNotExceeded(int si, StepReturn<VariablesPole> sr) {
        return !sr.isTerminal() && si < parameters.nofStepsMax();
    }

    protected List<Experience<VariablesPole>>  createExperienceListWithReturns(
            List<Experience<VariablesPole>> experienceList, double gamma) {
        List<Experience<VariablesPole>> experienceListNew=new ArrayList<>();
        List<Double> rewards=experienceList.stream().map(e->e.reward()).toList();
        ListIterator<Double> returnsIterator=returnCalculator.calcReturns(rewards,gamma).listIterator();
        for (Experience<VariablesPole> exp:experienceList) {
            experienceListNew.add(exp.copyWithValue(returnsIterator.next()));
        }
        return experienceListNew;
    }

    @Override
    public void train() {

    }

    @Override
    public List<Experience<VariablesPole>> getExperiences(AgentI agent) {
        return null;
    }
}
