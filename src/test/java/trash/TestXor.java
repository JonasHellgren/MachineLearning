package trash;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.ConnectionFactory;

import java.util.Arrays;

public class TestXor {

    public static final double DELTA = 0.1;
    public static final double LEARNING_RATE = 0.1;
    NeuralNetwork ann;
    int inputSize = 2;
    int outputSize = 1;
    DataSet trainingSet;

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

      //  ann=new MultiLayerPerceptron(2, 10, 1);  //Todo more/larger layers
      //  ann.getLearningRule().addListener(new LearningListener());
        trainingSet = new DataSet(2, 1);

        trainingSet = new DataSet(inputSize, outputSize);
        trainingSet.add(new DataSetRow(new double[]{0, 0}, new double[]{0}));
        trainingSet.add(new DataSetRow(new double[]{0, 1}, new double[]{1}));
        trainingSet.add(new DataSetRow(new double[]{1, 0}, new double[]{1}));
        trainingSet.add(new DataSetRow(new double[]{1, 1}, new double[]{0}));


    }

    @Test
    public void
    testTraining() {
        BackPropagation backPropagation = new BackPropagation();
        backPropagation.setMaxIterations(30000);
        backPropagation.setMaxError(0.0001);
        backPropagation.setLearningRate(LEARNING_RATE);
        ann.learn(trainingSet, backPropagation);
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
            ann.learn(trainingSet, backPropagation);

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

    static class LearningListener implements LearningEventListener {

        @Override
        public void handleLearningEvent(LearningEvent event) {
            BackPropagation bp = (BackPropagation) event.getSource();
            System.out.println("Current iteration: " + bp.getCurrentIteration());
            System.out.println("Error: " + bp.getTotalNetworkError());
        }

    }

}
