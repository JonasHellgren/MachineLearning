package mcts_spacegame.model_mcts;

import common.Conditionals;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.models_mcts_nodes.NodeInterface;

import java.util.*;
import java.util.stream.Collectors;

/***
 *    Fail states normally gives big negative rewards, to avoid destructive backup, measures below are taken
 *
 *   end node in path:
 *   1) terminal-non fail => normal backup
 *   2) non terminal => normal backup
 *   3) terminal-fail => defensive backup (simulations not applicable for case)
 *
 *    normal backup = backup all nodes in path
 *    defensive backup = backup end node AND set it parent as terminal if parents
 *    all children are fail-terminal
 *
 */

@Log
public class BackupModifierFromSteps extends BackupModifierAbstract {

    StepReturn stepReturnOfSelected;

    public BackupModifierFromSteps(NodeInterface rootTree,
                                   List<Action> actionsToSelected,
                                   Action actionOnSelected,
                                   StepReturn stepReturnOfSelected,
                                   MonteCarloSettings settings) {
        super(rootTree, actionsToSelected, actionOnSelected, settings);
        this.stepReturnOfSelected=stepReturnOfSelected;
    }

    //https://stackoverflow.com/questions/30717640/how-to-exclude-property-from-lombok-builder/39920328#39920328
    @Builder
    private static BackupModifierFromSteps newBUM(NodeInterface rootTree,
                                                  @NonNull List<Action> actionsToSelected,
                                                  @NonNull Action actionOnSelected,
                                                  @NonNull StepReturn stepReturnOfSelected,
                                                  MonteCarloSettings settings) {
        return new BackupModifierFromSteps(
                rootTree,
                actionsToSelected,
                actionOnSelected,
                stepReturnOfSelected,
                settings);

    }


    public void backup() {
        Conditionals.executeOneOfTwo(!stepReturnOfSelected.isFail,
                this::backupNormalFromTreeSteps,
                this::backupDefensiveFromTreeSteps);
    }

    private void backupNormalFromTreeSteps() {
        log.fine("Normal backup of selected node");
        List<Double> rewards = getRewards();
        List<Double> returns = getReturns(rewards);
        updateNodesFromReturns(returns);
    }

    private List<Double> getRewards() {
        List<Double> rewards = new ArrayList<>();
        for (NodeInterface nodeOnPath : nodesOnPath) {
            if (!nodeOnPath.equals(nodeSelected)) {
                Action action = actionsToSelected.get(nodesOnPath.indexOf(nodeOnPath));
                rewards.add(nodeOnPath.loadRewardForAction(action));
            }
        }
        rewards.add(stepReturnOfSelected.reward);
        return rewards;
    }

    private void backupDefensiveFromTreeSteps() {
        log.fine("Defensive backup of selected node");
        defensiveBackupOfSelectedNode();
        setSelectedAsTerminalIfAllItsChildrenAreTerminal();
    }

    private void defensiveBackupOfSelectedNode() {
        super.updateNode(nodeSelected, stepReturnOfSelected.reward, actionOnSelected,settings.alphaBackupSteps);
    }

    private void setSelectedAsTerminalIfAllItsChildrenAreTerminal() {
        Set<Action> children = nodeSelected.getChildNodes().stream()
                .filter(NodeInterface::isTerminalFail).map(NodeInterface::getAction)
                .collect(Collectors.toSet());

        Conditionals.executeIfTrue(children.size() == Action.applicableActions().size(),
                this::makeSelectedTerminal);
    }

    private void makeSelectedTerminal() {
        NodeInterface nodeCurrent = rootTree;
        Optional<NodeInterface> parentToSelected = Optional.empty();
        Action actionToSelected = Action.notApplicable;
        for (Action action : actionsToSelected) {
            boolean isSelectedChildToCurrent = nodeCurrent.getChildNodes().contains(nodeSelected);
            if (isSelectedChildToCurrent) {
                parentToSelected = Optional.of(nodeCurrent);
                actionToSelected = action;
            }
            nodeCurrent = nodeCurrent.getChild(action).orElseThrow();
        }

        if (parentToSelected.isEmpty()) {
            rootTree.printTree();
            log.warning("Parent to selected not found, probably children of root node are all terminal-fail");
            return;
        }

        NodeInterface selectedAsTerminalFail = NodeInterface.newTerminalFail(nodeSelected.getState(), actionToSelected);
        List<NodeInterface> childrenToParent = parentToSelected.get().getChildNodes();
        childrenToParent.remove(nodeSelected);
        parentToSelected.get().addChildNode(selectedAsTerminalFail);
    }

    private void updateNodesFromReturns(List<Double> returns) {
        double singleReturn;
        List<Action> actions = Action.getAllActions(actionsToSelected, actionOnSelected);
        for (NodeInterface node : nodesOnPath) {
            Action action = actions.get(nodesOnPath.indexOf(node));
            singleReturn = returns.get(nodesOnPath.indexOf(node));
            super.updateNode(node, singleReturn, action,settings.alphaBackupSteps);
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


}
