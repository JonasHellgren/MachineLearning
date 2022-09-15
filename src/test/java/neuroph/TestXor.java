package neuroph;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;


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
        ann=new MultiLayerPerceptron(2, 10, 1);  //Todo more/larger layers
        ann.getLearningRule().addListener(new LearningListener());
        trainingSet = new DataSet(2, 1);
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

    private void printOutPuts() {
        System.out.println("networkOutput10 = " + Arrays.toString(networkOutput10));
        System.out.println("networkOutput01 = " + Arrays.toString(networkOutput01));
        System.out.println("networkOutput11 = " + Arrays.toString(networkOutput11));
        System.out.println("networkOutput00 = " + Arrays.toString(networkOutput00));
    }

    private void assertOutPuts() {
        Assert.assertEquals(1 ^ 0, networkOutput10[0], DELTA);  //0
        Assert.assertEquals(0 ^ 1, networkOutput01[0], DELTA);  //1
        Assert.assertEquals(1 ^ 1, networkOutput11[0], DELTA);  //1
        Assert.assertEquals(0 ^ 0, networkOutput00[0], DELTA);  //0
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
