package udemy_Java_AI_courses.AI2refined.twolayernetwork;

import udemy_Java_AI_courses.AI2refined.twolayernetwork.datasets.XORData;
import udemy_Java_AI_courses.AI2refined.twolayernetwork.model.NeuralNetwork;


public class Runner {

    public static void main(String[] args) {

        final int NOF_ITERATIONS_BETWEEN_PRINTOUTS=1000;

        //Used data is selected below by uncommenting/commenting relevant row
        XORData data=new XORData();
        //CircleClassifierData data = new CircleClassifierData();
        //IrisData data = new IrisData();
        //NumberImagesData data = new NumberImagesData();


        NeuralNetwork network = new NeuralNetwork(
                data.NOF_LAYERS,
                data.NOF_INPUTS,
                data.NOF_NEURONS_HIDDENLAYER,
                data.NOF_NEURONS_OUTPUTLAYER);

        long startTime = System.currentTimeMillis();  //starting time, long <=> minimum value of 0
        for (int iteration = 0; iteration < data.NOF_ITERATIONS; iteration++) {

            for (int i = 0; i < data.outData.length; i++) {
                network.train(
                        data.inData[i],
                        data.outData[i],
                        data.LEARNING_RATE,
                        data.MOMENTUM);
            }

            if (iteration % NOF_ITERATIONS_BETWEEN_PRINTOUTS == 0)
                network.showProgress(data.inData, data.outData,  iteration);
        }
        System.out.printf("Time used (s): %d\n", (System.currentTimeMillis() - startTime)/1000);

        network.showNetworkResponse(data.inData, data.outData);

    }

}