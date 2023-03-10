package mcts_training;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import java.util.Arrays;

public class TestXORMemoryOld {

    double DELTA = 0.1;
    int inputSize = 2, nofNeuronsHiddenLayer = 10, outputSize = 1, nofIterations = 10000;
    double learningRate = 0.1, maxError = 0.00001;
    double refNetworkOutput00 = 0, refNetworkOutput01 = 0.5, refNetworkOutput10 = 0.5, refNetworkOutput11 = 1;
    int nofIterationsForWamUp = 1, maxNofEpochs = 1000;

    MultiLayerPerceptron ann;
    MomentumBackpropagation learningRule;

    @Before
    public void init() {


        int nofIterationsForWamUp = 1, maxNofEpochs = 1000;

        ann = new MultiLayerPerceptron(
                TransferFunctionType.TANH,
                inputSize,
                nofNeuronsHiddenLayer,
                nofNeuronsHiddenLayer,
                outputSize);

        learningRule = new MomentumBackpropagation(); //(MomentumBackpropagation) ann.getLearningRule();
        learningRule.setLearningRate(learningRate);
        learningRule.setNeuralNetwork(ann);
        learningRule.setMaxIterations(nofIterationsForWamUp);
    }

    @Test
    public void aa() {
        DataSet trainingSet = getDataSet();
        ann.learn(trainingSet);  //needs warm up - else null pointer exception when calling doOneLearningIteration
        int epoch = 1;
        System.out.println("trainingSet = " + trainingSet);
        do {
            learningRule.doOneLearningIteration(trainingSet);
            printProgressSometimes(learningRule, epoch);
            epoch++;

        } while (learningRule.getTotalNetworkError() > maxError && epoch < maxNofEpochs);

        printOutPut();
        assertOutPut();

    }

    private void printProgressSometimes(MomentumBackpropagation learningRule, int epoch) {
        if (epoch % 100 == 0) {
            System.out.println("Epoch " + epoch + ", error=" + learningRule.getTotalNetworkError());
        }
    }

    private double getOutPut(double[] inputVec, int outputSize) {
        ann.setInput(inputVec);
        ann.calculate();
        double[] output = Arrays.copyOf(ann.getOutput(), outputSize);
        return output[0];
    }

    private DataSet getDataSet() {
        DataSet trainingSet = new DataSet(inputSize, outputSize);
        trainingSet.add(new DataSetRow(new double[]{0, 0}, new double[]{refNetworkOutput00}));
        trainingSet.add(new DataSetRow(new double[]{0, 1}, new double[]{refNetworkOutput01}));
        trainingSet.add(new DataSetRow(new double[]{1, 0}, new double[]{refNetworkOutput10}));
        trainingSet.add(new DataSetRow(new double[]{1, 1}, new double[]{refNetworkOutput11}));
        return trainingSet;
    }

    private void printOutPut() {
        double networkOutput00 = getOutPut(new double[]{0, 0}, outputSize);
        double networkOutput01 = getOutPut(new double[]{0, 1}, outputSize);
        double networkOutput10 = getOutPut(new double[]{1, 0}, outputSize);
        double networkOutput11 = getOutPut(new double[]{1, 1}, outputSize);

        System.out.println("networkOutput00 = " + networkOutput00);
        System.out.println("networkOutput01 = " + networkOutput01);
        System.out.println("networkOutput10 = " + networkOutput10);
        System.out.println("networkOutput11 = " + networkOutput11);
    }

    private void assertOutPut() {
        double networkOutput00 = getOutPut(new double[]{0, 0}, outputSize);
        double networkOutput01 = getOutPut(new double[]{0, 1}, outputSize);
        double networkOutput10 = getOutPut(new double[]{1, 0}, outputSize);
        double networkOutput11 = getOutPut(new double[]{1, 1}, outputSize);

        Assert.assertEquals(refNetworkOutput10, networkOutput10, DELTA);  //0
        Assert.assertEquals(refNetworkOutput01, networkOutput01, DELTA);  //1
        Assert.assertEquals(refNetworkOutput11, networkOutput11, DELTA);  //1
        Assert.assertEquals(refNetworkOutput00, networkOutput00, DELTA);  //0

    }


}
