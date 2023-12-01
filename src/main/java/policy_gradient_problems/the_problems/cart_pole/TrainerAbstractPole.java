package policy_gradient_problems.the_problems.cart_pole;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.abstract_classes.TrainerAbstract;
import policy_gradient_problems.common.ReturnCalculator;
import policy_gradient_problems.common_value_classes.ExperienceDiscreteAction;
import policy_gradient_problems.the_problems.short_corridor.AgentSC;
import policy_gradient_problems.the_problems.short_corridor.EnvironmentSC;
import policy_gradient_problems.the_problems.short_corridor.StepReturnSC;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class TrainerAbstractPole extends TrainerAbstract {

    public static final double DUMMY_VALUE = 0d;
    EnvironmentPole environment;
    AgentPole agent;
    ReturnCalculator returnCalculator=new ReturnCalculator();

    void updateTracker(int ei, List<Double> nofSteps) {
            tracker.addMeasures(ei,0, nofSteps);
    }

    public void setAgent(@NotNull AgentPole agent) {
        this.agent = agent;
    }

    protected List<ExperiencePole> getExperiences() {
        List<ExperiencePole> experienceList=new ArrayList<>();
        int si = 0;
        StepReturnPole sr;
        do  {
            StatePole stateOld = agent.state;
            int action=agent.chooseAction(stateOld);
            sr=environment.step(action,agent.state);
            agent.setState(sr.newState());
            experienceList.add(new ExperiencePole(stateOld, action, sr.reward(), sr.newState(), DUMMY_VALUE));
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

}
