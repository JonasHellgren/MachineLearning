package dynamic_programming2;

import dynamic_programming.domain.Edge;
import dynamic_programming.domain.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestStateAndEdge {

    @Test
    public void whenCreatedState_thenCorrect() {
        Node node = Node.of(0,0);
        Assertions.assertEquals(0, node.x());
        Assertions.assertEquals(0, node.y());
    }

    @Test
    public void whenCreatedTwoStates_thenCorrectEqualsAndHash() {
        Node node1 = Node.of(0,0);
        Node node2 = Node.of(1,0);

        Assertions.assertEquals(node1, node1);
        Assertions.assertNotEquals(node1, node2);
        Assertions.assertEquals(node1.hashCode(), node1.hashCode());
        Assertions.assertNotEquals(node1.hashCode(), node2.hashCode());

    }


    @Test
    public void whenCreatedEdge_thenCorrect() {
        Edge edge1 = Edge.of(Node.of(0,0), Node.of(1,0));
        Edge edge2 = Edge.of(Node.of(1,0), Node.of(2,0));
        System.out.println("edge1 = " + edge1);

        Assertions.assertEquals(edge1,edge1);
        Assertions.assertNotEquals(edge1,edge2);
        Assertions.assertEquals(edge1.hashCode(),edge1.hashCode());
        Assertions.assertNotEquals(edge1.hashCode(),edge2.hashCode());
    }


    @Test
    public void whenCreatedEdge_thenCorrectIsValid() {
        Edge edge1 = Edge.of(Node.of(0,0), Node.of(1,0));
        Edge edge2 = Edge.of(Node.of(4,0), Node.of(5,0));
        int xMax = 1, yMax = 1;
        Assertions.assertTrue(edge1.isValid(xMax, yMax));
        Assertions.assertFalse(edge2.isValid(xMax, yMax));

    }

}
