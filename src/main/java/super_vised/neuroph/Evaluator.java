package super_vised.neuroph;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Evaluator {

    // for evaluating classification result
    int total, correct, incorrect;

    // if output is greater then this value it is considered as malign
    float delta;

    NeuralNetwork neuralNet;
    DataSet testSet;

    public Evaluator(NeuralNetwork neuralNet, DataSet testSet, float delta) {
        this.neuralNet=neuralNet;
        this.testSet=testSet;
        this.delta=delta;
    }

    public  void eval() {
        System.out.println("********************** TEST RESULT **********************");
        for (DataSetRow testSetRow : testSet.getRows()) {
            neuralNet.setInput(testSetRow.getInput());
            neuralNet.calculate();
            double[] networkOutput = neuralNet.getOutput();
            double[] desiredOutput = testSetRow.getDesiredOutput();
            countPredictions(networkOutput[0], desiredOutput[0]);
        }

        System.out.println("Total cases: " + total + ". ");
        System.out.println("Correctly predicted cases: " + correct);
        System.out.println("Incorrectly predicted cases: " + incorrect);
        double percentTotal = (correct / (double)total) * 100;
        System.out.println("Predicted correctly: " + formatDecimalNumber(percentTotal) + "%. ");
    }


    public void countPredictions(double prediction, double target) {
        if (Math.abs(prediction - target)< delta) {
            correct++;
        } else {
            incorrect++;
        }
        total++;
    }

    public String formatDecimalNumber(double number) {
        return new BigDecimal(number).setScale(4, RoundingMode.HALF_UP).toString();
    }

}
