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
    int nofNodesNotTerminal;
    int nofNodesFail;
    int totalNofChildren;
    int nofNodesWithNoChildren;
    int nofNodesWithChildren;
    int maxDepth;
    float averageNofChildrenPerNode;
    int nofIterations;
    float usedTimeInMilliSeconds;
    float usedRelativeTimeInPercentage;
    MonteCarloSettings<SSV,AV> settings;

    public MonteCarloSearchStatistics(@NonNull NodeInterface<SSV,AV> nodeRoot,
                                      @NonNull MonteCarloTreeCreator<SSV,AV> monteCarloTreeCreator,
                                      MonteCarloSettings<SSV,AV> settings) {
        this.nodeRoot = nodeRoot;
        this.cpuTimer=monteCarloTreeCreator.cpuTimer;
        this.nofIterations=monteCarloTreeCreator.nofIterations;
        this.settings=settings;
        setStatistics();
    }

    public void setStatistics() {
        tih=new TreeInfoHelper<>(nodeRoot,settings);
        nofNodes=tih.nofNodes();
        nofNodesNotTerminal =tih.nofNodesNotTerminal();
        nofNodesFail=tih.nofNodesFail();
        totalNofChildren=tih.totalNofChildren();
        nofNodesWithNoChildren=tih.nofNodesWithNoChildren();
        nofNodesWithChildren=nofNodes-nofNodesWithNoChildren;
        maxDepth= tih.maxDepth();
        averageNofChildrenPerNode=calcAverageNofChildrensPerNodeThatHasChildren();
        usedTimeInMilliSeconds=cpuTimer.absoluteProgress();
        usedRelativeTimeInPercentage =cpuTimer.getRelativeProgress()*100;
    }

    private float calcAverageNofChildrensPerNodeThatHasChildren() {
        return ((nofNodes-nofNodesFail) / (float) nofNodesWithChildren);
    }

}
