package policy_gradient_problems.environments.twoArmedBandit;

import com.google.common.base.Preconditions;
import common.ListUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorI;
import policy_gradient_problems.domain.common_episode_trainers.NeuralActorCrossEntropyLossTrainer;
import policy_gradient_problems.domain.common_episode_trainers.NeuralActorTrainer;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

import java.util.List;
import java.util.function.BiFunction;

@Log
@Getter
public final class TrainerBanditNeuralActor extends TrainerAbstractBandit {

    public static final int NUM_IN = 1;
    public static final double LEARNING_RATE = 0.01;
    AgentBanditNeuralActor agent;

    @Builder
    public TrainerBanditNeuralActor(EnvironmentBandit environment,
                                    AgentBanditNeuralActor agent,
                                    TrainerParameters parameters) {
        super(environment, parameters);
        this.agent=agent;
        this.agent = new AgentBanditNeuralActor(LEARNING_RATE);
    }

    @Override
    public void train() {
/*        NeuralActorCrossEntropyLossTrainer<VariablesBandit> episodeTrainer=
                new NeuralActorCrossEntropyLossTrainer<>(agent,parameters,EnvironmentBandit.NOF_ACTIONS);*/

        BiFunction<Experience<VariablesBandit>, AgentNeuralActorI<VariablesBandit>, List<Double>> labels =
                (exp, agent) -> {
                    int actionInt = exp.action().asInt();
                    int nofActions = EnvironmentBandit.NOF_ACTIONS;
                    Preconditions.checkArgument(actionInt < nofActions, "Non valid action, actionInt =" + actionInt);
                    List<Double> out = ListUtils.createListWithEqualElementValues(nofActions, 0d);
                    out.set(actionInt, exp.value());
                    return out;
                };

        var episodeTrainer = NeuralActorTrainer.<VariablesBandit>builder()
                .agent(agent)
                .parameters(parameters)
                .labelFunction(labels)
                .build();

        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            episodeTrainer.trainFromEpisode(super.getExperiences(agent));
            tracker.addMeasures(ei, agent.getState().getVariables().arm(), agent.getActionProbabilities());
        }
    }

}
