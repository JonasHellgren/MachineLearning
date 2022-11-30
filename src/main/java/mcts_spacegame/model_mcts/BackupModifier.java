package mcts_spacegame.model_mcts;

import common.ConditionalUtils;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.helpers.TreeInfoHelper;
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
 *   a single simulation:
 *   1) terminal-non fail => normal backup
 *   2) terminal-fail =>  defensive backup
 *
 */

@Log
public class BackupModifier {

    private static final int DISCOUNT_FACTOR_DEFAULT = 1;

    NodeInterface rootTree;
    List<Action> actionsToSelected;
    Action actionOnSelected;
    StepReturn stepReturnOfSelected;
    List<List<StepReturn>> simulationResults;
    double discountFactor;

    TreeInfoHelper treeInfoHelper;
    int nofNodesOnPath;
    int nofActionsOnPath;
    NodeInterface nodeSelected;
    List<NodeInterface> nodesOnPath;

    //https://stackoverflow.com/questions/30717640/how-to-exclude-property-from-lombok-builder/39920328#39920328
    @Builder
    private static BackupModifier newBUM(NodeInterface rootTree,
                                     @NonNull List<Action> actionsToSelected,
                                     @NonNull Action actionOnSelected,
                                     @NonNull StepReturn stepReturnOfSelected,
                                     List<List<StepReturn>> simulationResultsOnSelected,
                                     Double discountFactor) {
        BackupModifier bm = new BackupModifier();
        bm.rootTree = rootTree;
        bm.actionsToSelected = actionsToSelected;
        bm.actionOnSelected = actionOnSelected;
        bm.stepReturnOfSelected = stepReturnOfSelected;
        bm.simulationResults = simulationResultsOnSelected;

        ConditionalUtils.executeDependantOnCondition(Objects.isNull(simulationResultsOnSelected),
                () -> bm.simulationResults = new ArrayList<>(new ArrayList<>()),
                () -> bm.simulationResults = simulationResultsOnSelected);

        ConditionalUtils.executeDependantOnCondition(Objects.isNull(discountFactor),
                () -> bm.discountFactor = DISCOUNT_FACTOR_DEFAULT,
                () -> bm.discountFactor = discountFactor);


        bm.nofNodesOnPath = actionsToSelected.size();
        bm.nofActionsOnPath = actionsToSelected.size();
        bm.treeInfoHelper = new TreeInfoHelper(rootTree);
        bm.nodeSelected = bm.treeInfoHelper.getNodeReachedForActions(actionsToSelected).orElseThrow();  //"No node for action sequence"
        bm.nodesOnPath = bm.treeInfoHelper.getNodesOnPathForActions(actionsToSelected).orElseThrow();
        return bm;
    }


    public void backup() {
                ConditionalUtils.executeDependantOnCondition(!stepReturnOfSelected.isFail,
                        this::backupNormalFromTreeSteps,
                        this::backupDefensiveFromTreeSteps);
    }

    private void backupNormalFromTreeSteps() {
        log.fine("Normal backup of selected node");
        List<Double> rewards = getRewards();
        List<Double> GList = getReturns(rewards);
        updateNodesFromReturns(GList);
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
        updateNode(nodeSelected, stepReturnOfSelected.reward, actionOnSelected);
    }

    private void setSelectedAsTerminalIfAllItsChildrenAreTerminal() {
        Set<Action> children=nodeSelected.getChildNodes().stream()
                .filter(NodeInterface::isTerminalFail).map(NodeInterface::getAction)
                .collect(Collectors.toSet());

        ConditionalUtils.executeOnlyIfConditionIsTrue(children.size()==Action.applicableActions().size(),
                this::makeSelectedTerminal);
    }

    private void makeSelectedTerminal() {
        NodeInterface nodeCurrent=rootTree;
        Optional<NodeInterface> parentToSelected=Optional.empty();
        Action actionToSelected=Action.notApplicable;
        for (Action action:actionsToSelected)  {
            boolean isSelectedChildToCurrent=nodeCurrent.getChildNodes().contains(nodeSelected);
            if (isSelectedChildToCurrent) {
                parentToSelected=Optional.of(nodeCurrent);
                actionToSelected=action;
            }
            nodeCurrent=nodeCurrent.getChild(action).orElseThrow();
        }

        if (parentToSelected.isEmpty()) {
            rootTree.printTree();
            log.warning("Parent to selected not found, probably children of root node are all terminal-fail");
            return;
        }

        NodeInterface selectedAsTerminalFail=NodeInterface.newTerminalFail(nodeSelected.getState(),actionToSelected);
        List<NodeInterface> childrenToParent=parentToSelected.get().getChildNodes();
        childrenToParent.remove(nodeSelected);
        parentToSelected.get().addChildNode(selectedAsTerminalFail);
    }

    private void updateNodesFromReturns(List<Double> GList) {
        double G;
        List<Action> actions = Action.getAllActions(actionsToSelected, actionOnSelected);
        for (NodeInterface node : nodesOnPath) {
            Action action = actions.get(nodesOnPath.indexOf(node));
            G = GList.get(nodesOnPath.indexOf(node));
            updateNode(node, G, action);
        }
    }

    private void updateNode(NodeInterface node, double G, Action action) {
        node.increaseNofVisits();
        node.increaseNofActionSelections(action);
        node.updateActionValue(G, action);
    }

    private List<Double> getReturns(List<Double> rewards) {
        double G = 0;
        List<Double> GList = new ArrayList<>();
        for (int i = rewards.size() - 1; i >= 0; i--) {
            double reward = rewards.get(i);
            G = G + discountFactor * reward;
            GList.add(G);
        }
        Collections.reverse(GList);
        return GList;
    }


}
