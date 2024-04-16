package dl4j_aux;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.transforms.custom.SoftMax;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.util.Arrays;

public class TestSoftMax {


    @Test
    public void softMaxSumIsOne() {

        INDArray input = Nd4j.create(new float[]{1.0f, 2.0f, 3.0f});

        // Execute SoftMax along the first dimension (dimension 0 for a 1D array)
        INDArray output = Nd4j.getExecutioner().exec(new SoftMax(input))[0];
        INDArray logProb= Transforms.log(output);

        System.out.println("logProb = " + logProb);
        System.out.println("SoftMax applied on INDArray: " + output);


        Assertions.assertTrue(output.getDouble(2)>output.getDouble(1));
        Assertions.assertEquals(1, Arrays.stream(output.toDoubleVector()).sum(),0.01);


    }
}
