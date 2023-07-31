package multi_step_temp_diff.domain.environment_valueobj;

import lombok.Builder;

@Builder
public record ForkEnvironmentSettings(
        double rewardHeaven,
        double rewardHell,
        double rewardMove,
        int nofActions,
        int nofStates,
        int posHeaven,
        int posHell,
        int posSplit,
        int posStartHell,
        int posStartHeaven,
        int posAfterStartHell
) {

    public static ForkEnvironmentSettings getDefault() {
        return ForkEnvironmentSettings.builder()
                .rewardHeaven(10).rewardHell(-10).rewardMove(0)
                .nofActions(2).nofStates(16)
                .posHeaven(10).posHell(15).posSplit(5).posStartHell(6).posStartHeaven(7).posAfterStartHell(11)
                .build();
    }

}
