package policy_gradient_problems.environments.cart_pole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.agent_interfaces.AgentI;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.abstract_classes.TrainerA;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.ProgressMeasures;
import policy_gradient_problems.helpers.ReturnCalculator;
import policy_gradient_problems.domain.value_classes.StepReturn;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

@AllArgsConstructor
@Getter
public abstract class TrainerAbstractPole extends TrainerA<VariablesPole> {
    EnvironmentPole environment;
    ReturnCalculator<VariablesPole> returnCalculator = new ReturnCalculator<>();

    TrainerAbstractPole(@NonNull EnvironmentPole environment,
                        @NonNull TrainerParameters parameters) {
        this.environment = environment;
        super.parameters = parameters;
    }

    void updateTracker(List<Experience<VariablesPole>> experienceList) {
        super.getRecorderTrainingProgress().add(
                ProgressMeasures.ofAllZero().withNSteps(experienceList.size()));
    }


    void updateTracker(List<Experience<VariablesPole>> experienceList,
                       AgentNeuralActorNeuralCriticI<VariablesPole> agent) {
        double sumRewards = experienceList.stream().mapToDouble(e -> e.reward()).sum();
        super.getRecorderTrainingProgress().add(ProgressMeasures.builder()
                .nSteps(experienceList.size())
                .sumRewards(sumRewards)
                .actorLoss(Math.abs(agent.lossActorAndCritic().getFirst()))
                .criticLoss(agent.lossActorAndCritic().getSecond())
                .build());
    }

    @SneakyThrows
    protected List<Experience<VariablesPole>> getExperiences(AgentI<VariablesPole> agent) {
        List<Experience<VariablesPole>> experienceList = new ArrayList<>();
        int si = 0;
        StepReturn<VariablesPole> sr;
        do {
            StateI<VariablesPole> stateOld = agent.getState();
            Action action = agent.chooseAction();
            sr = environment.step(agent.getState(), action);
            agent.setState(sr.state());
            List<Double> actionProbabilities = agent.getActionProbabilities();
            throwIfBadAction(action, actionProbabilities);
            double probAction = actionProbabilities.get(action.asInt());
            Experience<VariablesPole> exp =
                    Experience.ofWithIsFail(stateOld, action, sr.reward(), sr.state(), probAction, sr.isFail());
            experienceList.add(exp);
            si++;
        } while (isNotTerminalAndNofStepsNotExceeded(si, sr));
        return experienceList;
    }

    private static void throwIfBadAction(Action action, List<Double> actionProbabilities) throws DataFormatException {
        if (action.asInt() >= actionProbabilities.size()) {
            throw new DataFormatException("Bad action value, action = " + action + ", actionProbabilities=" + actionProbabilities);
        }
    }

    private boolean isNotTerminalAndNofStepsNotExceeded(int si, StepReturn<VariablesPole> sr) {
        return !sr.isTerminal() && si < parameters.nofStepsMax();
    }

}
