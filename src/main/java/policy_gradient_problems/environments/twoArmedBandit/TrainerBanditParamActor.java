package policy_gradient_problems.environments.twoArmedBandit;

import lombok.Builder;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.common_episode_trainers.ParamActorEpisodeTrainer;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

/***
  agent.theta <- agent.theta+learningRateReplayBufferCritic*gradLog*vt;
 */

@Log
public final class TrainerBanditParamActor extends TrainerAbstractBandit {

    AgentBanditParamActor agent;

    @Builder
    public TrainerBanditParamActor(EnvironmentBandit environment,
                                   AgentBanditParamActor agent,
                                   TrainerParameters parameters) {
        super(environment, parameters);
        this.agent=agent;
    }

    @Override
    public void train() {
        ParamActorEpisodeTrainer<VariablesBandit> episodeTrainer= new ParamActorEpisodeTrainer<>(agent,parameters);
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            episodeTrainer.trainFromEpisode(getExperiences(agent));
            super.updateActionProbsRecorder(agent);
        }
    }

}
