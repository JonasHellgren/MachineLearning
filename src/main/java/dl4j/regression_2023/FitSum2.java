package dl4j.regression_2023;

import java.util.List;


/**
 * https://medium.com/@AlexanderObregon/getting-started-with-machine-learning-in-java-using-deeplearning4j-3a5dc47dbbf4
 */

public class FitSum2 {

    public static void main(String[] args) {
        int nofEpochs = 100;

        var dataGenerator=SumDataGenerator.builder()
                .minValue(0).maxValue(10).nSamplesPerEpoch(10).build();

        var neuralMemory  = MemoryNeuralSum.newDefault();

        for (int i = 0; i < nofEpochs; i++) {
            var trainData=dataGenerator.getTrainingData();
            neuralMemory.train(trainData.getFirst(),trainData.getSecond());
        }

        var trainData=dataGenerator.getTrainingData();
        for (List<Double> inData:trainData.getFirst()) {
           var outValue=neuralMemory.getOutValue(inData);
            System.out.println("inData = " + inData+", outValue = "+outValue);
        }

    }





}
