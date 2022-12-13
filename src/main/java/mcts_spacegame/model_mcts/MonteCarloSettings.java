package mcts_spacegame.model_mcts;

import lombok.Builder;
import lombok.Setter;
import lombok.ToString;
import mcts_spacegame.policies_action.SimulationPolicyInterface;

@Builder
@Setter
@ToString
public class MonteCarloSettings {
    private static final int NOF_ITERATIONS_DEFAULT=1000;
    private static final int C_DEFAULT=20;
    private static final int MAX_TREE_DEPTH_DEFAULT=5;
    private static final long TIME_BUDGET_DEFAULT=1000;
    private static final boolean BACKUP_STEPS_DEFAULT=true;
    private static final int NOF_SIMULATIONS_DEFAULT=0;
    private static final SimulationPolicyInterface FIRST_ACTION_POLICY_DEFAULT = SimulationPolicyInterface.newAlwaysStill();
    private static final SimulationPolicyInterface SIMULATION_POLICY_DEFAULT = SimulationPolicyInterface.newMostlyStill();
    private static final double DISCOUNT_FACTOR_STEPS_DEFAULT = 1;
    private static final double DISCOUNT_FACTOR_SIMULATIONS_NORMAL_DEFAULT = 1;
    private static final double DISCOUNT_FACTOR_SIMULATIONS_DEFENSIVE_DEFAULT = 0.1;
    private static final double C_MAX_AVERAGE_RETURN_BACKUP=0;  //1 <=> only max, 0 <=> only average
    private static final double ALPHA_NORMAL_DEFAULT =1;
    private static final double ALPHA_DEFENSIVE_DEFAULT =1;
    private static final double WEIGHT_STEPS_DEFAULT =1;
    private static final double WEIGHT_SIMULATION_DEFAULT =1;
    private static final double WEIGHT_MEMORY_DEFAULT =1;

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
    SimulationPolicyInterface firstActionSelectionPolicy = FIRST_ACTION_POLICY_DEFAULT;
    @Builder.Default
    SimulationPolicyInterface simulationPolicy = SIMULATION_POLICY_DEFAULT;
    @Builder.Default
    double discountFactorSteps=DISCOUNT_FACTOR_STEPS_DEFAULT;
    @Builder.Default
    double discountFactorSimulationNormal=DISCOUNT_FACTOR_SIMULATIONS_NORMAL_DEFAULT;
    @Builder.Default
    double discountFactorSimulationDefensive=DISCOUNT_FACTOR_SIMULATIONS_DEFENSIVE_DEFAULT;
    @Builder.Default
    double coefficientMaxAverageReturn=C_MAX_AVERAGE_RETURN_BACKUP;
    @Builder.Default
    double alphaBackupNormal = ALPHA_NORMAL_DEFAULT;
    @Builder.Default
    double alphaBackupDefensive = ALPHA_DEFENSIVE_DEFAULT;
    @Builder.Default
    double weightReturnsSteps = WEIGHT_STEPS_DEFAULT;
    @Builder.Default
    double weightReturnsSimulation = WEIGHT_SIMULATION_DEFAULT;
    @Builder.Default
    double weightMemoryValue=WEIGHT_MEMORY_DEFAULT;

    public static MonteCarloSettings newDefault() {
        return MonteCarloSettings.builder().build();
    }

}
