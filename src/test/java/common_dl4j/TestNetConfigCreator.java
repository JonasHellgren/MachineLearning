package common_dl4j;

import common.dl4j.Dl4JUtil;
import common.dl4j.MultiLayerNetworkCreator;
import common.dl4j.NetSettings;
import super_vised.dl4j.regression_2023.classes.SumDataGenerator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import java.util.ArrayList;
import java.util.List;

public class TestNetConfigCreator {

    public static final double LEARNING_RATE = 0.1;
    public static final int N_HIDDEN_LAYERS = 1;

    static MultiLayerNetwork net;

    @BeforeEach
    public void init() {
        net= MultiLayerNetworkCreator.create(NetSettings.builder()
                .nHiddenLayers(N_HIDDEN_LAYERS)
                .nInput(2).nHidden(2).nOutput(1)
                .learningRate(LEARNING_RATE)
                .build());
    }

    @Test
    public void whenCreated_thenCorrect() {
        INDArray inputNDArray = Dl4JUtil.convertListToOneRow(List.of(1d,1d));
        var outValue = net.output(inputNDArray,false);
        Assertions.assertEquals(LEARNING_RATE,net.getLearningRate(0));
        Assertions.assertEquals(N_HIDDEN_LAYERS,net.getnLayers()-1-1);
        Assertions.assertTrue(outValue.getDouble()<1d);
    }


    @Test
    public void whenCreated_thenCanBeTrained() {
        var errors=trainMemory();
        errors.forEach(System.out::println);
        Assertions.assertTrue(errors.get(0)>errors.get(errors.size()-1));
    }



    private static List<Double> trainMemory() {
        int nEpochs = 10;
        var dataGenerator = SumDataGenerator.builder()
                .minValue(0d).maxValue(1d).nSamplesPerEpoch(10).build();
        List<Double> errors=new ArrayList<>();
        for (int i = 0; i < nEpochs; i++) {
            var trainData = dataGenerator.getTrainingDataIndArray();
            net.fit(trainData.getFirst(),trainData.getSecond());
            double error=net.gradientAndScore().getSecond();
            errors.add(error);
        }
        return errors;
    }


}
