package monte_carlo_tree_search.classes;
import common.CpuTimer;
import lombok.Getter;
import lombok.NonNull;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;

import java.util.ArrayList;
import java.util.List;


@Getter
public class MonteCarloSearchStatistics<S, A> {

    private static final String NEW_LINE = System.lineSeparator();
    NodeWithChildrenInterface<S, A> nodeRoot;
    CpuTimer cpuTimer;
    TreeInfoHelper<S, A> tih;
    MonteCarloSettings<S, A> settings;

    int nofNodes;
    int nofNodesNotTerminal;
    int nofNodesFail;
    int totalNofChildren;
    int nofNodesWithNoChildren;
    int nofNodesWithChildren;
    int maxDepth;
    List<Integer> nofNodesPerDepthLevel;
    float averageNofChildrenPerNode;
    int nofIterations;
    float usedTimeInMilliSeconds;
    float usedRelativeTimeInPercentage;


    public MonteCarloSearchStatistics(@NonNull NodeWithChildrenInterface<S, A> nodeRoot,
                                      @NonNull MonteCarloTreeCreator<S, A> monteCarloTreeCreator,
                                      @NonNull CpuTimer cpuTimer,
                                      MonteCarloSettings<S, A> settings) {
        this.nodeRoot = nodeRoot;
        this.cpuTimer = cpuTimer;
        this.nofIterations = monteCarloTreeCreator.nofIterations;
        this.settings = settings;
        this.nofNodesPerDepthLevel=new ArrayList<>();
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
        nofNodesPerDepthLevel=setNofNodesPerDepthLevel();
        averageNofChildrenPerNode = calcAerageNofChildrenPerNodeThatHasChildren();
        usedTimeInMilliSeconds = cpuTimer.absoluteProgress();
        usedRelativeTimeInPercentage = cpuTimer.getRelativeProgress() * 100;
    }

//https://en.wikipedia.org/wiki/Branching_factor
    private float calcAerageNofChildrenPerNodeThatHasChildren() {
        int nofBranches = nofNodes - 1;
        return (nofNodesWithChildren == 0)
                ? 0
                : nofBranches / (float) nofNodesWithChildren;
    }

    private List<Integer> setNofNodesPerDepthLevel() {
        List<Integer> nofNodesList=new ArrayList<>();
        for (int depth = 0; depth <= tih.maxDepth()  ; depth++) {
            int nofNodesAtDepth=tih.nofNodesAtDepth(depth);
            nofNodesList.add(nofNodesAtDepth);
        }
        return nofNodesList;
    }

    @Override
    public String toString() {

        return "{" + NEW_LINE +
                "nofNodes = " + nofNodes + NEW_LINE +
                "nofIterations = " + nofIterations + NEW_LINE +
                "nofNodesNotTerminal = " + nofNodesNotTerminal + NEW_LINE +
                "nofNodesFail = " + nofNodesFail + NEW_LINE +
                "totalNofChildren = " + totalNofChildren + NEW_LINE +
                "nofNodesWithChildren = " + nofNodesWithChildren + NEW_LINE +
                "maxDepth = " + maxDepth + NEW_LINE +
                "nofNodesPerDepthLevel = " + nofNodesPerDepthLevel + NEW_LINE +
                "averageNofChildrenPerNode = " + averageNofChildrenPerNode + NEW_LINE +
                "usedTimeInMilliSeconds = " + usedTimeInMilliSeconds + NEW_LINE +
                "usedRelativeTimeInPercentage = " + usedRelativeTimeInPercentage + NEW_LINE +
                "}";

    }

}
