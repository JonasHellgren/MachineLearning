package simpleneuralnetworks;

/* *****************************************************************************

 ******************************************************************************/


import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.evaluation.classification.Evaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This basic example shows how to manually create a DataSet and train it to an basic Network.
 * <p>
 * The network consists in 2 input-neurons, 1 hidden-layer with 4 hidden-neurons, and 2 output-neurons.
 * <p>
 * I choose 2 output neurons, (the first fires for false, the second fires for
 * true) because the Evaluation class needs one neuron per classification.
 * <p>
 * +---------+---------+---------------+--------------+
 * | Input 1 | Input 2 | Label 1(XNOR) | Label 2(XOR) |
 * +---------+---------+---------------+--------------+
 * |    0    |    0    |       1       |       0      |
 * +---------+---------+---------------+--------------+
 * |    1    |    0    |       0       |       1      |
 * +---------+---------+---------------+--------------+
 * |    0    |    1    |       0       |       1      |
 * +---------+---------+---------------+--------------+
 * |    1    |    1    |       1       |       0      |
 * +---------+---------+---------------+--------------+
 *
 * @author Peter Großmann
 * @author Dariusz Zbyrad
 */
public class XORclassif {

    private static final Logger log = LoggerFactory.getLogger(XORclassif.class);

    public static void main(String[] args) {

        int seed = 1234;        // number used to initialize a pseudorandom number generator.
        int nEpochs = 2000;    // number of training epochs

        log.info("Data preparation...");
        // list off input values, 4 training samples with data for 2 input-neurons each
        INDArray input = Nd4j.zeros(4, 2);
        // correspondending list with expected output values
        INDArray labels = Nd4j.zeros(4, 2);
        // create first dataset, when first input=0 and second input=0
        input.putScalar(new int[]{0, 0}, 0);    input.putScalar(new int[]{0, 1}, 0);
        labels.putScalar(new int[]{0, 0}, 1);    labels.putScalar(new int[]{0, 1}, 0);
        // when first input=1 and second input=0
        input.putScalar(new int[]{1, 0}, 1);     input.putScalar(new int[]{1, 1}, 0);
        labels.putScalar(new int[]{1, 0}, 0);    labels.putScalar(new int[]{1, 1}, 1);
        // same as above
        input.putScalar(new int[]{2, 0}, 0);     input.putScalar(new int[]{2, 1}, 1);
        labels.putScalar(new int[]{2, 0}, 0);    labels.putScalar(new int[]{2, 1}, 1);
        // when both inputs fire, xor is false again - the first output should fire
        input.putScalar(new int[]{3, 0}, 1);     input.putScalar(new int[]{3, 1}, 1);
        labels.putScalar(new int[]{3, 0}, 1);    labels.putScalar(new int[]{3, 1}, 0);
        // create dataset object
        DataSet ds = new DataSet(input, labels);

        log.info("Network configuration and training...");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .updater(new Sgd(0.5))
                .seed(seed)
                .biasInit(0) // init the bias with 0 - empirical value, too
                .miniBatch(false)
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(2)
                        .nOut(4)
                        .activation(Activation.SIGMOID)
                        .weightInit(new UniformDistribution(0, 1)) // random initialize weights with values between 0 and 1
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(2)
                        .activation(Activation.SOFTMAX)
                        .weightInit(new UniformDistribution(0, 1))
                        .build())
                .build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        // add an listener which outputs the error every 100 parameter updates
        net.setListeners(new ScoreIterationListener(100));
        // Print the number of parameters in the network (and for each layer)
        System.out.println(net.summary());
        // here the actual learning takes place
        for( int i=0; i < nEpochs; i++ ) {         net.fit(ds);     }
        //print features, i.e. input
        System.out.println("Input"); System.out.println(ds.getFeatures());
        // create output for every training sample
        INDArray output = net.output(ds.getFeatures());
        System.out.println("NN output"); System.out.println(output);
        // let Evaluation prints stats how often the right output had the highest value
        Evaluation eval = new Evaluation();   eval.eval(ds.getLabels(), output);
        System.out.println(eval.stats());
    }
}



/*
public class XOR {

    public static void main(String[] args) {

        System.out.println("Test");

    }
}
*/