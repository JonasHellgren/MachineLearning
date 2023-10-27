package dynamic_programming.helpers;

import dynamic_programming.domain.DirectedGraph;
import dynamic_programming.domain.ValueMemory;

public class ValueMemorySetter {


    DirectedGraph graph;

    public ValueMemorySetter(DirectedGraph graph) {
        this.graph = graph;
    }

    public ValueMemory createMemory() {

        int xMax=graph.settings.xMax();

        return null;
    }


}
