package policy_gradient_problems.the_problems.cart_pole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.abstract_classes.AgentI;
import policy_gradient_problems.abstract_classes.TrainerA;
import policy_gradient_problems.common.ReturnCalculatorOld;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@AllArgsConstructor
@Getter
public class TrainerAbstractPole extends TrainerA {

    public static final double DUMMY_VALUE = 0d;
    EnvironmentPole environment;
    AgentPole agent;
    ReturnCalculatorOld returnCalculator=new ReturnCalculatorOld();

    public TrainerAbstractPole(@NonNull EnvironmentPole environment,
                             @NonNull AgentPole agent,
                             @NonNull TrainerParameters parameters) {
        this.environment = environment;
        this.agent = agent;
        super.parameters=parameters;
    }

    void updateTracker(int ei, List<ExperiencePole> experienceList) {
        List<Double> nofSteps=List.of((double) experienceList.size());
        tracker.addMeasures(ei,0, nofSteps);
    }

    public void setAgent(@NotNull AgentPole agent) {
        this.agent = agent;
    }

    public List<ExperiencePole> getExperiences() {
        List<ExperiencePole> experienceList=new ArrayList<>();
        int si = 0;
        StepReturnPole sr;
        do  {
            StatePole stateOld = agent.state;
            int action=agent.chooseAction();
            sr=environment.step(action,agent.state);
            agent.setState(sr.newState());
            experienceList.add(new ExperiencePole(stateOld, action, sr.reward(), sr.newState(), sr.isFail(),DUMMY_VALUE));
            si++;
        } while(isNotTerminalAndNofStepsNotExceeded(si, sr));
        return experienceList;
    }

    private boolean isNotTerminalAndNofStepsNotExceeded(int si, StepReturnPole sr) {
        return !sr.isTerminal() && si < parameters.nofStepsMax();
    }

    protected List<ExperiencePole>  createExperienceListWithReturns(
            List<ExperiencePole> experienceList, double gamma) {
        List<ExperiencePole> experienceListNew=new ArrayList<>();
        List<Double> rewards=experienceList.stream().map(e->e.reward()).toList();
        ListIterator<Double> returnsIterator=returnCalculator.calcReturns(rewards,gamma).listIterator();
        for (ExperiencePole exp:experienceList) {
            experienceListNew.add(exp.copyWithValue(returnsIterator.next()));
        }
        return experienceListNew;
    }

    @Override
    public List<Experience> getExperiences(AgentI agent) {
        return null;
    }
}
