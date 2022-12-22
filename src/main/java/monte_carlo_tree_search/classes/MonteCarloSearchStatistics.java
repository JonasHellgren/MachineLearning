package monte_carlo_tree_search.classes;

import common.CpuTimer;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.node_models.NodeInterface;


/**
 * The average branching factor can be quickly calculated as the number of non-root nodes (the size of the tree,
 * minus one; or the number of edges) divided by the number of non-leaf nodes (the number of nodes with children).
 */


@ToString
@Getter
public class MonteCarloSearchStatistics<SSV, AV> {

    private static final String NEW_LINE = System.lineSeparator();
    @ToString.Exclude
    NodeInterface<SSV, AV> nodeRoot;
    @ToString.Exclude
    CpuTimer cpuTimer;
    @ToString.Exclude
    TreeInfoHelper<SSV, AV> tih;

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
    MonteCarloSettings<SSV, AV> settings;

    public MonteCarloSearchStatistics(@NonNull NodeInterface<SSV, AV> nodeRoot,
                                      @NonNull MonteCarloTreeCreator<SSV, AV> monteCarloTreeCreator,
                                      MonteCarloSettings<SSV, AV> settings) {
        this.nodeRoot = nodeRoot;
        this.cpuTimer = monteCarloTreeCreator.cpuTimer;
        this.nofIterations = monteCarloTreeCreator.nofIterations;
        this.settings = settings;
        setStatistics();
    }

    public void setStatistics() {
        tih = new TreeInfoHelper<>(nodeRoot, settings);
        nofNodes = tih.nofNodes();
        nofNodesNotTerminal = tih.nofNodesNotTerminal();
        nofNodesFail = tih.nofNodesFail();
        totalNofChildren = tih.totalNofChildren();
        nofNodesWithNoChildren = tih.nofNodesWithNoChildren();
        nofNodesWithChildren = nofNodes - nofNodesWithNoChildren;
        maxDepth = tih.maxDepth();
        averageNofChildrenPerNode = calcAverageNofChildrenPerNodeThatHasChildren();
        usedTimeInMilliSeconds = cpuTimer.absoluteProgress();
        usedRelativeTimeInPercentage = cpuTimer.getRelativeProgress() * 100;
    }

    private float calcAverageNofChildrenPerNodeThatHasChildren() {
        int nofBranches = nofNodes - 1;
        return (nofNodesWithChildren == 0)
                ? 0
                : nofBranches / (float) nofNodesWithChildren;
    }

    @Override
    public String toString() {

        return "{" + NEW_LINE +
                "nofNodes = " + nofNodes + NEW_LINE +
                "nofNodesNotTerminal = " + nofNodesNotTerminal + NEW_LINE +
                "nofNodesFail = " + nofNodesFail + NEW_LINE +
                "totalNofChildren = " + totalNofChildren + NEW_LINE +
                "nofNodesWithChildren = " + nofNodesWithChildren + NEW_LINE +
                "maxDepth = " + maxDepth + NEW_LINE +
                "averageNofChildrenPerNode = " + averageNofChildrenPerNode + NEW_LINE +
                "usedTimeInMilliSeconds = " + usedTimeInMilliSeconds + NEW_LINE +
                "usedRelativeTimeInPercentage = " + usedRelativeTimeInPercentage + NEW_LINE +
                "}";

    }

}
