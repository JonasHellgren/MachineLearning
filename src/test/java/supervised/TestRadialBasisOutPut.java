package supervised;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import super_vised.radial_basis.KernelProperties;
import super_vised.radial_basis.RadialBasis;
import java.util.ArrayList;
import java.util.Collections;

public class TestRadialBasisOutPut {

    public static final int GAMMA = 100;
    RadialBasis rb;

    @BeforeEach
    void init() {
        var kernels = new ArrayList<KernelProperties>();
        for (int i = 0; i < 10; i++) {
            double center = (double) i / 10;
            kernels.add(KernelProperties.of(new double[]{center}, GAMMA));
        }
        rb = new RadialBasis(kernels);
    }

    @ParameterizedTest
    @CsvSource({
            "0.1, 1",
            "0.5, 5",
            "0.7, 7"
    })
    void givenX_thenCorrectIndexActiveKernel(double x, int expectedIndex) {
        var activations = rb.activations(new double[]{x});
        int maxIndex = activations.indexOf(Collections.max(activations));
        Assertions.assertEquals(expectedIndex, maxIndex);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0",
            "0.5, 1",
            "1, 0"
    })
    void givenXandWeights_whenCorrect(double x, int expectedOut) {
        double[] weights = new double[]{0, 0, 0, 0, 0, 1, 0, 0, 0, 0};
        double out = rb.output(new double[]{x}, weights);
        double delta = 0.01;
        Assertions.assertEquals(expectedOut, out, delta);
    }

}
