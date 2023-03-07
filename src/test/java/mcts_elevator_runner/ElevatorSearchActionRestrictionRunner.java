package mcts_elevator_runner;

import lombok.SneakyThrows;
import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.elevator.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.Arrays;
import java.util.function.Function;

public class ElevatorSearchActionRestrictionRunner {
    private static final int SOE_FULL = 1;
    private static final int POS_FLOOR_0 = 0;
    private static final int NSTEPS_BETWEEN = 50;
    private static final int NSTEPS_BETWEEN_LARGER = 100;
    private static final int NSTEPS_BETWEEN_LARGE = 50_000;
    private static final int NOF_STEPS = 1000;

    @SneakyThrows
    public static void main(String[] args) throws InterruptedException {
        EnvironmentGenericInterface<VariablesElevator, Integer> environment = EnvironmentElevator.newFromStepBetweenAddingNofWaiting
                (Arrays.asList(NSTEPS_BETWEEN,NSTEPS_BETWEEN,NSTEPS_BETWEEN_LARGER));
        StateInterface<VariablesElevator> state = StateElevator.newFromVariables(VariablesElevator.builder()
                .SoE(SOE_FULL).pos(POS_FLOOR_0).nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(1, 0, 0))
                .build());

        MonteCarloTreeCreator<VariablesElevator, Integer> monteCarloTreeCreator= createTreeCreator(state);
        ElevatorRunner er=new ElevatorRunner(monteCarloTreeCreator,environment, NOF_STEPS);
        er.run(state);

    }

    public static MonteCarloTreeCreator<VariablesElevator, Integer> createTreeCreator(StateInterface<VariablesElevator> startState) {
        EnvironmentGenericInterface<VariablesElevator, Integer> environment = EnvironmentElevator.newDefault();
        ActionInterface<Integer> actionTemplate = ActionElevator.newValueDefaultRange(0);
        Function<VariablesElevator, Integer> nofActionsFunction =
                (a) -> EnvironmentElevator.isAtFloor.test(a.speed, a.pos)
                        ? actionTemplate.applicableActions().size()
                        : 1;

        MonteCarloSettings<VariablesElevator, Integer> settings= MonteCarloSettings.<VariablesElevator, Integer>builder()
                .firstActionSelectionPolicy(ElevatorPolicies.newRandomDirectionAfterStopping())
                .simulationPolicy(ElevatorPolicies.newRandomDirectionAfterStopping())
                .discountFactorSteps(0.9)
                .discountFactorSimulationDefensive(0.99)
                .maxTreeDepth(100)
                .maxNofIterations(10_000)
                .timeBudgetMilliSeconds(200)
                .nofSimulationsPerNode(5)
                .maxSimulationDepth(50)   //20
                .coefficientExploitationExploration(1e1)  //1e6
                .build();

        return MonteCarloTreeCreator.<VariablesElevator, Integer>builder()
                .environment(environment)
                .startState(startState)
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

}
