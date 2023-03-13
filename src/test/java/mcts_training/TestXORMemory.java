package mcts_training;

import lombok.extern.java.Log;
import monte_carlo_tree_search.network_training.XORMemory;
import org.junit.Before;
import org.junit.Test;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.learning.MomentumBackpropagation;

import java.util.Arrays;

@Log
public class TestXORMemory {

    int inputSize = 2, outputSize = 1;
    double refNetworkOutput00 = 0, refNetworkOutput01 = 0.5, refNetworkOutput10 = 0.5, refNetworkOutput11 = 1;
    XORMemory<Integer,Integer> memory;

    @Before
    public void init() {
        memory = new XORMemory<>();
    }

    @Test
    public void a() {
        DataSet trainingSet = getDataSet();
          memory.getAnn().learn(trainingSet);  //needs warm up - else null pointer exception when calling doOneLearningIteration

        for (int i = 0; i < 1000 ; i++) {
            memory.getLearningRule().doOneLearningIteration(trainingSet);
            printProgressSometimes(memory.getLearningRule(), i);
        }

        System.out.println("getOutPut() = " + getOutPut(new double[]{0, 0}));
        System.out.println("getOutPut() = " + getOutPut(new double[]{1, 1}));


    }


    private void printProgressSometimes(MomentumBackpropagation learningRule, int epoch) {
        if (epoch % 100 == 0) {
            System.out.println("Epoch " + epoch + ", error=" + learningRule.getTotalNetworkError());
        }
    }

    private double getOutPut(double[] inputVec) {
        memory.getAnn().setInput(inputVec);
        memory.getAnn().calculate();
        double[] output = Arrays.copyOf(memory.getAnn().getOutput(), outputSize);
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


}
