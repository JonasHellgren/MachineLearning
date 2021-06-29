package udemycourse.nn2refined.twolayernetwork;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Random;

/***
 * The weights are updated according to:
 * dw_kj=-alpha*dE/da_k*da_k/dn_k*dn_k/dw_kj
 *      =-alpha*(-ta-a)*(derA(a)*(aj)
 *      =-alpha*delta*(aj)
 * alpha=learning rate, a=activation function output, ta=target activation, derA(a)=a*(1-a) for sigmoid, n=net sum
 * layer k, input j
 *
 * weights are defined as:  [w:s for in 0; w:s for in 1,...]
 *
 * because we have a single hidden layer, delta does not change when updating weights
 */

public class Layer {

    private final float[] outVec;
    private final float[] inVec;
    private final float[] weights;
    private final float[] dWeights;
    public int nofWeights;
    public int nofInputs;
    public int nofOutputs;
    private final Random random;
    public static final int NOF_BIAS_PER_LAYER=1;

    public Layer(int inputSize, int outputSize) {
        outVec = new float[outputSize];
        inVec = new float[inputSize + NOF_BIAS_PER_LAYER];
        nofOutputs=outVec.length;
        nofInputs=inVec.length;
        nofWeights=(NOF_BIAS_PER_LAYER + inputSize) * outputSize;
        weights = new float[nofWeights];
        dWeights = new float[nofWeights];
        this.random = new Random();
        initWeights();
    }

    public void initWeights() {
        for (int i = 0; i < nofWeights; i++) {
            weights[i] = (random.nextFloat() - 0.5f) * 4f;
        }
    }

    public float[] calcOut(float[] inputArray) {

        System.arraycopy(inputArray, 0, inVec, 0, inputArray.length);
        inVec[nofInputs - 1] = 1; // bias
        int offset = 0;
        for (int idxOutput = 0; idxOutput < nofOutputs; idxOutput++) {
            float[] netSum = calcNetSum(offset, idxOutput);
            outVec[idxOutput] = ActivationFunction.sigmoid(netSum[idxOutput]);
            offset += nofInputs;
        }
        return Arrays.copyOf(outVec, nofOutputs);
    }

    @NotNull
    private float[] calcNetSum(int offset, int idxOutput) {
        float[] netSum=new float[nofOutputs];
        for (int idxIn = 0; idxIn < nofInputs; idxIn++) {
            netSum[idxOutput] += weights[offset + idxIn] * inVec[idxIn];
        }
        return netSum;
    }

    public float[] train(float[] error, float learningRate, float momentum) {
        int offset = 0;
        float[] nextError = new float[nofInputs]; //the input error for next, more left, layer

        for (int idxOut = 0; idxOut < nofOutputs; idxOut++) {
            float delta = error[idxOut] * ActivationFunction.dSigmoid(outVec[idxOut]);
            for (int idxIn = 0; idxIn < nofInputs; idxIn++) {
                int idxWeight = offset + idxIn;
                nextError[idxIn] =  weights[idxWeight] * delta;
                float dw=inVec[idxIn] * delta * learningRate;
                updateWeightsAndDWeights(dw,  momentum, idxWeight);
            }
            offset += nofInputs;
        }
        return nextError;
    }

    private void updateWeightsAndDWeights(float dw, float momentum, int idxWeight) {
        weights[idxWeight] += dWeights[idxWeight] *momentum + dw;
        dWeights[idxWeight] = dw;
    }

}
