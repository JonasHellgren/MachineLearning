package mcts_spacegame.model_mcts;

import lombok.Builder;

@Builder
public class MonteCarloSettings {
    private static final int NOF_ITERATIONS_DEFAULT=1000;
    private static final int C_DEFAULT=1;

    @Builder.Default
    int maxNofIterations =NOF_ITERATIONS_DEFAULT;
    @Builder.Default
    int coefficientExploitationExploration =C_DEFAULT;


    public static MonteCarloSettings newDefault() {
        return MonteCarloSettings.builder().build();
    }

}
