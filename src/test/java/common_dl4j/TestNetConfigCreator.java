package common_dl4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.learning.config.Nesterovs;

import java.util.List;

public class TestNetConfigCreator {

    public static final double LEARNING_RATE = 0.1;
    public static final int N_HIDDEN_LAYERS = 1;

    @Test
    public void whenCreated_thenCorrect() {

        var net= MultiLayerNetworkCreator.create(NetSettings.builder()
                        .nHiddenLayers(N_HIDDEN_LAYERS)
                        .nInput(2).nHidden(5).nOutput(1)
                        .learningRate(LEARNING_RATE)
                .build());


        INDArray inputNDArray = Dl4JUtil.convertList(List.of(1d,1d),2);
        var outValue = net.output(inputNDArray,false);


        Assertions.assertEquals(LEARNING_RATE,net.getLearningRate(0));
        Assertions.assertEquals(N_HIDDEN_LAYERS,net.getnLayers()-1-1);
        Assertions.assertTrue(outValue.getDouble()<1d);

    }

}
