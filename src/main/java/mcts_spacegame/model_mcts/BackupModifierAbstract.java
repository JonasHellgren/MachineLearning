package mcts_spacegame.model_mcts;

import common.ConditionalUtils;
import lombok.Builder;
import lombok.NonNull;
import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.models_mcts_nodes.NodeInterface;

import java.util.List;
import java.util.Objects;

public class BackupModifierAbstract {

    NodeInterface rootTree;
    List<Action> actionsToSelected;
    Action actionOnSelected;
    MonteCarloSettings settings;

    TreeInfoHelper treeInfoHelper;
    int nofNodesOnPath;
    int nofActionsOnPath;
    NodeInterface nodeSelected;
    List<NodeInterface> nodesOnPath;

    public BackupModifierAbstract(NodeInterface rootTree,
                                  List<Action> actionsToSelected,
                                  Action actionOnSelected,
                                    MonteCarloSettings settings) {
        this.rootTree = rootTree;
        this.actionsToSelected = actionsToSelected;
        this.actionOnSelected = actionOnSelected;
        ConditionalUtils.executeDependantOnCondition(Objects.isNull(settings),
                () -> this.settings = MonteCarloSettings.builder().build(),
                () -> this.settings = settings);

        this.nofNodesOnPath = actionsToSelected.size();
        this.nofActionsOnPath = actionsToSelected.size();
        this.treeInfoHelper = new TreeInfoHelper(rootTree);
        this.nodeSelected = this.treeInfoHelper.getNodeReachedForActions(actionsToSelected).orElseThrow();  //"No node for action sequence"
        this.nodesOnPath = this.treeInfoHelper.getNodesOnPathForActions(actionsToSelected).orElseThrow();

    }


}
