package policy_gradient_problems.the_problems.cart_pole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.agent_interfaces.AgentI;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.abstract_classes.TrainerA;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_helpers.ReturnCalculator;
import policy_gradient_problems.common_generic.StepReturn;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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
            Experience<VariablesPole> exp =
                    Experience.ofWithIsFail(stateOld, action, sr.reward(), sr.state(), sr.isFail());
            experienceList.add(exp);
            si++;
        } while(isNotTerminalAndNofStepsNotExceeded(si, sr));
        return experienceList;
    }

    private boolean isNotTerminalAndNofStepsNotExceeded(int si, StepReturn<VariablesPole> sr) {
        return !sr.isTerminal() && si < parameters.nofStepsMax();
    }

}
