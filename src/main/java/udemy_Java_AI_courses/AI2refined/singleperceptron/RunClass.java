package udemy_Java_AI_courses.AI2refined.singleperceptron;

import udemy_Java_AI_courses.AI2refined.singleperceptron.model.Perceptron;

import java.util.Arrays;

public class RunClass {

    public static void main(String[] args) {

        final float LEARNING_RATE=0.1f;
        final int NOFITERATIONS_MAX=1000;

        float[][] input = { {0,0}, {0,1}, {1,0}, {1,1} };
        float[] output = {0,1,1,1};  //OR

       Perceptron perceptron = new Perceptron(input, output);
       perceptron.train(LEARNING_RATE,NOFITERATIONS_MAX);

        System.out.println("Neural network training is ready! Predictions: ");

        for(int idxExample = 0; idxExample< output.length; idxExample++) {
            float[] inVec = input[idxExample];
            System.out.println("in:" + Arrays.toString(inVec) +
                    ", calcOut:" + perceptron.calculateOutput(inVec)+
                    ", givenOut:" + output[idxExample]);
        }
    }
}
