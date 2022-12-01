package mcts_spacegame.model_mcts;

import lombok.Builder;

@Builder
public class MonteCarloSettings {
    private final int NOF_ITERATIONS_DEFAULT=1000;
    private final int C_DEFAULT=1;

    @Builder.Default
    int nofIterations=NOF_ITERATIONS_DEFAULT;
    @Builder.Default
    int coefficientExploitationExploration =C_DEFAULT;


    public static MonteCarloSettings newDefault() {
        return MonteCarloSettings.builder().build();
    }

}
