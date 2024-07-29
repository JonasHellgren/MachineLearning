package safe_rl.environments.factories;

import safe_rl.domain.trainer.value_objects.TrainerParameters;

public class TrainerParametersFactory {

    private TrainerParametersFactory() {
    }

    public static TrainerParameters trading24Hours() {
       return TrainerParameters.builder()
               .nofEpisodes(3000).gamma(1.00).stepHorizon(10)   //8k
               .learningRateReplayBufferCritic(1e-1)
               .learningRateReplayBufferActor(1e-2)
               .learningRateReplayBufferActorStd(1e-2)
               .replayBufferSize(1000).miniBatchSize(50).nReplayBufferFitsPerEpisode(5)
               .build();
    }

    public static TrainerParameters buying5Hours() {
        return TrainerParameters.newDefault()
                .withNofEpisodes(5000).withGamma(1.00).withRatioPenCorrectedAction(0.1d).withStepHorizon(5)
                .withLearningRateReplayBufferCritic(1e-1)
                .withLearningRateReplayBufferActor(1e-4);
    }

}
