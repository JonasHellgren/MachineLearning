package dl4j_aux;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;

/***
 * transformLabels does not work as expected so using separate normalizer for in and out
 */

public class TestNormalizer {
    NormalizerMinMaxScaler normalizerIn, normalizerOut;
    INDArray in,out;

    @BeforeEach
    public void init() {
        normalizerIn = new NormalizerMinMaxScaler();
        normalizerOut = new NormalizerMinMaxScaler();
        normalizerIn.setFeatureStats(getIndArr(0d, 0d), getIndArr(10d, 10d));
        normalizerOut.setFeatureStats(getIndArr(0d, 0d), getIndArr(100d, 100d));
        in=getIndArr(5,5);
        out=getIndArr(50,50);
    }

    @Test
    public void whenTransformIn_thenCorrect() {
        normalizerIn.transform(in);
        Assertions.assertEquals(getIndArr(0.5,0.5),in);
    }

    @Test
    public void whenTransformLabelOut_thenCorrect() {
        normalizerOut.transform(out);
        Assertions.assertEquals(getIndArr(0.5,0.5),out);
    }

    @Test
    public void whenTransformLabelOutAndRevert_thenCorrect() {
        normalizerOut.transform(out);
        normalizerOut.revertFeatures(out);
        Assertions.assertEquals(getIndArr(50,50),out);
    }


    private static INDArray getIndArr(double v0, double v1) {
        return Nd4j.create(new double[]{v0, v1});
    }


}
