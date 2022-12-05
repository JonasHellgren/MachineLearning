package mcts_spacegame.model_mcts;

import lombok.Builder;

@Builder
public class MonteCarloSettings {
    private static final int NOF_ITERATIONS_DEFAULT=1000;
    private static final int C_DEFAULT=1;
    private static final int MAX_TREE_DEPTH_DEFAULT=5;
    private static final long TIME_BUDGET_DEFAULT=1000;

    @Builder.Default
    int maxNofIterations =NOF_ITERATIONS_DEFAULT;
    @Builder.Default
    int coefficientExploitationExploration =C_DEFAULT;
    @Builder.Default
    int maxTreeDepth = MAX_TREE_DEPTH_DEFAULT;
    @Builder.Default
    long timeBudgetMilliSeconds = TIME_BUDGET_DEFAULT;


    public static MonteCarloSettings newDefault() {
        return MonteCarloSettings.builder().build();
    }

}
