package common.linear_regression_batch_fitting;


import org.apache.commons.math3.linear.*;

import java.util.Arrays;

public class MiniBatchGradientDescent {

    public static final double W0 = 1;
    public static final double W1 = 0;
    public static final double B = 0;
    public static final int N_POINTS = 10;

    public static void main(String[] args) {
        // Set the random seed for reproducibility
        java.util.Random rand = new java.util.Random(42);

        // Generate synthetic data
        RealMatrix X = new Array2DRowRealMatrix(N_POINTS, 2);
        RealVector y = new ArrayRealVector(N_POINTS);
        RealVector trueWeights = new ArrayRealVector(new double[]{W0, W1});
        double b = B; // true bias

        for (int i = 0; i < N_POINTS; i++) {
            double[] features = {1 * rand.nextDouble()-0.5, 0 * rand.nextDouble()};
            double target = trueWeights.dotProduct(new ArrayRealVector(features)) + b + 0 * rand.nextGaussian();
            X.setRow(i, features);
            y.setEntry(i, target);

            System.out.println("i = " + i);
            System.out.println("X.getRow(i) = " + Arrays.toString(X.getRow(i)));
            System.out.println("y.getEntry(i) = " + y.getEntry(i));
        }


        // Parameters
        int epochs = 1000;
        double learningRate = 1e-1;
        int batchSize = 3;

        // Initialize weights and bias
        //RealVector w = new ArrayRealVector(2, 0.0);
        RealVector w = new ArrayRealVector(new double[]{3*W0, W1});
        double bias = 0.0;


        // Mini-Batch Gradient Descent
        for (int epoch = 0; epoch < epochs; epoch++) {

            // Random shuffle indices
            int[] indices = rand.ints(0, N_POINTS).distinct().limit(batchSize).toArray();

            // Manual batch creation
            RealMatrix XBatch = new Array2DRowRealMatrix(batchSize, 2);
            RealVector yBatch = new ArrayRealVector(batchSize);
            int batchIndex = 0;
            for (int j = 0; j < batchSize; j++) {
                XBatch.setRow(batchIndex, X.getRow(indices[j]));
                yBatch.setEntry(batchIndex, y.getEntry(indices[j]));
                batchIndex++;
            }
            // Compute predictions
            RealVector predictions = XBatch.operate(w).mapAdd(bias);

            // Compute errors
            RealVector errors = predictions.subtract(yBatch);

            // Compute gradients
            RealVector gradientsW = XBatch.transpose().operate(errors).mapMultiply(2.0 / batchSize);
            double gradientBias = Arrays.stream(errors.toArray()).sum()*2/ batchSize;  // Proper bias gradient calculation

            w=w.subtract(gradientsW.mapMultiply(learningRate));
            bias -= learningRate * gradientBias;

        }

        // Output final weights and bias
        System.out.println("Final weights: " + w);
        System.out.println("Final bias: " + bias);
    }


}
