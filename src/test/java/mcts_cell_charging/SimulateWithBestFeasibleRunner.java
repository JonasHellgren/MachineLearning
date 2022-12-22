package mcts_cell_charging;

import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.domains.models_battery_cell.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import java.util.List;


public class SimulateWithBestFeasibleRunner {

    private static final double SOC_INIT = 0.1;
    private static final int TEMPERATURE_INIT = 20;
    private static final int TIME_INIT = 0;
    private static final int DT = 5;
    private static final int NOF_CURRENT_LEVELS = 100;
    private static final int MAX_TIME = 180;
    private static final int TIME_OUT = 0;

    static EnvironmentGenericInterface<CellVariables, Integer> environment;
    static StateCell state;
    static MonteCarloSettings<CellVariables, Integer> settings;
    static CellSettings cellSettings;
    static ActionInterface<Integer> action;
    static CellSimulator simulator;
    static ActionInterface<Integer> actionTemplate;

    public static void main(String[] args) {

        init();
        SimulationPolicyInterface<CellVariables, Integer> policy= CellPolicies.newBestFeasible(action,environment);
        List<EnvironmentCell.CellResults> resultsList=simulator.simulateWithPolicy(policy,MAX_TIME/DT,state);
        doPlotting(resultsList,"Start temp is 20");

        state= new StateCell(CellVariables.builder()
                .time(TIME_INIT).temperature(10).SoC(SOC_INIT).build());
        resultsList=simulator.simulateWithPolicy(policy,MAX_TIME/DT,state);
        doPlotting(resultsList,"Start temp is 10");

    }

    public static void init() {
        actionTemplate= ActionCell.builder()
                .nofCurrentLevels(NOF_CURRENT_LEVELS).build();
        settings= MonteCarloSettings.<CellVariables, Integer>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(CellPolicies.newBestFeasible(actionTemplate,environment))
                .simulationPolicy(CellPolicies.newRandomFeasible(actionTemplate,environment))
                    .build();
        cellSettings=CellSettings.builder()
                .maxTime(MAX_TIME).dt(DT).build();
        environment=new EnvironmentCell(cellSettings);
        state= new StateCell(CellVariables.builder()
                .time(TIME_INIT).temperature(TEMPERATURE_INIT).SoC(SOC_INIT).build());
        action= ActionCell.builder()
                .nofCurrentLevels(NOF_CURRENT_LEVELS).build();
        simulator=new CellSimulator((EnvironmentCell) environment);

    }

    private static void doPlotting(List<EnvironmentCell.CellResults> resultsList, String frameTitle) {
        CellResultsPlotter plotter=new CellResultsPlotter(frameTitle, TIME_OUT);
        plotter.plot(resultsList);
    }


}
