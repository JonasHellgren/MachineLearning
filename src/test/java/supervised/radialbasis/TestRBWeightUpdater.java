package supervised.radialbasis;

import common.list_arrays.List2ArrayConverter;
import common.list_arrays.ListCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import super_vised.radial_basis.KernelProperties;
import super_vised.radial_basis.RadialBasis;
import super_vised.radial_basis.WeightUpdater;
import java.util.ArrayList;
import java.util.List;

public class TestRBWeightUpdater {

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

     @Test
      void givenTBD_whenTBD_thenTBDt() {

         List<List<Double>> inputList=List.of(List.of(0.5),List.of(0.9));
         List<Double> outputList=List.of(-1d,0d);
         List<Double> weightList= ListCreator.createListWithEqualElementValues(rb.nKernels(),0.0);

         double[][] inputs = List2ArrayConverter.convertListWithListToDoubleMat(inputList);
         double[] outputs = List2ArrayConverter.convertListToDoubleArr(outputList);
         double[] weights = List2ArrayConverter.convertListToDoubleArr(weightList);


         var updater =  WeightUpdater.of(rb);

         for (int i = 0; i < 1000 ; i++) {
             weights = updater.updateWeights(inputs, outputs, weights);
         }

         var out0=rb.output(inputs[0], weights);
         var out1=rb.output(inputs[1], weights);

         System.out.println("out0 = " + out0);
         System.out.println("out1 = " + out1);


     }


}
