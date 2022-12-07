package mcts_spacegame.model_mcts;

import common.Conditionals;
import common.CpuTimer;
import lombok.Builder;
import lombok.Getter;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log
@Setter
@Getter
public class MonteCarloTreeCreator {

    Environment environment;
    State startState;
    NodeValueMemory memory;
    MonteCarloSettings settings;

    NodeInterface nodeRoot;
    TreeInfoHelper tih;
    CpuTimer cpuTimer;
    int nofIterations;

    List<Action> actionsToSelected;
    Action actionInSelected;

    @Builder
    private static MonteCarloTreeCreator newMCTC(@NonNull Environment environment,
                                                 @NonNull State startState,
                                                 MonteCarloSettings monteCarloSettings,
                                                 NodeValueMemory memory) {
        MonteCarloTreeCreator mctc=new MonteCarloTreeCreator();
        mctc.environment = environment;
        mctc.startState = startState;
        mctc.settings = monteCarloSettings;

        Conditionals.executeOneOfTwo(Objects.isNull(monteCarloSettings),
                () -> mctc.settings = MonteCarloSettings.newDefault(),
                () -> mctc.settings = monteCarloSettings);

        Conditionals.executeOneOfTwo(Objects.isNull(memory),
                () -> mctc.memory = NodeValueMemory.newEmpty(),
                () -> mctc.memory = memory);

        setSomeFields(startState, mctc);
        return mctc;
    }

    private static void setSomeFields(@NonNull State startState, MonteCarloTreeCreator mctc) {
        mctc.nodeRoot = NodeInterface.newNotTerminal(startState, Action.notApplicable);
        mctc.tih = new TreeInfoHelper(mctc.nodeRoot);
        mctc.cpuTimer = new CpuTimer(mctc.settings.timeBudgetMilliSeconds);
        mctc.nofIterations=0;
    }

    public NodeInterface doMCTSIterations() {
        setSomeFields(startState, this);  //needed because setStartState will not effect correctly otherwise
        this.cpuTimer.reset();
        int i;
        for (i = 0; i < settings.maxNofIterations; i++) {
            NodeInterface nodeSelected = select(nodeRoot);
            StepReturn sr = chooseActionAndExpand(nodeSelected);
            SimulationResults simulationResults=simulate(nodeSelected);
            backPropagate(sr,simulationResults);

            if (cpuTimer.isTimeExceeded()) {
                log.warning("Time exceeded");
                break;
            }
        }
        nofIterations=i;
        this.cpuTimer.stop();
        log.info("time used = " + cpuTimer.getAbsoluteProgress());

        return nodeRoot;
    }

    public MonteCarloSearchStatistics getStatistics() {
        MonteCarloSearchStatistics statistics=new MonteCarloSearchStatistics(nodeRoot,cpuTimer,nofIterations);
        statistics.setStatistics();
        return statistics;
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

        Conditionals.executeIfTrue(isChildAddedEarlier, () ->
            log.warning("Child has been added earlier, child = "+child+", in node = "+nodeSelected));

        Conditionals.executeIfTrue(isSelectedNotTerminal && !isChildAddedEarlier && isChildNotToDeep, () ->
            nodeSelected.addChildNode(child));
        return sr;
    }

    public SimulationResults simulate(NodeInterface nodeSelected) {
        SimulationResults simulationResults=new SimulationResults();
        State pos= nodeSelected.getState().copy();
        for (int i = 0; i <settings.nofSimulationsPerNode ; i++) {
            List<StepReturn> returns = stepToTerminal(pos.copy(), settings.policy);
            StepReturn endReturn = returns.get(returns.size()-1);
            double sumOfRewards=returns.stream().mapToDouble(r -> r.reward).sum();
            double valueInTerminal=memory.read(endReturn.newPosition);
            boolean isEndingInFail=endReturn.isFail;
            simulationResults.add(sumOfRewards,valueInTerminal,isEndingInFail);
        }
        return simulationResults;
    }

    private void backPropagate(StepReturn sr,SimulationResults simulationResults) {
        BackupModifierFromSteps bumSteps = BackupModifierFromSteps.builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .settings(settings)
                .stepReturnOfSelected(sr)
                .build();
        Conditionals.executeIfTrue(settings.isBackupFromSteps,
                bumSteps::backup);

        BackupModifierFromSimulations bumSim = BackupModifierFromSimulations.builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .settings(settings)
                .simulationResults(simulationResults)
                .build();
        bumSim.backup();

    }

    private List<StepReturn> stepToTerminal(State pos,SimulationPolicyInterface policy) {
        List<StepReturn> returns=new ArrayList<>();
        StepReturn stepReturn;
        do {
            Action action=policy.chooseAction(pos);
            stepReturn = environment.step(action, pos);
            pos.setFromReturn(stepReturn);
            returns.add(stepReturn);
        } while (!stepReturn.isTerminal);
        return returns;
    }

}
