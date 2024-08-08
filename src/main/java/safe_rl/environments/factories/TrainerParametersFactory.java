package safe_rl.environments.factories;

import lombok.NoArgsConstructor;
import safe_rl.domain.trainer.value_objects.TrainerParameters;

@NoArgsConstructor
public class TrainerParametersFactory {

    public static TrainerParameters trading24Hours() {
        return TrainerParameters.newDefault()
                .withNofEpisodes(2500).withGamma(1.0)
                .withMiniBatchSize(50).withNReplayBufferFitsPerEpisode(5)
                .withLearningRateReplayBufferCritic(1e-1)
                .withLearningRateReplayBufferActor(1e-2)
                .withLearningRateReplayBufferActorStd(1e-2)
                .withRatioPenCorrectedAction(10d);
    }
/*
    public static TrainerParameters tradingNightHours() {
        return TrainerParametersFactory.trading24Hours().withNofEpisodes(2000);
    }*/

    public static TrainerParameters tradingNightHours(int nofEpisodes) {
        return TrainerParametersFactory.trading24Hours().withNofEpisodes(nofEpisodes);
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
