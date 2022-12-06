package mcts_spacegame.model_mcts;

import common.ConditionalUtils;
import lombok.Builder;
import lombok.NonNull;
import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.models_mcts_nodes.NodeInterface;

import java.util.List;

/***
 *    Fail states normally gives big negative rewards, to avoid destructive backup, measures below are taken

 *    normal backup = use alphaNormal and discountFactorSimulationNormal
 *    defensive backup = use alphaDefensive and discountFactorSimulationDefensive
 *
 *   a single simulation:
 *   1) all simulations are terminal-fail => defensive backup
 *   2) at least one simulation is terminal-non fail => normal backup from mix of max and average of non-fail simulations
 *
 *
 */
public class BackupModifierFromSimulations extends BackupModifierAbstract {

    SimulationResults simulationResults;

    public BackupModifierFromSimulations(NodeInterface rootTree,
                                   List<Action> actionsToSelected,
                                   Action actionOnSelected,
                                   SimulationResults simulationResults,
                                   MonteCarloSettings settings) {
        super(rootTree, actionsToSelected, actionOnSelected, settings);
        this.simulationResults=simulationResults;
    }

    //https://stackoverflow.com/questions/30717640/how-to-exclude-property-from-lombok-builder/39920328#39920328
    @Builder
    private static BackupModifierFromSimulations newBUM(NodeInterface rootTree,
                                                  @NonNull List<Action> actionsToSelected,
                                                  @NonNull Action actionOnSelected,
                                                  @NonNull SimulationResults simulationResults,
                                                  MonteCarloSettings settings) {
        return new BackupModifierFromSimulations(rootTree,
                actionsToSelected,
                actionOnSelected,
                simulationResults,
                settings);

    }

    public void backup() {
        ConditionalUtils.executeDependantOnCondition(!simulationResults.areAllSimulationsTerminalFail(),
                this::backupNormal,
                this::backupDefensive);
    }

    public void backupNormal() {

    }


    public void backupDefensive() {

    }




}
