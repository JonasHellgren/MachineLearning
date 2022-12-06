package mcts_spacegame.model_mcts;

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
 *   1) all simulations are terminal-non fail => defensive backup
 *   2) at least one simulation is terminal-non fail => normal backup from mix of max and average of non-fail simulations
 *
 *
 */
public class BackupModifierFromSimulations extends BackupModifierAbstract {

    public BackupModifierFromSimulations(NodeInterface rootTree,
                                   List<Action> actionsToSelected,
                                   Action actionOnSelected,
                                   StepReturn stepReturnOfSelected,
                                   MonteCarloSettings settings) {
        super(rootTree, actionsToSelected, actionOnSelected, stepReturnOfSelected, settings);
    }

    //https://stackoverflow.com/questions/30717640/how-to-exclude-property-from-lombok-builder/39920328#39920328
    @Builder
    private static BackupModifierFromSimulations newBUM(NodeInterface rootTree,
                                                  @NonNull List<Action> actionsToSelected,
                                                  @NonNull Action actionOnSelected,
                                                  @NonNull StepReturn stepReturnOfSelected,
                                                  MonteCarloSettings settings) {
        BackupModifierFromSimulations bm = new BackupModifierFromSimulations(
                rootTree,
                actionsToSelected,
                actionOnSelected,
                stepReturnOfSelected,
                settings);
        bm.rootTree = rootTree;
        bm.actionsToSelected = actionsToSelected;
        bm.actionOnSelected = actionOnSelected;
        bm.stepReturnOfSelected = stepReturnOfSelected;
        return  bm;

    }


}
