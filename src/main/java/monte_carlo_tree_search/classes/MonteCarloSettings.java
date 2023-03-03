package monte_carlo_tree_search.classes;

import lombok.*;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;

import java.util.function.Function;

@Builder
@Setter
@Getter
@ToString
public class MonteCarloSettings<S,A> {
    private static final int NOF_ITERATIONS_DEFAULT=10_000;
    private static final double C_DEFAULT=20;
    private static final int MAX_TREE_DEPTH_DEFAULT=5;
    private static final int MAX_SIMULATION_DEPTH_DEFAULT=Integer.MAX_VALUE;
    private static final long TIME_BUDGET_DEFAULT=1000;
    private static final int NOF_SIMULATIONS_DEFAULT=0;
    private static final double DISCOUNT_FACTOR_SIMULATIONS_DEFAULT = 1;  //used in simulation
    private static final double DISCOUNT_FACTOR_STEPS_DEFAULT = 1; //used for backup
    private static final double DISCOUNT_FACTOR_SIMULATIONS_NORMAL_DEFAULT = 1;  //used for backup
    private static final double DISCOUNT_FACTOR_SIMULATIONS_DEFENSIVE_DEFAULT = 0.1; //used for backup
    private static final double C_MAX_AVERAGE_RETURN_BACKUP=0;  //1 <=> only max, 0 <=> only average
    private static final double ALPHA_NORMAL_DEFAULT =1;
    private static final double ALPHA_DEFENSIVE_DEFAULT =1;
    private static final double WEIGHT_STEPS_DEFAULT =1;
    private static final double WEIGHT_SIMULATION_DEFAULT =1;
    private static final double WEIGHT_MEMORY_DEFAULT =1;
    private static final boolean DEFENSIVE_DEFAULT=true;
    private static final boolean IS_PLOT_DEFAULT = false;

    @Builder.Default
    int maxNofIterations =NOF_ITERATIONS_DEFAULT;
    @Builder.Default
    double coefficientExploitationExploration =C_DEFAULT;
    @Builder.Default
    int maxTreeDepth = MAX_TREE_DEPTH_DEFAULT;
    @Builder.Default
    int maxSimulationDepth = MAX_SIMULATION_DEPTH_DEFAULT;
    @Builder.Default
    long timeBudgetMilliSeconds = TIME_BUDGET_DEFAULT;
    @Builder.Default
    int nofSimulationsPerNode = NOF_SIMULATIONS_DEFAULT;
    @NonNull
    Function<S,Integer> maxNofTestedActionsForBeingLeafFunction;
    @NonNull
    SimulationPolicyInterface<S,A> firstActionSelectionPolicy;
    @NonNull
    SimulationPolicyInterface<S,A> simulationPolicy;
    @Builder.Default
    double discountFactorSteps=DISCOUNT_FACTOR_STEPS_DEFAULT;
    @Builder.Default
    double discountFactorSimulation=DISCOUNT_FACTOR_SIMULATIONS_DEFAULT;
    @Builder.Default
    double discountFactorSimulationNormal=DISCOUNT_FACTOR_SIMULATIONS_NORMAL_DEFAULT;
    @Builder.Default
    double discountFactorSimulationDefensive=DISCOUNT_FACTOR_SIMULATIONS_DEFENSIVE_DEFAULT;
    @Builder.Default
    double coefficientMaxAverageReturn=C_MAX_AVERAGE_RETURN_BACKUP;
    @Builder.Default
    double alphaBackupNormal = ALPHA_NORMAL_DEFAULT;
    @Builder.Default
    double alphaBackupDefensiveStep = ALPHA_DEFENSIVE_DEFAULT;
    @Builder.Default
    double alphaBackupDefensiveSimulation = ALPHA_DEFENSIVE_DEFAULT;
    @Builder.Default
    double weightReturnsSteps = WEIGHT_STEPS_DEFAULT;
    @Builder.Default
    double weightReturnsSimulation = WEIGHT_SIMULATION_DEFAULT;
    @Builder.Default
    double weightMemoryValue=WEIGHT_MEMORY_DEFAULT;
    @Builder.Default
    boolean isDefensiveBackup=DEFENSIVE_DEFAULT;
    @Builder.Default
    boolean isCreatePlotData= IS_PLOT_DEFAULT;

}
