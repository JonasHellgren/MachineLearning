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
import org.jetbrains.annotations.NotNull;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class IrisClassifier {

    private static final Logger logger = LoggerFactory.getLogger(IrisClassifier.class);

    private static final int NOF_CLASSES = 3;
    private static final int NOF_FEATURES = 4;
    private static final int  NOF_NEURONS_HIDDEN=5;
    private static final int BATCH_SIZE = 150;
    private static final int NOF_ITERATIONS = 5000;
    private static final int NOF_ITERATIONS_BETWEENOUTPUTS =1000;
    private static final double LEARNING_RATE =0.01;
    private static final String DATA_FILE_NAME="iris.txt";  //located at folder resources


    public static void main(String[] args) throws IOException, InterruptedException {

        DataSet allData = getData(DATA_FILE_NAME);
        SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(0.8);
        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();

        MultiLayerNetwork networkModel = getNetworkModel();
        networkModel.setListeners(new ScoreIterationListener(NOF_ITERATIONS_BETWEENOUTPUTS));

        trainNetwork(trainingData, networkModel);

        Evaluation eval = new Evaluation(NOF_CLASSES);
        INDArray output = networkModel.output(testData.getFeatures());
        eval.eval(testData.getLabels(), output);
        System.out.println(eval.stats());
    }

    private static void trainNetwork(DataSet trainingData, MultiLayerNetwork networkModel) {
        for (int i = 0; i < NOF_ITERATIONS ; i++)
            networkModel.fit(trainingData);
    }

    @NotNull
    private static DataSet getData(String filename) throws IOException, InterruptedException {
        DataSet allData;
        try (RecordReader recordReader = new CSVRecordReader(0, ',')) {
            recordReader.initialize(new FileSplit(new ClassPathResource(filename).getFile()));
            DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, BATCH_SIZE, NOF_FEATURES, NOF_CLASSES);
            allData = iterator.next();
        }

        allData.shuffle();
        DataNormalization normalizer = new NormalizerStandardize();
        normalizer.fit(allData);
        normalizer.transform(allData);

        logSampleData(allData, normalizer);

        return allData;
    }

    private static void logSampleData(DataSet allData, DataNormalization normalizer) {
        logger.info("Data created");
        DataSet sampleData= allData.sample(5);
        logger.info(sampleData.toString());
        logger.info("After normalizer");
        normalizer.fit(sampleData);
        normalizer.transform(sampleData);
        logger.info(sampleData.toString());
    }

    @NotNull
    private static MultiLayerNetwork getNetworkModel() {
        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()

                .l2(0.0001)
                .updater(new Sgd(LEARNING_RATE))
                .miniBatch(false)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(NOF_FEATURES).nOut(NOF_NEURONS_HIDDEN)
                        .activation(Activation.TANH)
                        .weightInit(new UniformDistribution(0, 1))
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(NOF_NEURONS_HIDDEN).nOut(NOF_NEURONS_HIDDEN)
                        .activation(Activation.TANH)
                        .weightInit(new UniformDistribution(0, 1))
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(NOF_NEURONS_HIDDEN).nOut(NOF_CLASSES).build())
                .backpropType(BackpropType.Standard)
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(configuration);
        model.init();
        return model;
    }



}
