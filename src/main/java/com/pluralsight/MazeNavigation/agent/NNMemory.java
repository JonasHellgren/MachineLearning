package com.pluralsight.MazeNavigation.agent;

import com.pluralsight.MazeNavigation.enums.Action;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.ArrayList;
import java.util.HashSet;


public class NNMemory implements Memory {

    private static final int SEED = 234;
    private static final double LEARNING_RATE = 0.5;
    public static final int INPUT_NEURONS = 3;
    public static final int HIDDEN_NEURONS = 10;
    public static final int OUTPUT_NEURONS = 1;

    public MultiLayerNetwork net;
    public HashSet<Transition> repBuff;
    public HashSet<Transition> miniBatch;
    public static final int RBLEN = 10;   //replay buffer length
    public static final int MBLEN = 5;    //mini batch length
    public ArrayList rbKeys;  //random integer number set to point out replay buffer items

    public NNMemory() {
        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
        builder.seed(SEED);
        //builder.biasInit(0);
        builder.weightInit(WeightInit.XAVIER);
        builder.miniBatch(false);
        builder.updater(new Sgd(LEARNING_RATE));
        NeuralNetConfiguration.ListBuilder listBuilder = builder.list();

        // Hidden Layer
        DenseLayer.Builder hiddenLayerBuilder = new DenseLayer.Builder();
        hiddenLayerBuilder.nIn(INPUT_NEURONS);
        hiddenLayerBuilder.nOut(HIDDEN_NEURONS);
        hiddenLayerBuilder.activation(Activation.TANH);
        //hiddenLayerBuilder.weightInit(new UniformDistribution(0, 1));
        listBuilder.layer(0, hiddenLayerBuilder.build());

        // Output Layer
        OutputLayer.Builder outputLayerBuilder = new OutputLayer.Builder(LossFunctions.LossFunction.SQUARED_LOSS);
        outputLayerBuilder.nIn(HIDDEN_NEURONS);
        outputLayerBuilder.nOut(OUTPUT_NEURONS);
        outputLayerBuilder.activation(Activation.TANH);
        //outputLayerBuilder.weightInit(new UniformDistribution(0, 1));
        listBuilder.layer(1, outputLayerBuilder.build());
        MultiLayerConfiguration conf = listBuilder.build();
        net = new MultiLayerNetwork(conf);
        net.init();    System.out.println("Creating NN memory");

        //create repBuff, miniBatch and mbkeys
        repBuff=new HashSet<>();
        miniBatch=new HashSet<>();
        rbKeys = new ArrayList(); for (int i = 0; i < RBLEN; i++) rbKeys.add(i);
    }

    @Override
    public double readMem(Pos2d s, Action a) {
        //TBD
        INDArray oneinput  = Nd4j.create(new double[1][INPUT_NEURONS]);
        oneinput.putRow(0, Nd4j.create(new double[] {s.getX(),s.getY(),a.val}));
        INDArray out = net.output(oneinput);
        return out.getDouble(0, 0);
    }

    @Override
    public void clearMem()  { };

}
