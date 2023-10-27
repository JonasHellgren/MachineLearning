package dynamic_programming2;

import dynamic_programming.domain.*;
import dynamic_programming.helpers.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestOptimalPathFinder {

    DirectedGraph graph;
    ValueMemory memory;
    OptimalPathFinder pathFinder;

    @BeforeEach
    public void init() {
        graph=TestHelper.createExampleGraph();
        ValueMemorySetter valueMemorySetter = new ValueMemorySetter(graph);
        memory = valueMemorySetter.createMemory();
        pathFinder=new OptimalPathFinder(graph,memory);
    }

    @Test
    public void whenCreated_thenCorrectSize() {
        List<Integer> actions=pathFinder.getActionsOnPath();
        System.out.println("actions = " + actions);

        assertEquals(3, actions.size());
        assertEquals(List.of(1,1,0), actions);
    }

    @Test
    public void whenCreated_thenCorrectStates() {
        List<State> statesOnOptimalPath=pathFinder.getStatesOnPath();
        System.out.println("statesOnOptimalPath = " + statesOnOptimalPath);

        assertEquals(3, statesOnOptimalPath.size());
        assertEquals(List.of(State.of(0,0),State.of(1,1),State.of(2,1)), statesOnOptimalPath);
    }

}
