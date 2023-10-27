package dynamic_programming2;

import dynamic_programming.domain.Edge;
import dynamic_programming.domain.State;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestStateAndEdge {

    @Test
    public void whenCreatedState_thenCorrect() {
        State state = State.of(0,0);
        Assertions.assertEquals(0,state.x());
        Assertions.assertEquals(0,state.y());
    }

    @Test
    public void whenCreatedTwoStates_thenCorrectEqualsAndHash() {
        State state1 = State.of(0,0);
        State state2 = State.of(1,0);

        Assertions.assertEquals(state1,state1);
        Assertions.assertNotEquals(state1,state2);
        Assertions.assertEquals(state1.hashCode(),state1.hashCode());
        Assertions.assertNotEquals(state1.hashCode(),state2.hashCode());

    }


    @Test
    public void whenCreatedEdge_thenCorrect() {
        Edge edge1 = Edge.newEdge(State.of(0,0),State.of(1,0));
        Edge edge2 = Edge.newEdge(State.of(1,0),State.of(2,0));
        System.out.println("edge1 = " + edge1);

        Assertions.assertEquals(edge1,edge1);
        Assertions.assertNotEquals(edge1,edge2);
        Assertions.assertEquals(edge1.hashCode(),edge1.hashCode());
        Assertions.assertNotEquals(edge1.hashCode(),edge2.hashCode());
    }


    @Test
    public void whenCreatedEdge_thenCorrectIsValid() {
        Edge edge1 = Edge.newEdge(State.of(0,0),State.of(1,0));
        Edge edge2 = Edge.newEdge(State.of(4,0),State.of(5,0));
        int xMax = 1, yMax = 1;
        Assertions.assertTrue(edge1.isValid(xMax, yMax));
        Assertions.assertFalse(edge2.isValid(xMax, yMax));

    }

}
