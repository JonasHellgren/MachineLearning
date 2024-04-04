package policy_gradient_problems.environments.short_corridor;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.apache.commons.math3.util.Pair;
import policy_gradient_problems.domain.common_episode_trainers.ParamActorEpisodeTrainer;
import policy_gradient_problems.domain.value_classes.TrainerParameters;


@Log
public final class TrainerParamActorSC extends TrainerAbstractSC {

    AgentParamActorSC agent;

    @Builder
    public TrainerParamActorSC(@NonNull EnvironmentSC environment,
                               @NonNull AgentParamActorSC agent,
                               @NonNull TrainerParameters parameters) {
        super(environment, parameters);
        this.agent=agent;
    }

    @Override
    public void train() {
        var episodeTrainer= new ParamActorEpisodeTrainer<>(agent,parameters);
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StateSC.randomNonTerminal());
            episodeTrainer.trainFromEpisode(getExperiences(agent));
            updateRecorders((s) -> agent.helper.calcActionProbsInObsState(s), Pair.create(0d,0d));
        }
    }


}
