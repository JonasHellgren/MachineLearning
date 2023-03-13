package monte_carlo_tree_search.network_training;

import lombok.Getter;
import monte_carlo_tree_search.generic_interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import java.util.List;

@Getter
public class XORMemory <SSV> implements NetworkMemoryInterface<SSV> {

    double DELTA = 0.1;
    int inputSize = 2, nofNeuronsHiddenLayer = 10, outputSize = 1, nofIterations = 10000;
    double learningRate = 0.1, maxError = 0.00001;
    double refNetworkOutput00 = 0, refNetworkOutput01 = 0.5, refNetworkOutput10 = 0.5, refNetworkOutput11 = 1;
    int nofIterationsForWamUp = 1, maxNofEpochs = 1000;

    MultiLayerPerceptron ann;
    MomentumBackpropagation learningRule;

    public XORMemory() {
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

        System.out.println("learningRule = " + learningRule);

    }

    @Override
    public void write(StateInterface<SSV> state, double value) {
    }


    @Override
    public double read(StateInterface<SSV> state) {
        return 0;
    }

    @Override
    public void save(String fileName) {

    }

    @Override
    public void load(String fileName) {

    }

    @Override
    public void learn(List<Experience<SSV, Integer>> miniBatch) {

    }

    @Override
    public MomentumBackpropagation getLearningRule() {
        return learningRule;
    }

    @Override
    public double getAverageValueError(List<Experience<SSV, Integer>> experiences) {
        return 0;
    }

    @Override
    public void createOutScalers(double minOut, double maxOut) {

    }
}
