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
        createMemoryAndPathFinder();
    }

    private void createMemoryAndPathFinder() {
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
    public void whenCreated_thenCorrectNodes() {
        List<Node> nodesOnOptimalPath=pathFinder.getNodesOnPath();
        System.out.println("nodesOnOptimalPath = " + nodesOnOptimalPath);

        assertEquals(3, nodesOnOptimalPath.size());
        assertEquals(List.of(Node.of(0,0), Node.of(1,1), Node.of(2,1)), nodesOnOptimalPath);
    }

    @Test
    public void whenCreatedUpIsNegativeGraph_thenCorrectSize() {
        graph=TestHelper.createExampleGraphUpIsNegative();
        createMemoryAndPathFinder();

        List<Integer> actions=pathFinder.getActionsOnPath();
        System.out.println("actions = " + actions);

        assertEquals(3, actions.size());
        assertEquals(List.of(0,0,0), actions);
    }

}
