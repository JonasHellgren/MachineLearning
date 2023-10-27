package dynamic_programming2;

import dynamic_programming.domain.DirectedGraph;
import dynamic_programming.domain.State;
import dynamic_programming.domain.ValueMemory;
import dynamic_programming.helpers.ValueMemorySetter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestValueMemorySetter {

    DirectedGraph graph;
    ValueMemory memory;
    ValueMemorySetter valueMemorySetter;

    @BeforeEach
    public void init() {
        graph=TestHelper.createExampleGraph();
        valueMemorySetter = new ValueMemorySetter(graph);
        memory = valueMemorySetter.createMemory();
    }

    @Test
    public void whenCreated_thenCorrectSize() {
        Assertions.assertEquals(5, memory.size());
        System.out.println("memory = " + memory);
    }

    @Test
    public void whenCreated_thenCorrectValuesAtXIs2() {
        int x = 2;
        Assertions.assertEquals(1, memory.getValue(State.of(x, 1)).orElseThrow());
        Assertions.assertEquals(3, memory.getValue(State.of(x, 0)).orElseThrow());
    }

    @Test
    public void whenCreated_thenCorrectValuesAtXIs1() {
        int x = 1;
        Assertions.assertEquals(6, memory.getValue(State.of(x, 1)).orElseThrow());
        Assertions.assertEquals(5, memory.getValue(State.of(x, 0)).orElseThrow());
    }

    @Test
    public void whenCreated_thenCorrectValuesAtXIs0() {
        int x = 0;
        Assertions.assertEquals(9, memory.getValue(State.of(x, 0)).orElseThrow());
    }


}
