package mcts_elevator_runner;

import black_jack.result_drawer.GridPanel;
import lombok.SneakyThrows;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.cart_pole.*;
import monte_carlo_tree_search.domains.elevator.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.Arrays;

public class ElevatorOnlySearchRunner {
    private static final int SOE_FULL = 1;
    private static final int POS_FLOOR_0 = 0;
    private static final int NOF_STEPS_HALF_RANDOM_POLICY = 50;
    private static final int NSTEPS_BETWEEN = 50;
    private static final int NOF_CYCLES = 5;
    private static final int SLEEP_TIME = 100;
    private static final int NOF_STEPS = 100;


    @SneakyThrows
    public static void main(String[] args) throws InterruptedException {
        EnvironmentGenericInterface<VariablesElevator, Integer>  environment = EnvironmentElevator.newFromStepBetweenAddingNofWaiting
                (Arrays.asList(NSTEPS_BETWEEN,NSTEPS_BETWEEN,NSTEPS_BETWEEN));
        StateInterface<VariablesElevator> state = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(SOE_FULL).pos(POS_FLOOR_0).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(1, 0, 0))
                .build());

        MonteCarloTreeCreator<VariablesElevator, Integer> monteCarloTreeCreator= createTreeCreator(state);
        ElevatorRunner er=new ElevatorRunner(monteCarloTreeCreator,environment, NOF_STEPS);
        er.run(state);
        
    }

    public static MonteCarloTreeCreator<VariablesElevator, Integer> createTreeCreator(StateInterface<VariablesElevator> state) {
        EnvironmentGenericInterface<VariablesElevator, Integer> environment = EnvironmentElevator.newDefault();
        ActionInterface<Integer> actionTemplate=  ActionCartPole.newRandom();
        MonteCarloSettings<VariablesElevator, Integer> settings= MonteCarloSettings.<VariablesElevator, Integer>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(ElevatorPolicies.newRandomDirectionAfterStopping())
                .simulationPolicy(ElevatorPolicies.newRandomDirectionAfterStopping())
                .isDefensiveBackup(false)
                .coefficientMaxAverageReturn(0) //average
                .maxTreeDepth(100)
                .maxNofIterations(10_000)
                .timeBudgetMilliSeconds(100)
                .weightReturnsSteps(0)
                .nofSimulationsPerNode(100)
                .coefficientExploitationExploration(0.1)
                .isCreatePlotData(true)
                .build();

        return MonteCarloTreeCreator.<VariablesElevator, Integer>builder()
                .environment(environment)
                .startState(state)
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

}
