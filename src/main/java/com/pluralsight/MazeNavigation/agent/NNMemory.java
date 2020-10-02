package com.pluralsight.MazeNavigation.agent;

import com.pluralsight.MazeNavigation.enums.Action;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;



public class NNMemory implements Memory {

    private static final int SEED = 234;
    private static final double LEARNING_RATE = 0.03;   //learning rate
    public static final int INPUT_NEURONS = 2;  //nof input neurons
    public static final int HIDDEN_NEURONS = 10;  //nof neurons in hidden layers
    public static final int OUTPUT_NEURONS = 4; //nof output neurons

    public MultiLayerNetwork net;

    public static final int RBLEN = 100;   //replay buffer length
    public static final int MBLEN = 10;    //mini batch length
    public static final int NEPOCHS=1; //nof epochs for fitting NN to batch


    public NNMemory() {
        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
        builder.seed(SEED);
        //builder.biasInit(0); builder.weightInit(WeightInit.ZERO);
        builder.weightInit(WeightInit.XAVIER);
        builder.miniBatch(false);
        builder.updater(new Sgd(LEARNING_RATE));
        builder.l2(0.0001);
        NeuralNetConfiguration.ListBuilder listBuilder = builder.list();

        // Hidden Layer
        DenseLayer.Builder hidden1LayerBuilder = new DenseLayer.Builder();
        hidden1LayerBuilder.nIn(INPUT_NEURONS);
        hidden1LayerBuilder.nOut(HIDDEN_NEURONS);
        hidden1LayerBuilder.activation(Activation.TANH);
        //hiddenLayerBuilder.weightInit(new UniformDistribution(0, 1));
        listBuilder.layer(0, hidden1LayerBuilder.build());

        // Hidden Layer
        DenseLayer.Builder hidden2LayerBuilder = new DenseLayer.Builder();
        hidden2LayerBuilder.nIn(HIDDEN_NEURONS);
        hidden2LayerBuilder.nOut(HIDDEN_NEURONS);
        hidden2LayerBuilder.activation(Activation.TANH);
        listBuilder.layer(1, hidden2LayerBuilder.build());

        // Output Layer
        OutputLayer.Builder outputLayerBuilder = new OutputLayer.Builder(LossFunctions.LossFunction.SQUARED_LOSS);
        outputLayerBuilder.nIn(HIDDEN_NEURONS);
        outputLayerBuilder.nOut(OUTPUT_NEURONS);
        outputLayerBuilder.activation(Activation.IDENTITY);
        //outputLayerBuilder.weightInit(new UniformDistribution(0, 1));
        listBuilder.layer(2, outputLayerBuilder.build());
        MultiLayerConfiguration conf = listBuilder.build();
        net = new MultiLayerNetwork(conf);
        net.init();    System.out.println("Creating NN memory");

    }

    @Override
    public double readMem(Pos2d s, Action a) {
        //TBD
        INDArray oneinput  = Nd4j.create(new double[1][INPUT_NEURONS]);
        oneinput.putRow(0, Nd4j.create(new double[] {s.getX(),s.getY()}));
        INDArray out = net.output(oneinput);
        return out.getDouble(0, a.val);
    }

    @Override
    public void clearMem()  { };



}
