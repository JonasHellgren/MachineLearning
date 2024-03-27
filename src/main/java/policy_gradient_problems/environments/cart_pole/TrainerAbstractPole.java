package policy_gradient_problems.environments.cart_pole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.agent_interfaces.AgentI;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.abstract_classes.TrainerA;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.helpers.ReturnCalculator;
import policy_gradient_problems.domain.value_classes.StepReturn;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public abstract class TrainerAbstractPole extends TrainerA<VariablesPole> {
    EnvironmentPole environment;
    ReturnCalculator<VariablesPole> returnCalculator=new ReturnCalculator<>();

    public TrainerAbstractPole(@NonNull EnvironmentPole environment,
                             @NonNull TrainerParameters parameters) {
        this.environment = environment;
        super.parameters=parameters;
    }

    void updateTracker(int ei, List<Experience<VariablesPole>> experienceList) {
        List<Double> nofSteps=List.of((double) experienceList.size());
        tracker.addMeasures(ei,0, nofSteps);
    }


    protected List<Experience<VariablesPole>> getExperiences(AgentI<VariablesPole> agent) {
        List<Experience<VariablesPole>> experienceList=new ArrayList<>();
        int si = 0;
        StepReturn<VariablesPole> sr;
        do  {
            StateI<VariablesPole> stateOld = agent.getState();
            Action action=agent.chooseAction();
            sr=environment.step(agent.getState(),action);
            agent.setState(sr.state());
            double probAction=agent.getActionProbabilities().get(action.asInt());
            Experience<VariablesPole> exp =
                    Experience.ofWithIsFail(stateOld, action, sr.reward(), sr.state(), probAction, sr.isFail());
            experienceList.add(exp);
            si++;
        } while(isNotTerminalAndNofStepsNotExceeded(si, sr));
        return experienceList;
    }

    private boolean isNotTerminalAndNofStepsNotExceeded(int si, StepReturn<VariablesPole> sr) {
        return !sr.isTerminal() && si < parameters.nofStepsMax();
    }

}
