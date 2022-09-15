package q;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.ConnectionFactory;

import java.util.Arrays;

public class TestXor {

    public static final double DELTA = 0.001;
    public static final double LEARNING_RATE = 0.0001;
    NeuralNetwork ann;
    int inputSize = 2;
    int outputSize = 1;
    DataSet ds;

    double[] networkOutput10;
    double[] networkOutput01;
    double[] networkOutput11;
    double[] networkOutput00;

    @Before
    public void init() {
        Layer inputLayer = new Layer();
        inputLayer.addNeuron(new Neuron());
        inputLayer.addNeuron(new Neuron());

        Layer hiddenLayerOne = new Layer();
        hiddenLayerOne.addNeuron(new Neuron());
        hiddenLayerOne.addNeuron(new Neuron());
        hiddenLayerOne.addNeuron(new Neuron());
        hiddenLayerOne.addNeuron(new Neuron());

        Layer hiddenLayerTwo = new Layer();
        hiddenLayerTwo.addNeuron(new Neuron());
        hiddenLayerTwo.addNeuron(new Neuron());
        hiddenLayerTwo.addNeuron(new Neuron());
        hiddenLayerTwo.addNeuron(new Neuron());

        Layer outputLayer = new Layer();
        outputLayer.addNeuron(new Neuron());

        ann = new NeuralNetwork();
        ann.addLayer(0, inputLayer);
        ann.addLayer(1, hiddenLayerOne);
        ConnectionFactory.fullConnect(ann.getLayerAt(0), ann.getLayerAt(1));
        ann.addLayer(2, hiddenLayerTwo);
        ConnectionFactory.fullConnect(ann.getLayerAt(1), ann.getLayerAt(2));
        ann.addLayer(3, outputLayer);
        ConnectionFactory.fullConnect(ann.getLayerAt(2), ann.getLayerAt(3));
        ConnectionFactory.fullConnect(ann.getLayerAt(0),
                ann.getLayerAt(ann.getLayersCount() - 1), false);
        ann.setInputNeurons(inputLayer.getNeurons());
        ann.setOutputNeurons(outputLayer.getNeurons());

        ds = new DataSet(inputSize, outputSize);
        DataSetRow rOne
                = new DataSetRow(new double[]{0, 1}, new double[]{1});
        ds.addRow(rOne);
        DataSetRow rTwo
                = new DataSetRow(new double[]{1, 1}, new double[]{0});
        ds.addRow(rTwo);
        DataSetRow rThree
                = new DataSetRow(new double[]{0, 0}, new double[]{0});
        ds.addRow(rThree);
        DataSetRow rFour
                = new DataSetRow(new double[]{1, 0}, new double[]{1});
        ds.addRow(rFour);
    }

    @Test
    public void
    testTraining() {
        BackPropagation backPropagation = new BackPropagation();
        backPropagation.setMaxIterations(100);
        ann.learn(ds, backPropagation);
        calcOutputs();
        printOutPuts();
        assertOutPuts();
    }


    @Test
    public void
    testBatchTraining() {
        BackPropagation backPropagation = new BackPropagation();
        backPropagation.setLearningRate(LEARNING_RATE);
        backPropagation.setMaxIterations(1);
        int nofEpochs = 10000;
        for (int i = 0; i < nofEpochs; i++) {
            ann.learn(ds, backPropagation);

        }
        calcOutputs();
        printOutPuts();
        assertOutPuts();
    }

    private void printOutPuts() {
        System.out.println("networkOutput10 = " + Arrays.toString(networkOutput10));
        System.out.println("networkOutput01 = " + Arrays.toString(networkOutput01));
        System.out.println("networkOutput11 = " + Arrays.toString(networkOutput11));
        System.out.println("networkOutput00 = " + Arrays.toString(networkOutput00));
    }

    private void assertOutPuts() {
        Assert.assertEquals(1 ^ 0, networkOutput10[0], DELTA);
        Assert.assertEquals(0 ^ 1, networkOutput01[0], DELTA);
        Assert.assertEquals(1 ^ 1, networkOutput11[0], DELTA);
        Assert.assertEquals(0 ^ 0, networkOutput00[0], DELTA);
    }

    private void calcOutputs() {
        ann.setInput(1, 0);
        ann.calculate();
        networkOutput10 = Arrays.copyOf(ann.getOutput(), outputSize);
        ann.setInput(0, 1);
        ann.calculate();
        networkOutput01 = Arrays.copyOf(ann.getOutput(), outputSize);
        ann.setInput(1, 1);
        ann.calculate();
        networkOutput11 = Arrays.copyOf(ann.getOutput(), outputSize);
        ann.setInput(0, 0);
        ann.calculate();
        networkOutput00 = Arrays.copyOf(ann.getOutput(), outputSize);
    }

}
