package monte_carlo_tree_search.search_tree_node_models;

import monte_carlo_tree_search.interfaces.ActionInterface;

/** Interface for NodeNotTerminal nodes
 *  Includes methods specific for nodes with children, this to follow interface segregation principle
 * */

public interface NodeWithChildrenInterface<SSV,AV> extends NodeInterface<SSV,AV>  {

    void addChildNode(NodeInterface<SSV,AV> node);
    void increaseNofVisits();
    void increaseNofActionSelections(ActionInterface<AV> a);
    void updateActionValue(double G, ActionInterface<AV> a, double alpha, double nofVisitsExponent);
    void saveRewardForAction(ActionInterface<AV> action, double reward);
    double restoreRewardForAction(ActionInterface<AV> action);
    int getNofActionSelections(ActionInterface<AV> a);

}
