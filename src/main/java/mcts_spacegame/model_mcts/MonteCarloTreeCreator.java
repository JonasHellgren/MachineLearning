package mcts_spacegame.model_mcts;

import common.ConditionalUtils;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.helpers.NodeInfoHelper;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.State;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@Log
@Setter
public class MonteCarloTreeCreator {

    Environment environment;
    State startState;
    MonteCarloSettings settings;

    NodeInterface nodeRoot;
    TreeInfoHelper tih;
    List<Action> actionsToSelected;
    Action actionInSelected;

    @Builder
    private static MonteCarloTreeCreator newMCTC(@NonNull Environment environment,
                                                 @NonNull State startState,
                                                 MonteCarloSettings monteCarloSettings) {
        MonteCarloTreeCreator mctc=new MonteCarloTreeCreator();
        mctc.environment = environment;
        mctc.startState = startState;
        mctc.settings = monteCarloSettings;

        ConditionalUtils.executeDependantOnCondition(Objects.isNull(monteCarloSettings),
                () -> mctc.settings = MonteCarloSettings.newDefault(),
                () -> mctc.settings = monteCarloSettings);

        mctc.nodeRoot = NodeInterface.newNotTerminal(startState, Action.notApplicable);
        mctc.tih=new TreeInfoHelper(mctc.nodeRoot);
        return mctc;
    }

    public NodeInterface doMCTSIterations() {
        for (int i = 0; i < settings.maxNofIterations; i++) {
            NodeInterface nodeSelected = select(nodeRoot);
            StepReturn sr = chooseActionAndExpand(nodeSelected);
            //todo simulation
            backPropagate(sr);
        }
        return nodeRoot;
    }

    private NodeInterface select(NodeInterface nodeRoot) {
        NodeSelector ns = new NodeSelector(nodeRoot,settings.coefficientExploitationExploration);
        NodeInterface nodeSelected=ns.select();
        actionsToSelected = ns.getActionsFromRootToSelected();
        return nodeSelected;
    }

    @NotNull
    private StepReturn chooseActionAndExpand(NodeInterface nodeSelected) {
        State state = TreeInfoHelper.getState(startState, environment, actionsToSelected);
        ActionSelector as=new ActionSelector(nodeSelected);
        actionInSelected=as.select();
        StepReturn sr = environment.step(actionInSelected, state);
        nodeSelected.saveRewardForAction(actionInSelected, sr.reward);
        NodeInterface child = NodeInterface.newNode(sr, actionInSelected);
        child.setDepth(nodeSelected.getDepth()+1);  //easy to forget
        boolean isChildAddedEarlier= NodeInfoHelper.findNodeMatchingNode(nodeSelected.getChildNodes(),child).isPresent();
        boolean isSelectedNotTerminal= nodeSelected.isNotTerminal();
        boolean isChildNotToDeep=child.getDepth()<=settings.maxTreeDepth;

        if (isChildAddedEarlier) {
            log.warning("Child has been added earlier, child = "+child+", in node = "+nodeSelected);
        }

        if (isSelectedNotTerminal && !isChildAddedEarlier && isChildNotToDeep)  {
            nodeSelected.addChildNode(child); }
        return sr;
    }

    private void backPropagate(StepReturn sr) {
        BackupModifier bum = BackupModifier.builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(sr)
                .build();
        bum.backup();
    }

}
