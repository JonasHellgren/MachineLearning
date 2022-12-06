package mcts_spacegame.model_mcts;

import lombok.Builder;

@Builder
public class MonteCarloSettings {
    private static final int NOF_ITERATIONS_DEFAULT=1000;
    private static final int C_DEFAULT=1;
    private static final int MAX_TREE_DEPTH_DEFAULT=5;
    private static final long TIME_BUDGET_DEFAULT=1000;
    private static final int NOF_SIMULATIONS_DEFAULT=0;
    private static final SimulationPolicyInterface POLICY_DEFAULT = SimulationPolicyInterface.newMostlyStill();
    private static final double DISCOUNT_FACTOR_STEPS_DEFAULT = 1;
    private static final double DISCOUNT_FACTOR_SIMULATIONS_NORMAL_DEFAULT = 1;
    private static final double DISCOUNT_FACTOR_SIMULATIONS_DEFENSIVE_DEFAULT = 0.1;
    private static final double C_MAX_AVERAGE_RETURN_BACKUP=0;  //1 <=> only max, 0 <=> only average
    private static final double ALPHA_STEPS_DEFAULT=1;
    private static final double ALPHA_SIMULATION_DEFAULT=0.1;

    @Builder.Default
    int maxNofIterations =NOF_ITERATIONS_DEFAULT;
    @Builder.Default
    int coefficientExploitationExploration =C_DEFAULT;
    @Builder.Default
    int maxTreeDepth = MAX_TREE_DEPTH_DEFAULT;
    @Builder.Default
    long timeBudgetMilliSeconds = TIME_BUDGET_DEFAULT;
    @Builder.Default
    int nofSimulationsPerNode = NOF_SIMULATIONS_DEFAULT;
    @Builder.Default
    SimulationPolicyInterface policy=POLICY_DEFAULT;
    @Builder.Default
    double discountFactorSteps=DISCOUNT_FACTOR_STEPS_DEFAULT;
    @Builder.Default
    double discountFactorSimulationNormal=DISCOUNT_FACTOR_SIMULATIONS_NORMAL_DEFAULT;
    @Builder.Default
    double discountFactorSimulationDefensive=DISCOUNT_FACTOR_SIMULATIONS_DEFENSIVE_DEFAULT;
    @Builder.Default
    double coefficientMaxAverageReturn=C_MAX_AVERAGE_RETURN_BACKUP;
    @Builder.Default
    double alphaBackupSteps=ALPHA_STEPS_DEFAULT;
    @Builder.Default
    double alphaBackupSimulation=ALPHA_SIMULATION_DEFAULT;



    public static MonteCarloSettings newDefault() {
        return MonteCarloSettings.builder().build();
    }

}
