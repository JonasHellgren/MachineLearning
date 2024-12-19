package book_rl_explained.radial_basis;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

 class TestActivations {


    @Test
     void testSize() {
        Activations activations = Activations.of(5);
        assertEquals(5, activations.nSamples());
    }

    @Test
     void testOf() {
        int nExamples = 5;
        Activations activations = Activations.of(nExamples);
        assertEquals(nExamples, activations.nSamples());
        for (int i = 0; i < nExamples; i++) {
            assertTrue(activations.activationsAllExamples.get(i).isEmpty());
        }
    }

    @Test
     void testEmpty() {
        Activations activations = Activations.empty();
        assertEquals(0, activations.nSamples());
    }

    @Test
     void testChangeActivations() {
        TrainData data = TrainData.ofOutputs(
                List.of(List.of(1.0), List.of(3.0)),
                List.of(1.0, 1.0));
        Kernels kernels = Kernels.empty();
        Kernel kernel = new Kernel(new double[]{1.0}, new double[]{1.0});
        kernels.addKernel(kernel);

        Activations activations = Activations.of(data.nExamples());
        activations.setActivations(data, kernels);

        // Verify that activations were changed
        assertEquals(2, activations.nSamples());
        assertNotNull(activations.activationsAllExamples.get(0));
        assertNotNull(activations.activationsAllExamples.get(1));
        assertTrue(activations.get(0,0)>activations.get(1,0));  //example 1 has higher activation
    }

    @Test
     void testChange() {
        Activations activations = Activations.of(1);
        List<Double> newActivations = Arrays.asList(1.0, 2.0);
        activations.change(0, newActivations);
        assertEquals(newActivations, activations.activationsAllExamples.get(0));
    }

    @Test
     void testGet() {
        Activations activations = Activations.of(1);
        List<Double> newActivations = Arrays.asList(1.0, 2.0);
        activations.change(0, newActivations);
        assertEquals(1.0, activations.get(0, 0), 1e-6);
        assertEquals(2.0, activations.get(0, 1), 1e-6);
    }


}
