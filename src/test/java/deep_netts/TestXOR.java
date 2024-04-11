package deep_netts;

/*
import deepnetts.data.MLDataItem;
import deepnetts.data.TabularDataSet;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
*/
import org.junit.Test;

import javax.visrec.ml.data.DataSet;

public class TestXOR {

    /*
    // Create XOR dataset
    float[][] inputs = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
    float[][] outputs = {{0}, {1}, {1}, {0}};

    @Test
    public void eval() {

      // TabularDataSet dataSet = new TabularDataSet(2,1);  //TabularDataSet.create(inputs, targets);

      //  TabularDataSet<MLDataItem> trainingSet = new TabularDataSet<>(4, 3); // 4 inputs and 3 outputs

        System.out.println("inputs[0].length = " + inputs[0].length);
        System.out.println("outputs[0].length = " + outputs[0].length);

        DataSet<MLDataItem> trainingSet = new TabularDataSet<>(inputs[0].length, outputs[0].length);

        for (int i = 0; i < inputs.length; i++) {
            trainingSet.add(new TabularDataSet.Item(inputs[i], outputs[i]));
        }

        // Create a feedforward neural network
        FeedForwardNetwork neuralNet = new FeedForwardNetwork.Builder()
                .addFullyConnectedLayer(2, ActivationType.TANH)
                .addOutputLayer(1, ActivationType.SIGMOID)
                .lossFunction(LossType.MEAN_SQUARED_ERROR)
                .build();

        // Train the network


        neuralNet.train(trainingSet);

        // Evaluate the network
        float[][] testInputs = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};

        float[] testInput = testInputs[0];
        float[] predictedOutputs = neuralNet.predict(testInput);

        // Print the predicted outputs
        System.out.println("XOR Predictions:");
        for (int i = 0; i < testInputs.length; i++) {
            float[] input = testInputs[i];
            float[] output = new float[]{predictedOutputs[i]};
            System.out.println(input[0] + " XOR " + input[1] + " = " + output[0]);
        }

        // Save the trained model
       // DeepNetts.save(neuralNet, "xor_model.dnet");

        System.out.println("Model saved successfully.");

    }
*/

}
