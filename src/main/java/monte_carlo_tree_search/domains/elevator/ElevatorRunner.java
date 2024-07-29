package monte_carlo_tree_search.domains.elevator;

import black_jack.result_drawer.GridPanel;
import common.list_arrays.ListUtils;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import monte_carlo_tree_search.models_and_support_classes.*;
import monte_carlo_tree_search.create_tree.MonteCarloSearchStatistics;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.exceptions.StartStateIsTrapException;
import monte_carlo_tree_search.interfaces.*;

import java.util.Set;

@Log
public class ElevatorRunner {
    private static final int SLEEP_TIME = 1;
    private static final String TITLE = "Elevator evaluation animation";
    MonteCarloTreeCreator<VariablesElevator, Integer> mcForSearch;
    EnvironmentGenericInterface<VariablesElevator, Integer> environment;
    MonteCarloSettings<VariablesElevator, Integer> settings;
    NetworkMemoryInterface<VariablesElevator,Integer> memory;
    int nofSteps;

    public ElevatorRunner(MonteCarloTreeCreator<VariablesElevator, Integer> mcForSearch,
                          EnvironmentGenericInterface<VariablesElevator, Integer> environment,
                          int nofSteps) {
        this(mcForSearch,environment,null,nofSteps );
    }

    public ElevatorRunner(MonteCarloTreeCreator<VariablesElevator, Integer> mcForSearch,
                          EnvironmentGenericInterface<VariablesElevator, Integer> environment,
                          NetworkMemoryInterface<VariablesElevator,Integer> memory,
                          int nofSteps) {
        this.mcForSearch = mcForSearch;
        this.environment = environment;
        this.settings=mcForSearch.getSettings();
        this.memory = memory;
        this.nofSteps=nofSteps;
    }

    @SneakyThrows
    public void run(StateInterface<VariablesElevator> state) throws StartStateIsTrapException {
        int i = 0;
        boolean isFail;
        GridPanel panel=FrameAndPanelCreatorElevator.createPanel("","","");
        ElevatorPanelUpdater panelUpdater=new ElevatorPanelUpdater(state,panel);
        //todo stateNew=stateNew.copy()
        do {
            mcForSearch.setStartState(state);
            mcForSearch.run();

            MonteCarloSearchStatistics<VariablesElevator, Integer> stats=mcForSearch.getStatistics();
            SimulationPolicyInterface<VariablesElevator, Integer> policy=settings.getSimulationPolicy();
            PolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping policyCasted=(PolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping) policy;
            Set<Integer> actions=policyCasted.availableActionValues(state);
            log.info("Search completed, tree size = " +stats.getNofNodes()+", tree depth = "+stats.getMaxDepth()+", nof iter = "+stats.getNofIterations()+", actions = "+actions);

            updatePanelAndSleepMillis(panelUpdater);
            ActionInterface<Integer> actionCartPole = mcForSearch.getFirstAction();
            StepReturnGeneric<VariablesElevator> sr = environment.step(actionCartPole, state);
            state.setFromReturn(sr);
            //double value = memory.read(stateNew);
            double rootNodeValue= ListUtils.findEnd(mcForSearch.getPlotData()).orElse(TreePlotData.builder().build()).maxValue;
            isFail = sr.isFail;
            i++;
        } while (i < nofSteps && !isFail);

    }

    private  void updatePanelAndSleepMillis(ElevatorPanelUpdater panelUpdater) throws InterruptedException {
        panelUpdater.insertStates();
        Thread.sleep(SLEEP_TIME);
    }

}
