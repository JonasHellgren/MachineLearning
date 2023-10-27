package dynamic_programming2;

import dynamic_programming.domain.DirectedGraphDP;
import dynamic_programming.domain.NodeDP;
import dynamic_programming.domain.ValueMemoryDP;
import dynamic_programming.domain.MemoryTrainerDP;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestValueMemoryTrainer {

    DirectedGraphDP graph;
    ValueMemoryDP memory;
    MemoryTrainerDP trainer;

    @BeforeEach
    public void init() {
        graph=TestHelper.createExampleGraph();
        trainer = new MemoryTrainerDP(graph);
        memory = trainer.createMemory();
    }

    @Test
    public void whenCreated_thenCorrectSize() {
        Assertions.assertEquals(5, memory.size());
        System.out.println("memory = " + memory);
    }

    @Test
    public void whenCreated_thenCorrectValuesAtXIs2() {
        int x = 2;
        Assertions.assertEquals(1, memory.getValue(NodeDP.of(x, 1)).orElseThrow());
        Assertions.assertEquals(3, memory.getValue(NodeDP.of(x, 0)).orElseThrow());
    }

    @Test
    public void whenCreated_thenCorrectValuesAtXIs1() {
        int x = 1;
        Assertions.assertEquals(6, memory.getValue(NodeDP.of(x, 1)).orElseThrow());
        Assertions.assertEquals(5, memory.getValue(NodeDP.of(x, 0)).orElseThrow());
    }

    @Test
    public void whenCreated_thenCorrectValuesAtXIs0() {
        int x = 0;
        Assertions.assertEquals(9, memory.getValue(NodeDP.of(x, 0)).orElseThrow());
    }


}
