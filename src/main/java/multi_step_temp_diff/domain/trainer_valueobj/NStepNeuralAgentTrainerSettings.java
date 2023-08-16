package multi_step_temp_diff.domain.trainer_valueobj;

import lombok.Builder;

import static common.DefaultPredicates.*;

public record NStepNeuralAgentTrainerSettings(
        Integer nofStepsBetweenUpdatedAndBackuped,
        Integer nofEpis,
        Integer maxTrainingTimeInMilliS,
        Integer batchSize,
        Integer nofIterations,
        Double probStart,
        Double probEnd,
        Double initValue,
        Integer maxBufferSize,
        Integer maxStepsInEpisode,
        Integer nofEpisodesBetweenLogs
) implements  NStepTabularTrainerSettingsInterface {

  //  private static final Double ALPHA = 0.5;
    private static final Integer N_DEFAULT = 3;
    private static final Integer NOF_EPIS = 100;
    private static final Integer BATCH_SIZE = 50;
    private static final Integer NOF_ITERATIONS = 1;
    private static final Double PROB_START = 0.9;
    private static final Double PROB_END = 0.01;
    public static final Double INIT_VALUE = 0d;
    public static final Integer MAX_BUFFER_SIZE = 10_000;  //only applies if non-default buffer is created
    public static final int NOF_EPISODES_BETWEEN_LOGS = 100;


    public static NStepNeuralAgentTrainerSettings getDefault() {
        return NStepNeuralAgentTrainerSettings.builder().build();
    }
    @Builder
    public NStepNeuralAgentTrainerSettings(Integer nofStepsBetweenUpdatedAndBackuped,
                                           Integer nofEpis,
                                           Integer maxTrainingTimeInMilliS,
                                           Integer batchSize,
                                           Integer nofIterations,
                                           Double probStart,
                                           Double probEnd,
                                           Double initValue,
                                           Integer maxBufferSize,
                                           Integer maxStepsInEpisode,
                                           Integer nofEpisodesBetweenLogs) {
        this.nofStepsBetweenUpdatedAndBackuped = defaultIfNullInteger.apply(nofStepsBetweenUpdatedAndBackuped,N_DEFAULT);
        this.nofEpis = defaultIfNullInteger.apply(nofEpis,NOF_EPIS);
        this.maxTrainingTimeInMilliS = defaultIfNullInteger.apply(maxTrainingTimeInMilliS,Integer.MAX_VALUE);
        this.batchSize = defaultIfNullInteger.apply(batchSize,BATCH_SIZE);
        this.nofIterations = defaultIfNullInteger.apply(nofIterations,NOF_ITERATIONS);
        this.probStart = defaultIfNullDouble.apply(probStart,PROB_START);
        this.probEnd = defaultIfNullDouble.apply(probEnd,PROB_END);
        this.initValue = defaultIfNullDouble.apply(initValue,INIT_VALUE);
        this.maxBufferSize = defaultIfNullInteger.apply(maxBufferSize,MAX_BUFFER_SIZE);
        this.maxStepsInEpisode = defaultIfNullInteger.apply(maxStepsInEpisode,Integer.MAX_VALUE);
        this.nofEpisodesBetweenLogs = defaultIfNullInteger.apply(nofEpisodesBetweenLogs, NOF_EPISODES_BETWEEN_LOGS);

    }
}
