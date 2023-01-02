package monte_carlo_tree_search.classes;

import common.Conditionals;
import common.ListUtils;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.node_models.NodeInterface;

import java.util.*;

/***
 *  This class updates a monte carlo tree, internal node variables can be changed or node(s) can be replaced.
 *
 *   A special case managed by backup operator is than all children of selected node are fail nodes. These leads
 *   to transforming selected node to terminal fail.
 *
 *   Fail states normally gives big negative rewards, to avoid destructive backup, measures below are taken
 *   The new node in selection path can be (and leads to):
 *   1) terminal-non fail => normal backup
 *   2) non terminal => normal backup
 *   3) terminal-fail => defensive backup (simulations not applicable for case)
 *
 *    normal backup = backup all nodes in path
 *    defensive backup = backup end node (selected) AND set it as terminal if all children are fail-terminal
 *
 *             (r)
 *            /   \
 *          (1)    (2)
 *         /   \
 *       (3)    (4)
 *       /
 *   (new node)
 *
 *  actionsToSelected={left,left} => nodesOnPath={r,1,3}  => nodeSelected=3, nofNodesOnPath=3, nofActionsOnPath=2
 *  in nodeSelected an action will be applied leading to expansion
 *
 *
 *  return(ni)=returnStep(ni)*weightReturnsSteps+returnSimulation(i)*weightReturnsSimulation where ni is a node on a path
 *  If the end node in a path corresponds to a terminal state, a value memory can affect the result. The memory value
 *  is simply added to all rewards in the path. The memory value also affects the terminal state in simulations.
 *  For both steps and simulations, the memory value is multiplied by weightMemoryValue.
 *  The above described weights are handy to cancel out for example step results and/or put less trust in the memory.
 *
 */

@Log
public class BackupModifier<SSV,AV> {

    NodeInterface<SSV,AV> rootTree;
    List<ActionInterface<AV>> actionsToSelected;
    ActionInterface<AV> actionOnSelected;
    StepReturnGeneric<SSV> stepReturnOfSelected;
    MonteCarloSettings<SSV,AV> settings;

    TreeInfoHelper<SSV,AV> treeInfoHelper;
    NodeInterface<SSV,AV> nodeSelected;
    List<NodeInterface<SSV,AV>> nodesOnPath;

    //https://stackoverflow.com/questions/30717640/how-to-exclude-property-from-lombok-builder/39920328#39920328
    @Builder
    private static <SSV,AV> BackupModifier<SSV,AV> newBUM(NodeInterface<SSV,AV> rootTree,
                                         @NonNull List<ActionInterface<AV>> actionsToSelected,
                                         @NonNull ActionInterface<AV> actionOnSelected,
                                         @NonNull StepReturnGeneric<SSV> stepReturnOfSelected,
                                         @NonNull MonteCarloSettings<SSV,AV> settings) {
        BackupModifier<SSV,AV> bm = new BackupModifier<>();
        bm.rootTree = rootTree;
        bm.actionsToSelected = actionsToSelected;
        bm.actionOnSelected = actionOnSelected;
        bm.stepReturnOfSelected = stepReturnOfSelected;
        bm.settings = settings;

        bm.treeInfoHelper = new TreeInfoHelper<>(rootTree, settings);
        bm.nodesOnPath = bm.treeInfoHelper.getNodesOnPathForActions(actionsToSelected).orElseThrow();
        bm.nodeSelected = bm.treeInfoHelper.getNodeReachedForActions(actionsToSelected).orElseThrow();

        return bm;
    }

    public void backup() {
        backup(ListUtils.listWithZeroElements(nodesOnPath.size()),0);
    }

    public void backup(List<Double> returnsSimulation, double memoryValueStateAfterAction) {
        Conditionals.executeOneOfTwo(!stepReturnOfSelected.isFail,
                () -> backupNormalFromTreeSteps(returnsSimulation,memoryValueStateAfterAction),
                this::backupDefensiveFromTreeSteps);
    }

    private void backupNormalFromTreeSteps(List<Double> returnsSimulation,double memoryValue) {
        log.fine("Normal backup of selected node");
        List<Double> rewards = getRewards();
        List<Double> returnsSteps = getReturns(rewards);
        returnsSteps = ListUtils.addScalarToListElements(returnsSteps, memoryValue*settings.weightMemoryValue);
        updateNodesFromReturns(returnsSteps, returnsSimulation);
    }

    private List<Double> getRewards() {
        List<Double> rewards = new ArrayList<>();
        for (NodeInterface<SSV,AV> nodeOnPath : nodesOnPath) {
            if (!nodeOnPath.equals(nodeSelected)) {   //skipping selected because its reward is added after loop
                ActionInterface<AV> action = actionsToSelected.get(nodesOnPath.indexOf(nodeOnPath));
                rewards.add(nodeOnPath.restoreRewardForAction(action));
            }
        }
        rewards.add(stepReturnOfSelected.reward);
        return rewards;
    }

    private void backupDefensiveFromTreeSteps() {
        log.fine("Defensive backup of selected node");
        defensiveBackupOfSelectedNode();
    }

    private void defensiveBackupOfSelectedNode() {
        this.updateNode(nodeSelected, stepReturnOfSelected.reward, actionOnSelected, settings.alphaBackupDefensive);
    }

    private void updateNodesFromReturns(List<Double> returnsSteps, List<Double> returnsSimulation) {
        if (returnsSteps.size() != returnsSimulation.size()) {
            throw new IllegalArgumentException("Non equal list lengths");
        }
        returnsSteps = ListUtils.multiplyListElements(returnsSteps, settings.weightReturnsSteps);
        returnsSimulation = ListUtils.multiplyListElements(returnsSimulation, settings.weightReturnsSimulation);
        List<Double> returnsSum = ListUtils.sumListElements(returnsSteps, returnsSimulation);

        List<ActionInterface<AV>> actions =
                ActionInterface.mergeActionsWithAction(actionsToSelected, actionOnSelected);
        for (NodeInterface<SSV,AV> node : nodesOnPath) {
            ActionInterface<AV> action = actions.get(nodesOnPath.indexOf(node));
            double singleReturn = returnsSum.get(nodesOnPath.indexOf(node));
            this.updateNode(node, singleReturn, action, settings.alphaBackupNormal);
        }
    }

    private List<Double> getReturns(List<Double> rewards) {
        double singleReturn = 0;
        List<Double> returns = new ArrayList<>();
        for (int i = rewards.size() - 1; i >= 0; i--) {
            double reward = rewards.get(i);
            singleReturn = singleReturn + settings.discountFactorSteps * reward;
            returns.add(singleReturn);
        }
        Collections.reverse(returns);
        return returns;
    }

    void updateNode(NodeInterface<SSV,AV> node, double singleReturn, ActionInterface<AV> action, double alpha) {
        node.increaseNofVisits();
        node.increaseNofActionSelections(action);
        node.updateActionValue(singleReturn, action, alpha);
    }
}
