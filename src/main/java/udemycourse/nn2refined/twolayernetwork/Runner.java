package udemycourse.nn2refined.twolayernetwork;

import udemycourse.nn2refined.twolayernetwork.datasets.CircleClassifierData;
import udemycourse.nn2refined.twolayernetwork.datasets.XORData;
import udemycourse.nn2refined.twolayernetwork.model.NeuralNetwork;
import udemycourse.nn2refined.twolayernetwork.model.Parameters;

import java.util.Arrays;

public class Runner {

    public static void main(String[] args) {

        //XORData data=new XORData();
        CircleClassifierData data = new CircleClassifierData();

        NeuralNetwork network = new NeuralNetwork(
                data.NOF_LAYERS,
                data.NOF_INPUTS,
                data.NOF_NEURONS_HIDDENLAYER,
                data.NOF_NEURONS_OUTPUTLAYER);

        long startTime = System.currentTimeMillis();  //starting time, long <=> minimum value of 0
        for (int iteration = 0; iteration < Parameters.NOF_ITERATIONS; iteration++) {

            for (int i = 0; i < data.outData.length; i++) {
                network.train(data.inData[i], data.outData[i],
                        Parameters.LEARNING_RATE, Parameters.MOMENTUM);
            }

            if (iteration % 1000 == 0)
                showProgress(data.inData, data.outData, network, iteration);
        }
        System.out.printf("Time used (ms): %d\n", (System.currentTimeMillis() - startTime) / 1);

        showNetworkRespons(data.inData, data.outData, network);
    }

    private static void showProgress(float[][] inData,
                                     float[][] outData,
                                     NeuralNetwork network,
                                     int iteration) {

        float errorSum=0;
        for (int idxDataPoint = 0; idxDataPoint < outData.length; idxDataPoint++) {
            float[] inVec = inData[idxDataPoint];
            float[] calculatedOutput = network.calcOutput(inVec);
            float[] errorOut=network.calcErrorVecOutput(outData[idxDataPoint], calculatedOutput);
            errorSum=errorSum+findSumWithoutUsingStream(errorOut);
        }
        System.out.println("Iteration:"+ iteration+", Error:"+errorSum);
    }

    private static void showNetworkRespons(float[][] inData,
                                     float[][] outData,
                                     NeuralNetwork network) {

        for (int idxDataPoint = 0; idxDataPoint < outData.length; idxDataPoint++) {
            float[] inVec = inData[idxDataPoint];
            System.out.printf("in:"+ Arrays.toString(inVec)+"--> ");
            float[] resVec = network.calcOutput(inVec);
            System.out.printf(Arrays.toString(roundArrayItems(resVec)));
            System.out.println();
        }
    }


    private static float[] roundArrayItems(float[] array) {
        float[] roundedArray = new float[array.length];

        for (int i = 0; i < array.length; i++)
            roundedArray[i] = (float) (Math.round(array[i] * 100.0) / 100.0);

        return roundedArray;
    }

    public static float findSumWithoutUsingStream(float[] array) {
        float sum = 0;
        for (float value : array) {
            sum = sum+Math.abs(value);
        }
        return sum;
    }

}