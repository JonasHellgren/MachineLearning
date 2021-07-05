package udemycourse.nn2refined.twolayernetwork.model;

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
        float[] errorOut=calcErrorVecOutput(outVec, calculatedOutput);
        trainAllLayers(learningRate, momentum, errorOut);
    }

    public float[] calcErrorVecOutput(float[] outVec, float[] calculatedOutput) {
        float[] errorOut = new float[calculatedOutput.length];
        for (int idxOut = 0; idxOut < calculatedOutput.length; idxOut++) {
            errorOut[idxOut] = outVec[idxOut] - calculatedOutput[idxOut];
        }
        logger.trace("error out:"+ Arrays.toString(errorOut));
        return errorOut;
    }

    private void trainAllLayers(float learningRate, float momentum, float[] errorOut) {
        float[] errorLayer = errorOut;
        for (int idxLayer = nofLayers - 1; idxLayer >= 0; idxLayer--) {
            errorLayer = layers[idxLayer].train(errorLayer, learningRate, momentum);
            logger.trace("layer i:"+idxLayer+", error:"+Arrays.toString(errorLayer));
        }
    }

}
