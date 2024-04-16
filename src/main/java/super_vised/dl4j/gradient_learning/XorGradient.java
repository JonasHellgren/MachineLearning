package super_vised.dl4j.gradient_learning;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;

/***
 * Need to be fixed, use input output data in training
 */

public class XorGradient {

    public static void main(String[] args) {
        INDArray input = Nd4j.create(new double[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}});
        INDArray output = Nd4j.create(new double[][]{
                {1, 0}, // Output 0
                {0, 1}, // Output 1
                {0, 1}, // Output 1
                {1, 0}});  // Output 0
        // Configuring the neural network
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(123)
                .updater(new Sgd(0.1)).list().layer(new DenseLayer.Builder().nIn(2).nOut(4)
                        .weightInit(WeightInit.XAVIER).activation(Activation.RELU).build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nIn(4).nOut(2).weightInit(WeightInit.XAVIER).activation(Activation.SOFTMAX)
                        .build()).build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        // model.setListeners(new ScoreIterationListener(100));
        // Print the score every 100 iterations
        int numEpochs = 1000;
        for (int i = 0; i < numEpochs; i++) {
            model.computeGradientAndScore();
            Gradient gradient = model.gradient();
            model.getUpdater().update(model, gradient, i, 0, 1, LayerWorkspaceMgr.noWorkspaces());
            model.params().subi(gradient.gradient());
            // After each epoch, you can optionally evaluate the model
            if ((i + 1) % 100 == 0) {
                INDArray predicted = model.output(input);
                System.out.println(predicted);
            }
        }
        // ... [Continue with testing and output as before] } }
    }
}