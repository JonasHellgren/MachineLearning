package policy_gradient_problems.environments.maze;

import common.ListUtils;
import common_dl4j.EntropyCalculator;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.TrainerA;
import policy_gradient_problems.domain.agent_interfaces.AgentI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.ProgressMeasures;
import policy_gradient_problems.domain.value_classes.StepReturn;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class TrainerAbstractMaze extends TrainerA<VariablesMaze> {

    EnvironmentMaze environment;
    double avgEntropy;

    TrainerAbstractMaze(@NonNull EnvironmentMaze environment,
                        @NonNull TrainerParameters parameters) {
        this.environment = environment;
        super.parameters = parameters;
    }

    void updateRecorders(Pair<Double, Double> lossActorCritic) {
        super.recorderTrainingProgress.add(ProgressMeasures.builder()
                .actorLoss(Math.abs(lossActorCritic.getFirst()))
                .criticLoss(lossActorCritic.getSecond())
                .entropy(avgEntropy)
                .build());
    }

    protected List<Experience<VariablesMaze>> getExperiences(AgentI<VariablesMaze> agent) {
        List<Experience<VariablesMaze>> experienceList = new ArrayList<>();
        int si = 0;
        StepReturn<VariablesMaze> sr;
        List<Double> entrList = new ArrayList<>();
        do {
            var action = agent.chooseAction();
            sr = environment.step(agent.getState(), action);
            experienceList.add(createExperience(agent, sr, action));
            double entropy = EntropyCalculator.calcEntropy(agent.actionProbabilitiesInPresentState());
            entrList.add(entropy);
            si++;
            agent.setState(sr.state());
        } while (isNotTerminalAndNofStepsNotExceeded(si, sr));
        avgEntropy = ListUtils.findAverage(entrList).orElseThrow();

        return experienceList;
    }

    private Experience<VariablesMaze> createExperience(AgentI<VariablesMaze> agent,
                                                       StepReturn<VariablesMaze> sr,
                                                       Action action) {
        double probAction = agent.actionProbabilitiesInPresentState().get(action.asInt());
        return Experience.ofWithIsTerminal(agent.getState(), action, sr.reward(), sr.state(),
                probAction, sr.isTerminal());

    }

    private boolean isNotTerminalAndNofStepsNotExceeded(int si, StepReturn<VariablesMaze> sr) {
        return !sr.isTerminal() && si < parameters.nofStepsMax();
    }


}
