package common_dl4j;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.jetbrains.annotations.NotNull;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class MultiLayerNetworkCreator {


     public static MultiLayerNetwork create(NetSettings settings) {
        var confBuilder = new NeuralNetConfiguration.Builder()
                .seed(settings.seed())
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(settings.learningRate(), settings.momentum()))
                .list();
         addInputLayer(settings, confBuilder);
         addHiddenLayers(settings, confBuilder);
         addOutPutLayer(settings, confBuilder);
         return buildNetwork(confBuilder);


    }

    @NotNull
    private static MultiLayerNetwork buildNetwork(NeuralNetConfiguration.ListBuilder confBuilder) {
        MultiLayerConfiguration conf = confBuilder.build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        return net;
    }

    private static void addOutPutLayer(NetSettings settings, NeuralNetConfiguration.ListBuilder confBuilder) {
        confBuilder.layer(settings.nHiddenLayers() + 1, new OutputLayer.Builder(settings.lossFunction())
                .activation(settings.activOutLayer())
                .nIn(settings.nHidden()).nOut(settings.nOutput()).build());
    }

    private static void addHiddenLayers(NetSettings settings, NeuralNetConfiguration.ListBuilder confBuilder) {
        for (int i = 1; i <= settings.nHiddenLayers(); i++) {
            confBuilder.layer(i, new DenseLayer.Builder().nIn(settings.nHidden()).nOut(settings.nHidden())
                    .activation(settings.activHiddenLayer())
                    .build());
        }
    }

    private static void addInputLayer(NetSettings settings, NeuralNetConfiguration.ListBuilder confBuilder) {
        confBuilder.layer(0, new DenseLayer.Builder().nIn(settings.nInput()).nOut(settings.nHidden())
                .activation(settings.activInLayer())
                .build());
    }


}
