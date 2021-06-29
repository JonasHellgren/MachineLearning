package udemycourse.nn2refined.twolayernetwork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class NeuralNetwork {

    private static final Logger logger = LoggerFactory.getLogger(NeuralNetwork.class);

    private final Layer[] layers;
    final int nofLayers;
    final int nofOutputs;

    public NeuralNetwork(int nofLayers, int inputSize, int hiddenSize, int outputSize) {
        layers = new Layer[nofLayers];
        layers[0] = new Layer(inputSize, hiddenSize);
        layers[1] = new Layer(hiddenSize, outputSize);
        this.nofLayers=nofLayers;
        this.nofOutputs=outputSize;
    }

    public Layer getLayer(int index) {
        return layers[index];
    }

    public float[] calcOutput(float[] inVec) {
        float[] outVec=null;
        for (int idxLayer = 0; idxLayer < nofLayers; idxLayer++) {
            outVec = layers[idxLayer].calcOut(inVec);
            inVec=outVec;  //inVec to next (more right) layer
        }
        return outVec;
    }

    public void train(float[] inVec, float[] outVec, float learningRate, float momentum) {

        float[] calculatedOutput = calcOutput(inVec);
        float[] errorOut = new float[nofOutputs];

        calcErrorVecOutput(outVec, calculatedOutput, errorOut);
        trainAllLayers(learningRate, momentum, errorOut);
    }

    private void calcErrorVecOutput(float[] outVec, float[] calculatedOutput, float[] errorOut) {
        for (int i = 0; i < errorOut.length; i++) {
            errorOut[i] = outVec[i] - calculatedOutput[i];
        }
        logger.trace("error out:"+ Arrays.toString(errorOut));
    }

    private void trainAllLayers(float learningRate, float momentum, float[] errorOut) {
        float[] errorLayer = errorOut;
        for (int i = nofLayers - 1; i >= 0; i--) {
            errorLayer = layers[i].train(errorLayer, learningRate, momentum);
            logger.trace("layer i:"+i+", error:"+Arrays.toString(errorLayer));
        }
    }

}
