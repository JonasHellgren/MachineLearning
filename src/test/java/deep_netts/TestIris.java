package deep_netts;

import lombok.extern.java.Log;
import org.junit.Test;
import org.neuroph.util.data.norm.MaxNormalizer;

import javax.visrec.ml.data.DataSet;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * https://www.deepnetts.com/blog/deep-learning-in-java-getting-started-with-deep-netts.html
 */

@Log
public class TestIris {

        @Test
        public void givenIris() throws IOException {

            /*
            TabularDataSet<> dataSet = DataSets.readCsv("datasets/iris-flowers.csv", 4, 3, true, ",");
            DataSet<?>[] trainTest = DataSets.trainTestSplit(dataSet, 0.65);

            // normalize data using max normalization
            MaxNormalizer norm = new MaxNormalizer(trainTest.getTrainingeSet());
            norm.normalize(trainTest.getTrainingeSet());
            norm.normalize(trainTest.getTestSet());

            // create instance of feed forward neural network using builder
            FeedForwardNetwork neuralNet = FeedForwardNetwork.builder()
                    .addInputLayer(4)
                    .addFullyConnectedLayer(8, ActivationType.TANH)
                    .addOutputLayer(3, ActivationType.SOFTMAX)
                    .lossFunction(LossType.CROSS_ENTROPY)
                    .randomSeed(123).
                    build();

            // create and configure instanceof backpropagation trainer
            BackpropagationTrainer trainer = neuralNet.getTrainer();
            trainer.setMaxError(0.03f)
                    .setMaxEpochs(1000000)
                    .setLearningRate(0.1f);

            trainer.train(trainTest.getTrainingeSet());

            ClassifierEvaluationResult ceResult = Evaluators.evaluateClassifier(neuralNet, trainTest.getTestSet());

            log.info("Classification performance measure" + System.lineSeparator());
            log.info("ceResult");

            // shutdown the thread pool
            DeepNetts.shutdown();

*/

        }

}
