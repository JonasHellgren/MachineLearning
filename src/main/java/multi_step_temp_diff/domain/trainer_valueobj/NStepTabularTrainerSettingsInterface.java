package multi_step_temp_diff.domain.trainer_valueobj;

public interface NStepTabularTrainerSettingsInterface {
    Integer nofStepsBetweenUpdatedAndBackuped();
    Integer nofEpis();
    Double probStart();
    Double probEnd();
}
