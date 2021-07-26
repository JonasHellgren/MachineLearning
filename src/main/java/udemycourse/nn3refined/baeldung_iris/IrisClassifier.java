package udemycourse.nn3refined.baeldung_iris;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.common.io.ClassPathResource;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.IOException;

public class IrisClassifier {
    private static final int CLASSES_COUNT = 3;
    private static final int FEATURES_COUNT = 4;
    private static final int NOF_ITERATIONS = 5000;
    private static final int NOF_ITERATIONS_BETWEENOUTPUTS =100;

    public static void main(String[] args) throws IOException, InterruptedException {

        DataSet allData;
        try (RecordReader recordReader = new CSVRecordReader(0, ',')) {
            recordReader.initialize(new FileSplit(new ClassPathResource("iris.txt").getFile()));

            DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, 150, FEATURES_COUNT, CLASSES_COUNT);
            allData = iterator.next();
        }

        allData.shuffle();

        DataNormalization normalizer = new NormalizerStandardize();
        normalizer.fit(allData);
        normalizer.transform(allData);

        SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(0.65);
        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();

        double learningRate = 0.01;
        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .activation(Activation.TANH)
                .weightInit(new UniformDistribution(0, 1))
                .l2(0.0001)
                .updater(new Sgd(learningRate))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(FEATURES_COUNT).nOut(3)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(3).nOut(3)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(3).nOut(CLASSES_COUNT).build())
                .backpropType(BackpropType.Standard)
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(configuration);
        model.init();
        model.setListeners(new ScoreIterationListener(NOF_ITERATIONS_BETWEENOUTPUTS));
        for (int i = 0; i < NOF_ITERATIONS ; i++)
            model.fit(trainingData);

        INDArray output = model.output(testData.getFeatures());

        Evaluation eval = new Evaluation(CLASSES_COUNT);
        eval.eval(testData.getLabels(), output);
        System.out.println(eval.stats());

    }

}
