package dynamic_programming2;

import dynamic_programming.domain.State;
import dynamic_programming.domain.ValueMemory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestValueMemory {

    ValueMemory valueMemory;

    @BeforeEach
    public void init() {
        valueMemory = ValueMemory.newEmpty();
    }

    @Test
    public void whenAddingOneReward_thenCanGetByActionInState() {
        State s0 = newState(0, 0);
        State s1 = newState(1, 0);
        double valueS0 = 1d, valueS1 = 2d;
        valueMemory.addValue(s0, valueS0);
        valueMemory.addValue(s1, valueS1);

        assertTrue(valueMemory.getValue(s0).isPresent());
        assertTrue(valueMemory.getValue(s1).isPresent());
        assertEquals(valueS0,valueMemory.getValue(s0).get());
        assertEquals(valueS1,valueMemory.getValue(s1).get());
    }


    @NotNull
    private static State newState(int x, int y) {
        return State.of(x,y );
    }

}
