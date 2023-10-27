package dynamic_programming.helpers;

import dynamic_programming.domain.DirectedGraphDP;
import dynamic_programming.domain.NodeDP;
import dynamic_programming.domain.ValueMemoryDP;
import lombok.extern.java.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dynamic_programming.domain.DirectedGraphDP.INDEX_FIRST_X;

@Log
public class OptimalPathFinderDP {
    DirectedGraphDP graph;
    ValueMemoryDP memory;

    public OptimalPathFinderDP(DirectedGraphDP graph, ValueMemoryDP memory) {
        logIfEmptyMemory(memory);
        this.graph = graph;
        this.memory = memory;
    }

    private static void logIfEmptyMemory(ValueMemoryDP memory) {
        if (memory.isEmpty()) {
            log.warning("Empty memory");
        }
    }

    public List<Integer> getActionsOnPath() {
        List<Integer> actionList = new ArrayList<>();
        NodeDP node = getFirstNode();
        ActionSelectorDP actionSelector = new ActionSelectorDP(graph, memory);
        Optional<Integer> actionOpt;
        do {
            actionOpt = actionSelector.bestAction(node);
            if (isNotReachedEndNode(actionOpt.isPresent())) {
                actionList.add(actionOpt.orElseThrow());
                node = graph.getNextNode(node, actionOpt.get());
            }
        } while (isNotReachedEndNode(actionOpt.isPresent()));

        return actionList;
    }

    private NodeDP getFirstNode() {
        var nodeSet = graph.getNodeSet();
        List<NodeDP> nodesAtXIs0 = nodeSet.stream().filter(s -> s.x() == INDEX_FIRST_X).toList();
        throwIfNotOneStartNode(nodesAtXIs0);
        return nodesAtXIs0.get(INDEX_FIRST_X);
    }

    public List<NodeDP> getNodesOnPath() {
        var actionList=getActionsOnPath();
        List<NodeDP> nodeList =new ArrayList<>();
        NodeDP node = getFirstNode();
        for (int action:actionList) {
            nodeList.add(node);
            node = graph.getNextNode(node, action);
        }
        return nodeList;
    }

    private static boolean isNotReachedEndNode(boolean isActionPresent) {
        return isActionPresent;
    }

    private static void throwIfNotOneStartNode(List<NodeDP> nodesAtXIs0) {
        if (nodesAtXIs0.size() != 1) {
            throw new IllegalArgumentException("Graph includes none or multiple start nodes, nodes = " + nodesAtXIs0);
        }
    }


}
