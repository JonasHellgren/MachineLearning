package mcts_spacegame.model_mcts;

import common.CpuTimer;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.models_mcts_nodes.NodeInterface;

@ToString
@Getter
public class MonteCarloSearchStatistics {

    @ToString.Exclude NodeInterface nodeRoot;
    @ToString.Exclude CpuTimer cpuTimer;
    @ToString.Exclude  TreeInfoHelper tih;

    int nofNodes;
    int nofNodesWithNoChildren;
    int maxDepth;
    float averageNofChildrenPerNode;
    int nofIterations;
    float usedTimeInMilliSeconds;
    float usedRelativeTimeInPercentage;
    MonteCarloSettings settings;

    public MonteCarloSearchStatistics(@NonNull  NodeInterface nodeRoot,
                                      @NonNull CpuTimer cpuTimer,
                                      int nofIterations,
                                      MonteCarloSettings settings) {
        this.nodeRoot = nodeRoot;
        this.cpuTimer=cpuTimer;
        this.nofIterations=nofIterations;
        this.settings=settings;
    }

    public void setStatistics() {
        tih=new TreeInfoHelper(nodeRoot,settings);
        nofNodes=tih.nofNodesInTree();
        nofNodesWithNoChildren=tih.nofNodesWithNoChildren();
        maxDepth= tih.maxDepth();
        averageNofChildrenPerNode=calcAverageNofChildrensPerNodeThatHasChildren();
        usedTimeInMilliSeconds=cpuTimer.absoluteProgress();
        usedRelativeTimeInPercentage =cpuTimer.getRelativeProgress()*100;
    }

    private float calcAverageNofChildrensPerNodeThatHasChildren() {
        int totalNofChildren=tih.totalNofChildren();
        int nofNodesWithChildren=nofNodes-nofNodesWithNoChildren;
        return (totalNofChildren / (float) nofNodesWithChildren);
    }

}
