package dynamic_programming2;

import dynamic_programming.domain.NodeDP;
import dynamic_programming.domain.ValueMemoryDP;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestValueMemoryDP {

    ValueMemoryDP valueMemoryDP;

    @BeforeEach
    public void init() {
        valueMemoryDP = ValueMemoryDP.newEmpty();
    }

    @Test
    public void whenAddingOneReward_thenCanGetByActionInState() {
        NodeDP s0 = NodeDP.of(0, 0);
        NodeDP s1 = NodeDP.of(1, 0);
        double valueS0 = 1d, valueS1 = 2d;
        valueMemoryDP.addValue(s0, valueS0);
        valueMemoryDP.addValue(s1, valueS1);

        assertTrue(valueMemoryDP.getValue(s0).isPresent());
        assertTrue(valueMemoryDP.getValue(s1).isPresent());
        assertEquals(valueS0, valueMemoryDP.getValue(s0).get());
        assertEquals(valueS1, valueMemoryDP.getValue(s1).get());
    }


}
