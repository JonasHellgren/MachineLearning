package dynamic_programming2;

import dynamic_programming.domain.DirectedGraph;
import dynamic_programming.domain.Edge;
import dynamic_programming.domain.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDirectedGraph {

    public static final int X_MAX = 3, Y_MAX = 1;
    DirectedGraph graph;

    @BeforeEach
    public void init() {
        graph=DirectedGraph.newWithSize(X_MAX, Y_MAX);
    }

    @Test
    public void whenAddingOneReward_thenCorrect() {
        graph.addEdgeWithReward(Edge.of(Node.of(0,0), Node.of(1,0)),1d);
        assertEquals(1,graph.size());
    }

    @Test
    public void whenAddingOneReward_thenCanGet() {
        Edge edge = Edge.of(Node.of(0, 0), Node.of(1, 0));
        double reward = 1d;
        graph.addEdgeWithReward(edge, reward);
        assertTrue(graph.getReward(edge).isPresent());
        assertEquals(reward,graph.getReward(edge).get());
    }

    @Test
    public void whenAdding_thenCorrectStateSet() {
        double reward = 1d;
        Edge edge1 = Edge.of(Node.of(0, 0), Node.of(1, 0));
        Edge edge2 = Edge.of(Node.of(0, 0), Node.of(1, 1));
        Edge edge3 = Edge.of(Node.of(1, 0), Node.of(2, 0));
        Edge edge4 = Edge.of(Node.of(1, 0), Node.of(2, 1));

        graph.addEdgeWithReward(edge1, reward);
        graph.addEdgeWithReward(edge2, reward);
        graph.addEdgeWithReward(edge3, reward);
        graph.addEdgeWithReward(edge4, reward);

        var states=graph.getNodeSet();

        System.out.println("states = " + states);

        Assertions.assertEquals(5,states.size());

    }

    @Test
    public void whenAddingOneReward_thenCanGetByActionInState() {
        Node s0 = Node.of(0, 0);
        Node s1 = Node.of(1, 0);
        Edge edge = Edge.of(s0, s1);
        double reward = 1d;
        graph.addEdgeWithReward(edge, reward);
        assertTrue(graph.getReward(s0,0).isPresent());
        assertEquals(reward,graph.getReward(s0,0).get());
        Assertions.assertFalse(graph.getReward(s1,0).isPresent());
    }

    @Test
    public void whenGetStateNew_thenCorrect() {
        Node s00 = Node.of(0, 0);
        Node s10 = Node.of(1, 0);
        Node s11 = Node.of(1, 1);
        Node s30 = Node.of(3, 0);

        assertEquals(s10,graph.getNextNode(s00,0));
        assertEquals(s11,graph.getNextNode(s00,1));
        assertEquals(s30,graph.getNextNode(s30,0));  //cant move on
    }

}
