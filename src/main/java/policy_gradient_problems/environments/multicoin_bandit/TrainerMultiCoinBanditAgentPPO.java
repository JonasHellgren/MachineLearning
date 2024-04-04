package policy_gradient_problems.environments.multicoin_bandit;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorI;
import policy_gradient_problems.domain.common_episode_trainers.NeuralActorTrainer;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.ProgressMeasures;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.twoArmedBandit.*;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Log
@Getter
public class TrainerMultiCoinBanditAgentPPO extends TrainerAbstractMultiCoinBandit {

    public static final int NUM_IN = 1;
    public static final double LEARNING_RATE = 0.01;
    MultiCoinBanditAgentPPO agent;

    @Builder
    public TrainerMultiCoinBanditAgentPPO(EnvironmentMultiCoinBandit environment,
                                          MultiCoinBanditAgentPPO agent,
                                          TrainerParameters parameters) {
        super(environment, parameters);
        this.agent = agent;
        this.agent = new MultiCoinBanditAgentPPO(LEARNING_RATE);
    }


    @Override
    public void train() {

        BiFunction<Experience<VariablesBandit>, AgentNeuralActorI<VariablesBandit>, List<Double>> labels =
                (exp, a) -> {
                    int actionInt = exp.action().asInt();
                    int nA = EnvironmentBandit.NOF_ACTIONS;
                    Preconditions.checkArgument(actionInt < nA, "Non valid action, actionInt =" + actionInt);
                    double probOld = a.getActionProbabilities().get(actionInt);
                    double adv = exp.value();
                    return List.of((double) actionInt, adv, probOld);
                };

        var episodeTrainer = NeuralActorTrainer.<VariablesBandit>builder()
                        .agent(agent)
                        .parameters(parameters)
                        .labelFunction(labels)
                        .build();
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            episodeTrainer.trainFromEpisode(super.getExperiences(agent));
            super.getRecorderActionProbabilities().addStateProbabilitiesMap(
                    Map.of(agent.getState().getVariables().arm(), agent.getActionProbabilities()));
            super.getRecorderTrainingProgress().add(ProgressMeasures.ofAllZero().withActorLoss(agent.lossLastFit()));
        }
    }
}
