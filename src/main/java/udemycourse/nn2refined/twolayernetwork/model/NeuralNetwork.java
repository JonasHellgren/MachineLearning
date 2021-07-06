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

    public void showProgress(float[][] inData,
                             float[][] outData,
                             int iteration) {

        float errorSum=0;
        for (int idxDataPoint = 0; idxDataPoint < outData.length; idxDataPoint++) {
            float[] inVec = inData[idxDataPoint];
            float[] calculatedOutput = calcOutput(inVec);
            float[] errorOut= calcErrorVecOutput(outData[idxDataPoint], calculatedOutput);
            errorSum=errorSum+ calcSingleLoss(errorOut);
        }
        System.out.println("Iteration:"+ iteration+", avgError:"+errorSum/outData.length);
    }

    public void showNetworkResponse(float[][] inData,
                                    float[][] outData) {

        int nofCorrectClassifications=0;
        for (int idxDataPoint = 0; idxDataPoint < outData.length; idxDataPoint++) {
            float[] inVec = inData[idxDataPoint];
            System.out.print("in:"+ Arrays.toString(inVec)+"--> ");
            float[] resVec = calcOutput(inVec);
            System.out.print(Arrays.toString(roundArrayItems(resVec)));
            System.out.println();
            nofCorrectClassifications=(getIndexOfMax(resVec)==getIndexOfMax(outData[idxDataPoint]))
                    ?nofCorrectClassifications+1
                    :nofCorrectClassifications;
        }
        System.out.print("nofCorrectClassifications:"+ nofCorrectClassifications+
                ", nofIncorrectClassifications:"+ (outData.length-nofCorrectClassifications));
    }

    private static float getIndexOfMax(float[] vec)
    {
        if ( vec == null || vec.length == 0 ) return -1; // null or empty

        int indexOfMax = 0;
        for ( int i = 1; i < vec.length; i++ )
        {
            if ( vec[i] > vec[indexOfMax] ) indexOfMax = i;
        }
        return indexOfMax;
    }

    private static float[] roundArrayItems(float[] array) {
        float[] roundedArray = new float[array.length];

        for (int i = 0; i < array.length; i++)
            roundedArray[i] = (float) (Math.round(array[i] * 100.0) / 100.0);

        return roundedArray;
    }

    public static float calcSingleLoss(float[] errorVec) {
        //Euclidean distance between the vectors y-y', error=y-y'
        //https://en.wikipedia.org/wiki/Euclidean_distance
        float sum = 0;
        for (float value : errorVec) {
            sum = (float) (sum+Math.pow(value,2));
        }
        return (float) Math.sqrt(sum);
    }

}
