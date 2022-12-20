package monte_carlo_tree_search.classes;

import common.CpuTimer;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.node_models.NodeInterface;

@ToString
@Getter
public class MonteCarloSearchStatistics<SSV,AV> {

    @ToString.Exclude
    NodeInterface<SSV,AV> nodeRoot;
    @ToString.Exclude CpuTimer cpuTimer;
    @ToString.Exclude  TreeInfoHelper<SSV,AV> tih;

    int nofNodes;
    int nofNodesWithNoChildren;
    int maxDepth;
    float averageNofChildrenPerNode;
    int nofIterations;
    float usedTimeInMilliSeconds;
    float usedRelativeTimeInPercentage;
    MonteCarloSettings<SSV,AV> settings;

    public MonteCarloSearchStatistics(@NonNull  NodeInterface<SSV,AV> nodeRoot,
                                      @NonNull CpuTimer cpuTimer,
                                      int nofIterations,
                                      MonteCarloSettings<SSV,AV> settings) {
        this.nodeRoot = nodeRoot;
        this.cpuTimer=cpuTimer;
        this.nofIterations=nofIterations;
        this.settings=settings;
    }

    public void setStatistics() {
        tih=new TreeInfoHelper<>(nodeRoot,settings);
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
