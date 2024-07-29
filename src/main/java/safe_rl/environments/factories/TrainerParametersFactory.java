package safe_rl.environments.factories;

import safe_rl.domain.trainer.value_objects.TrainerParameters;

public class TrainerParametersFactory {

    private TrainerParametersFactory() {
    }

    public static TrainerParameters trading24Hours() {
        return TrainerParameters.newDefault()
                .withNofEpisodes(3000).withGamma(1.0)
                .withMiniBatchSize(50).withNReplayBufferFitsPerEpisode(5)
                .withLearningRateReplayBufferCritic(1e-1)
                .withLearningRateReplayBufferActor(1e-2)
                .withLearningRateReplayBufferActorStd(1e-2);
    }

    public static TrainerParameters buying5Hours() {
        return TrainerParameters.newDefault()
                .withNofEpisodes(5000).withGamma(1.00)
                .withRatioPenCorrectedAction(0.1d).withStepHorizon(5)
                .withLearningRateReplayBufferCritic(1e-1)
                .withLearningRateReplayBufferActor(1e-4);
    }

    public static TrainerParameters buying3Hours() {
        return buying5Hours().withNofEpisodes(2000);
    }

}
