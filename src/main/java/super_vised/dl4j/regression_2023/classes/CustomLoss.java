package super_vised.dl4j.regression_2023.classes;

import com.codepoetics.protonpack.functions.TriFunction;
import common.dl4j.NumericalGradCalculator;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.nd4j.common.primitives.Pair;

/***
 * https://github.com/breandan/deep-learning-samples/blob/master/dl4j-examples/src/main/java/org/deeplearning4j/examples/misc/lossfunctions/CustomLossExample.java

 This example illustrates how to implement a custom loss function that can then be applied to training your neural net
 All loss functions have to implement the ILossFunction interface
 The loss function implemented here is:
 L = (y - y_hat)^2 +  |y - y_hat|
 y is the true label, y_hat is the predicted output

 */

public class CustomLoss implements ILossFunction {

    boolean IS_NUM_GRAD=true;

    /*
    Remains the same for all loss functions
    Compute Score computes the average loss function across many datapoints.
    The loss for a single datapoint is summed over all output features.
     */
    @Override
    public double computeScore(INDArray labels, INDArray preOutput, IActivation activationFn, INDArray mask, boolean average) {
        INDArray scoreArr = scoreArray(labels, preOutput, activationFn, mask);
        double score = scoreArr.sumNumber().doubleValue();
        score = getAverageScoreIfRequested(average, scoreArr, score);
        return score;
    }

    /*
    Remains the same for all loss functions
    Compute Score computes the loss function for many datapoints.
    The loss for a single datapoint is the loss summed over all output features.
    Returns an array that is #of samples x size of the output feature
     */
    @Override
    public INDArray computeScoreArray(INDArray labels, INDArray preOutput, IActivation activationFn, INDArray mask) {
        INDArray scoreArr = scoreArray(labels, preOutput, activationFn, mask);
        return scoreArr.sum(1);
    }


    /*
    Needs modification depending on your loss function
        scoreArray calculates the loss for a single data point or in other words a batch size of one
        It returns an array the shape and size of the output of the neural net.
        Each element in the array is the loss function applied to the prediction and it's true value
        scoreArray takes in:
        true labels - labels
        the input to the final/output layer of the neural network - preOutput,
        the activation function on the final layer of the neural network - activationFn
        the mask - (if there is a) mask associated with the label

        This is the output of the neural network, the y_hat in the notation above
        To obtain y_hat: pre-output is transformed by the activation function to give the output of the neural network
        The score is calculated as the sum of (y-y_hat)^2 + |y - y_hat|
     */
    private static INDArray scoreArray(INDArray labels, INDArray preOutput, IActivation activationFn, INDArray mask) {
        INDArray output = activationFn.getActivation(preOutput.dup(), true);
        INDArray yMinusYHat = labels.sub(output);
        INDArray absYMinusyHat = Transforms.abs(yMinusYHat);
        INDArray yMinusyHatSqr = yMinusYHat.mul(yMinusYHat);
        INDArray scoreArr = yMinusyHatSqr.add(absYMinusyHat);   //(y-y_hat)^2+|y - y_hat|
        maskIfRequired(mask, scoreArr);
        return scoreArr;
    }

    /*
    Needs modification depending on your loss function
        Compute the gradient wrt to the preout (which is the input to the final layer of the neural net)
        Use the chain rule
        In this case L = (y - yhat)^2 + |y - yhat|
        dL/dyhat = -2*(y-yhat) - sign(y-yhat), sign of y - yhat = +1 if y-yhat>= 0 else -1
        dyhat/dpreout = d(Activation(preout))/dpreout = Activation'(preout)
        dL/dpreout = dL/dyhat * dyhat/dpreout
    */

    @Override
    public INDArray computeGradient(INDArray yHat, INDArray preOutput, IActivation activationFn, INDArray mask) {
        INDArray dLdPreOut = IS_NUM_GRAD
                ? getNumericGrad(yHat, preOutput, activationFn)
                : getAnalyticGrad(yHat, preOutput, activationFn);

        maskIfRequired(mask, dLdPreOut);
        return dLdPreOut;
    }

    private static INDArray getAnalyticGrad(INDArray yHat, INDArray preOutput, IActivation activationFn) {
        INDArray y = activationFn.getActivation(preOutput.dup(), true);
        yHat = yHat.castTo(preOutput.dataType());   //else: Failed to execute op mmul exception
        INDArray yMinusyHat = yHat.sub(y);
        INDArray dldyhat = yMinusyHat.mul(-2).sub(Transforms.sign(yMinusyHat));
        return activationFn.backprop(preOutput.dup(), dldyhat).getFirst();
    }

    static final float EPS = 1e-5f;

    private static INDArray getNumericGrad(INDArray yHat, INDArray preOutput, IActivation activationFn) {
        TriFunction<Pair<INDArray, INDArray>, IActivation, INDArray, Double> scoreFcn =
                (p, a, m) -> scoreArray(p.getFirst(), p.getSecond(), a, m).sumNumber().doubleValue();
        var gradCalculator = new NumericalGradCalculator(EPS, scoreFcn);
        return gradCalculator.getGrad(yHat, preOutput, activationFn, null);
    }

    @Override
    public Pair<Double, INDArray> computeGradientAndScore(INDArray labels, INDArray preOutput, IActivation activationFn, INDArray mask, boolean average) {
        return Pair.of(
                computeScore(labels, preOutput, activationFn, mask, average),
                computeGradient(labels, preOutput, activationFn, mask));
    }

    @Override
    public String name() {
        return toString();
    }

    @Override
    public String toString() {
        return "CustomLoss()";
    }

    private static double getAverageScoreIfRequested(boolean average, INDArray scoreArr, double score) {
        if (average) {
            score /= scoreArr.size(0);
        }
        return score;
    }


    private static void maskIfRequired(INDArray mask, INDArray dLdPreOut) {
        if (mask != null) {
            dLdPreOut.muliColumnVector(mask);
        }
    }

}
