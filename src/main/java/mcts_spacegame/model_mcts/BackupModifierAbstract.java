package mcts_spacegame.model_mcts;

import common.Conditionals;
import mcts_spacegame.enums.Action;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import java.util.List;
import java.util.Objects;

/***
 *              (r)
 *             /   \
 *           (1)    (2)
 *          /   \
 *        (3)    (4)
 *
 *      actionsToSelected={left,left} => nodesOnPath={r,1,3}  => nodeSelected=3, nofNodesOnPath=3, nofActionsOnPath=2
 */

public class BackupModifierAbstract {

    NodeInterface rootTree;
    List<Action> actionsToSelected;
    Action actionOnSelected;
    MonteCarloSettings settings;

    TreeInfoHelper treeInfoHelper;
    //int nofNodesOnPath;
  //  int nofActionsOnPath;
    NodeInterface nodeSelected;
    List<NodeInterface> nodesOnPath;

    public BackupModifierAbstract(NodeInterface rootTree,
                                  List<Action> actionsToSelected,
                                  Action actionOnSelected,
                                  MonteCarloSettings settings) {
        this.rootTree = rootTree;
        this.actionsToSelected = actionsToSelected;
        this.actionOnSelected = actionOnSelected;
        Conditionals.executeOneOfTwo(Objects.isNull(settings),
                () -> this.settings = MonteCarloSettings.builder().build(),
                () -> this.settings = settings);

        //this.nofNodesOnPath = actionsToSelected.size();
       // this.nofActionsOnPath = actionsToSelected.size();
        this.treeInfoHelper = new TreeInfoHelper(rootTree);

        this.nodesOnPath = this.treeInfoHelper.getNodesOnPathForActions(actionsToSelected).orElseThrow();
       // this.nofNodesOnPath = nodesOnPath.size();

        this.nodeSelected = this.treeInfoHelper.getNodeReachedForActions(actionsToSelected).orElseThrow();  //"No node for action sequence"

    }

    protected void updateNode(NodeInterface node, double singleReturn, Action action, double alpha) {
        node.increaseNofVisits();
        node.increaseNofActionSelections(action);
        node.updateActionValue(singleReturn, action,alpha);
    }


}
