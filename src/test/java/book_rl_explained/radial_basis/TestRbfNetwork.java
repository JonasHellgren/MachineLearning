package book_rl_explained.radial_basis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

public class TestRbfNetwork {

    public static final double LEARNING_RATE = 0.5;
    public static final double TOL = 0.01;
    public static final int N_FITS = 100;
    RbfNetwork rbfNetwork;
    TrainData data;

    @BeforeEach
    void init() {
        data=TrainData.emptyFourOutputs();
        data.addInOutPair(List.of(0.0), 1.0);
        data.addInOutPair(List.of(1.0), 2.0);
        Kernels kernels = Kernels.empty();
        Kernel kernel1 = Kernel.ofSigmas(new double[]{0.0}, new double[]{1.0});
        Kernel kernel2 = Kernel.ofSigmas(new double[]{1.0}, new double[]{1.0});
        kernels.addKernel(kernel1);
        kernels.addKernel(kernel2);

        rbfNetwork = RbfNetwork.of(kernels, LEARNING_RATE);
    }

    @Test
    void whenTrained_thenCorrectOutput() {
        rbfNetwork.train(data, N_FITS);
        double out0 = rbfNetwork.outPut(data.input(0));
        double out1 = rbfNetwork.outPut(data.input(1));
        Assertions.assertEquals(data.output(0), out0, TOL);
        Assertions.assertEquals(data.output(1), out1, TOL);
    }

}
