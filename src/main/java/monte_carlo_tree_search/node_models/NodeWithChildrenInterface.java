package monte_carlo_tree_search.node_models;

import monte_carlo_tree_search.generic_interfaces.ActionInterface;

import java.util.List;
import java.util.Optional;

public interface NodeWithChildrenInterface<SSV,AV> extends NodeInterface<SSV,AV>  {

    void addChildNode(NodeInterface<SSV,AV> node);


    void increaseNofVisits();
    void increaseNofActionSelections(ActionInterface<AV> a);
    void updateActionValue(double G, ActionInterface<AV> a, double alpha);
    void saveRewardForAction(ActionInterface<AV> action, double reward);
    double restoreRewardForAction(ActionInterface<AV> action);


    int getNofActionSelections(ActionInterface<AV> a);


}
