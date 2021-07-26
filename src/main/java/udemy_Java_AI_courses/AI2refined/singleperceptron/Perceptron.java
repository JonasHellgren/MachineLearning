package udemycourse.nn2refined.singleperceptron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Random;

/***
 * A set of input features are fed to the perceptron. A training example is {input features, desired output}.
 * A training set is made up of multiple training examples. Every input to the perceptron is associated with a weight.
 * During training, weights are tuned and data in the training set is considered as fixed.
 * Assume a training example is given, then the weight update for input i is:
 *
 *  w_i <-- w_i + LEARNING_RATE*in_i*(desOut-calcOut)
 *  where calcOut=activFunction(sum(w*input features))
 *
 *  A weight can be seen as a parameters that says if the output shall "react" on the input or not
 */

public class Perceptron {
    private static final Logger logger = LoggerFactory.getLogger(Perceptron.class);

    private final float[] weights;
    private final float[][] inputMatrix;   //[datapoint,input feature]
    private final float[] desiredOutputs;  //one output per datapoint
    private final int nofInputFeatures;
    private final int nofTrainingExamples;
    private final Random random=new Random();

    public Perceptron(float[][] inputMatrix, float[] desiredOutputs) {
        this.inputMatrix = inputMatrix;
        this.desiredOutputs = desiredOutputs;
        this.nofInputFeatures = inputMatrix[0].length;
        this.nofTrainingExamples = desiredOutputs.length;
        this.weights = new float[nofInputFeatures];
        initializeWeights();
    }

    private void initializeWeights() {
        for(int i = 0; i< nofInputFeatures; ++i)
            weights[i] = (random.nextFloat() - 0.5f)*4;
    }

    public void train(float learningRate,int nofIterationsMax) {

        float totalError = 1;
        int nofIterations=0;
        while ( totalError != 0 && nofIterations< nofIterationsMax) {
            totalError = 0;
            for(int idxExample = 0; idxExample< nofTrainingExamples; idxExample++) {
                float error = desiredOutputs[idxExample]-calculateOutput(inputMatrix[idxExample]);
                totalError += Math.abs(error);
                updateWeights(learningRate, inputMatrix[idxExample], error);
            }
            showLogMessage(totalError);
            nofIterations++;
        }

        logger.info("The nof iterations is: " + nofIterations);
    }

    private void updateWeights(float learningRate, float[] inVector, float error) {
        for(int idxInput = 0; idxInput< nofInputFeatures; idxInput++)
            weights[idxInput] = weights[idxInput] + learningRate * inVector[idxInput] * error;
    }

    private void showLogMessage(float totalError) {
        logger.info("Keep on training the network, error is: " + totalError);
        StringBuilder sb = new StringBuilder(10);
        sb.append("Weights:");
        for(int idxInput = 0; idxInput< nofInputFeatures; idxInput++) {
            String textAfterWeight=(idxInput+1==nofInputFeatures)?"":", ";
            sb.append(weights[idxInput]).append(textAfterWeight);
        }
        logger.info(sb.toString());
    }


    public float calculateOutput(float[] inputVec) {
        float sum = 0f;
        for(int idxInput=0;idxInput<nofInputFeatures;++idxInput)
            sum = sum + weights[idxInput] * inputVec[idxInput];
        return ActivFunction.stepFunction(sum);
    }
}
