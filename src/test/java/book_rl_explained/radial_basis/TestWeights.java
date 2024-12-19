package book_rl_explained.radial_basis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestWeights {
    @Test
    void testAllZeroWeights() {
        Weights weights = Weights.allZero(5);
        assertEquals(5, weights.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(0.0, weights.get(i), 1e-6);
        }
    }

    @Test
    void testAllWithValue() {
        int size = 5;
        double value = 2.5;
        Weights weights = Weights.allWithValue(size, value);

        assertEquals(size, weights.size());
        for (int i = 0; i < size; i++) {
            assertEquals(value, weights.get(i), 1e-6);
        }
    }

    @Test
    void testGetAndSet() {
        Weights weights = Weights.allZero(3);
        weights.set(1, 2.5);
        assertEquals(2.5, weights.get(1), 1e-6);
    }

    @Test
    void testSize() {
        Weights weights = Weights.allZero(10);
        assertEquals(10, weights.size());
    }


}
