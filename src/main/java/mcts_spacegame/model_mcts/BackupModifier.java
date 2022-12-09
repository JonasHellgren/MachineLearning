package mcts_spacegame.model_mcts;

import common.Conditionals;
import common.ListUtils;
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
 *     actionsToSelected={left,left} => nodesOnPath={r,1,3}  => nodeSelected=3, nofNodesOnPath=3, nofActionsOnPath=2
 *     in nodeSelected an action will be applied leading to expansion
 *
 *
 *
 */

@Log
public class BackupModifier {

    NodeInterface rootTree;
    List<Action> actionsToSelected;
    Action actionOnSelected;
    StepReturn stepReturnOfSelected;
    MonteCarloSettings settings;

    TreeInfoHelper treeInfoHelper;
    NodeInterface nodeSelected;
    List<NodeInterface> nodesOnPath;

    //https://stackoverflow.com/questions/30717640/how-to-exclude-property-from-lombok-builder/39920328#39920328
    @Builder
    private static BackupModifier newBUM(NodeInterface rootTree,
                                         @NonNull List<Action> actionsToSelected,
                                         @NonNull Action actionOnSelected,
                                         @NonNull StepReturn stepReturnOfSelected,
                                         MonteCarloSettings settings,
                                         NodeInterface nodeSelected) {
        BackupModifier bm=new BackupModifier();
        bm.rootTree = rootTree;
        bm.actionsToSelected = actionsToSelected;
        bm.actionOnSelected = actionOnSelected;
        bm.stepReturnOfSelected=stepReturnOfSelected;
        Conditionals.executeOneOfTwo(Objects.isNull(settings),
                () -> bm.settings = MonteCarloSettings.builder().build(),
                () -> bm.settings = settings);

        bm.treeInfoHelper = new TreeInfoHelper(rootTree);
        bm.nodesOnPath = bm.treeInfoHelper.getNodesOnPathForActions(actionsToSelected).orElseThrow();

        //todo fimpa actionsToSelected
        Conditionals.executeOneOfTwo(Objects.isNull(nodeSelected),
                () -> bm.nodeSelected = bm.treeInfoHelper.getNodeReachedForActions(actionsToSelected).orElseThrow(),
                () -> bm.nodeSelected = nodeSelected);

        return bm;
    }

    public void backup() {
        backup(ListUtils.listWithZeroElements(nodesOnPath.size()));
    }

    public void backup(List<Double> returnsSimulation) {
        if (nodeSelected.isTerminalNoFail())  {
            rootTree.printTree();
            throw new RuntimeException("nodeSelected.isTerminalNoFail");
        }
        if (isAllChildrenAreTerminal()) {
            makeSelectedTerminal();
            return;
        }

        Conditionals.executeOneOfTwo(!stepReturnOfSelected.isFail,
                () -> backupNormalFromTreeSteps(returnsSimulation),
                this::backupDefensiveFromTreeSteps);
    }

    private void backupNormalFromTreeSteps(List<Double> returnsSimulation) {
        log.fine("Normal backup of selected node");
        List<Double> rewards = getRewards();
        List<Double> returnsSteps = getReturns(rewards);
        updateNodesFromReturns(returnsSteps,returnsSimulation);
    }

    private List<Double> getRewards() {

/*
        if (actionsToSelected.size() != nodesOnPath.size()) {
            log.warning("non equal lengths");
            System.out.println("actionsToSelected = " + actionsToSelected);
            System.out.println("actionOnSelected = " + actionOnSelected);
            System.out.println("nodesOnPath = " + nodesOnPath);
            System.out.println("nodeSelected = " + nodeSelected);
            nodeSelected.printTree();
            throw new RuntimeException("non equal lengths");
        }  */


        List<Double> rewards = new ArrayList<>();
        for (NodeInterface nodeOnPath : nodesOnPath) {
            if (!nodeOnPath.equals(nodeSelected)) {   //todo behövs?

                if (nodesOnPath.indexOf(nodeOnPath)>= actionsToSelected.size()) {
                    System.out.println("nodeOnPath = " + nodeOnPath);
                    System.out.println("nodeOnPath.getClass() = " + nodeOnPath.getClass());
                    System.out.println("nodeSelected = " + nodeSelected);
                    System.out.println("nodeSelected.getClass() = " + nodeSelected.getClass());
                    System.out.println("nodeOnPath.equals(nodeSelected) = " + nodeOnPath.equals(nodeSelected));
                    System.out.println("actionsToSelected = " + actionsToSelected);
                    System.out.println("actionOnSelected = " + actionOnSelected);
                    System.out.println("nodesOnPath");
                    nodesOnPath.forEach(System.out::println);

                    rootTree.printTree();
                }

                Action action = actionsToSelected.get(nodesOnPath.indexOf(nodeOnPath));
                rewards.add(nodeOnPath.restoreRewardForAction(action));
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
        this.updateNode(nodeSelected, stepReturnOfSelected.reward, actionOnSelected,settings.alphaBackupDefensive);
    }

    private void setSelectedAsTerminalIfAllItsChildrenAreTerminal() {
        boolean allChildrenAreTerminal = isAllChildrenAreTerminal();

        Conditionals.executeIfTrue(allChildrenAreTerminal,
                this::makeSelectedTerminal);
    }

    private boolean isAllChildrenAreTerminal() {
        Set<Action> children = nodeSelected.getChildNodes().stream()
                .filter(NodeInterface::isTerminalFail).map(NodeInterface::getAction)
                .collect(Collectors.toSet());
        return children.size() == Action.applicableActions().size();
    }

    public void makeSelectedTerminal() {
        log.info("making node = "+nodeSelected.getName() + " terminal, all its children are fail states");

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
            log.warning("Parent to selected not found, probably children of root node are all terminal-fail");
            //rootTree.printTree();
            return;
        } else
        {
            log.warning("Parent to selected is = "+parentToSelected.orElseThrow());
        }

        NodeInterface selectedAsTerminalFail = NodeInterface.newTerminalFail(nodeSelected.getState().copy(), actionToSelected);
        List<NodeInterface> childrenToParent = parentToSelected.get().getChildNodes();
        childrenToParent.remove(nodeSelected);
        parentToSelected.get().addChildNode(selectedAsTerminalFail);

      //  System.out.println("parentToSelected");
      //  parentToSelected.orElseThrow().printTree();
      //  System.out.println("rootTree");
      //  rootTree.printTree();

    }

    private void updateNodesFromReturns(List<Double> returnsSteps,List<Double> returnsSimulation) {
        if (returnsSteps.size() != returnsSimulation.size()) {
            throw new IllegalArgumentException("Non equal list lengths");
        }

        returnsSteps = ListUtils.multiplyListElements(returnsSteps, settings.weightReturnsSteps);
        returnsSimulation = ListUtils.multiplyListElements(returnsSimulation, settings.weightReturnsSimulation);
        List<Double> returnsSum= ListUtils.sumListElements(returnsSteps,returnsSimulation);
        List<Action> actions = Action.getAllActions(actionsToSelected, actionOnSelected);
        for (NodeInterface node : nodesOnPath) {
            Action action = actions.get(nodesOnPath.indexOf(node));
            double singleReturn = returnsSum.get(nodesOnPath.indexOf(node));
            this.updateNode(node, singleReturn, action,settings.alphaBackupNormal);
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

     void updateNode(NodeInterface node, double singleReturn, Action action, double alpha) {
        node.increaseNofVisits();
        node.increaseNofActionSelections(action);
        node.updateActionValue(singleReturn, action,alpha);
    }

}
