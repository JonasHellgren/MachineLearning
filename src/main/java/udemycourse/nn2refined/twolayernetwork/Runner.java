package udemycourse.nn2refined.twolayernetwork;

public class Runner {

    public static void main(String[] args) {

        float[][] inData = new float[][]{
                new float[]{0, 0},
                new float[]{0, 1},
                new float[]{1, 0},
                new float[]{1, 1}
        };

        float[][] outData = new float[][]{
                new float[]{0},
                new float[]{1},
                new float[]{1},
                new float[]{0}
        };

        NeuralNetwork network = new NeuralNetwork(
                Parameters.NOF_LAYERS,
                Parameters.NOF_INPUTS,
                Parameters.NOF_NEURONS_HIDDENLAYER,
                Parameters.NOF_NEURONS_OUTPUTLAYER);

        long startTime = System.currentTimeMillis();  //starting time, long <=> minimum value of 0
        for (int iteration = 0; iteration < Parameters.NOF_ITERATIONS; iteration++) {

            for (int i = 0; i < outData.length; i++) {
                network.train(inData[i], outData[i],
                        Parameters.LEARNING_RATE, Parameters.MOMENTUM);
            }

            if (iteration % 1000 == 0)
                showProgress(inData, outData, network, iteration);
        }
        System.out.printf("Time used (ms): %d\n", (System.currentTimeMillis()-startTime)/1);
    }

    private static void showProgress(float[][] inData,
                                     float[][] outData,
                                     NeuralNetwork network,
                                     int iteration) {
        System.out.printf("Iteration: %d\n", iteration);
        for (int idxDataPoint = 0; idxDataPoint < outData.length; idxDataPoint++) {
            float[] inVec = inData[idxDataPoint];
            System.out.printf("%.1f, %.1f --> %.3f\n", inVec[0], inVec[1], network.calcOutput(inVec)[0]);
        }
    }
}
