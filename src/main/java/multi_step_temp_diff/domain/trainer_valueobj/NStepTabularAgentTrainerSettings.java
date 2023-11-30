package multi_step_temp_diff.domain.trainer_valueobj;

import lombok.Builder;

import static common.MyFunctions.defaultIfNullDouble;
import static common.MyFunctions.defaultIfNullInteger;

public record NStepTabularAgentTrainerSettings  (
        Double alpha,
        Integer nofStepsBetweenUpdatedAndBackuped,
        Integer nofEpis,
        Double probStart,
        Double probEnd
) implements  NStepTabularTrainerSettingsInterface {

      private static final Double ALPHA = 0.5;
    private static final Integer N_DEFAULT = 3;
    private static final Integer NOF_EPIS = 100;
    private static final Double PROB_START = 0.9;
    private static final Double PROB_END = 0.01;

    public static NStepTabularAgentTrainerSettings getDefault() {
        return NStepTabularAgentTrainerSettings.builder().build();
    }
    @Builder
    public NStepTabularAgentTrainerSettings(Double alpha,
                                            Integer nofStepsBetweenUpdatedAndBackuped,
                                           Integer nofEpis,
                                           Double probStart,
                                           Double probEnd) {
        this.alpha = defaultIfNullDouble.apply(alpha,ALPHA);
        this.nofStepsBetweenUpdatedAndBackuped = defaultIfNullInteger.apply(nofStepsBetweenUpdatedAndBackuped,N_DEFAULT);
        this.nofEpis = defaultIfNullInteger.apply(nofEpis,NOF_EPIS);
        this.probStart = defaultIfNullDouble.apply(probStart,PROB_START);
        this.probEnd = defaultIfNullDouble.apply(probEnd,PROB_END);
    }
}
