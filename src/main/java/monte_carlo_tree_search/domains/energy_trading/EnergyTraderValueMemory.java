package monte_carlo_tree_search.domains.energy_trading;

import common.ListUtils;
import lombok.SneakyThrows;
import monte_carlo_tree_search.classes.StateValueMemoryAbstract;
import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.domains.cart_pole.EnvironmentCartPole;
import monte_carlo_tree_search.domains.cart_pole.StateNormalizerCartPole;
import monte_carlo_tree_search.interfaces.StateInterface;
import monte_carlo_tree_search.network_training.Experience;
import org.jetbrains.annotations.NotNull;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

import java.util.ArrayList;
import java.util.List;

public class EnergyTraderValueMemory <SSV,AV> extends StateValueMemoryAbstract<SSV,AV> {

    private static final int INPUT_SIZE = 2;
    private static final int OUTPUT_SIZE = 1;
    private static final int NOF_NEURONS_HIDDEN = INPUT_SIZE;
    private static final double LEARNING_RATE = 0.01;

    StateNormalizerEnergyTrader<SSV> normalizer;

    public EnergyTraderValueMemory() {
        this(-EnvironmentEnergyTrading.RETURN_MAX_ESTIMATE, EnvironmentEnergyTrading.RETURN_MAX_ESTIMATE);
    }
    public EnergyTraderValueMemory(double minOut, double maxOut) {
        neuralNetwork = new MultiLayerPerceptron(
                TransferFunctionType.TANH,  //happens to be adequate for this environment
                INPUT_SIZE,
                NOF_NEURONS_HIDDEN,
                NOF_NEURONS_HIDDEN,
                OUTPUT_SIZE);
        super.settings= NetSettings.builder()
                .inputSize(INPUT_SIZE)
                .outPutSize(OUTPUT_SIZE)
                .nofNeuronsHidden(NOF_NEURONS_HIDDEN)
                .learningRate(LEARNING_RATE)
                .build();
        super.createLearningRule(neuralNetwork,settings);
        normalizer = new StateNormalizerEnergyTrader<>();
        createOutScalers(minOut, maxOut);
        isWarmedUp=false;
    }

    @SneakyThrows
    @Override
    public void write(StateInterface<SSV> state, double value) {
        throw new NoSuchMethodException("Not defined/needed - use learn instead");
    }

    @Override
    public double getAverageValueError(List<Experience<SSV, AV>> experienceList) {  //todo - to abstract
        List<Double> errors=new ArrayList<>();
        for (Experience<SSV, AV> e : experienceList) {
            double expectedValue= e.value;
            double memoryValue=getNetworkOutputValue(getInputVec(e.stateVariables));
            errors.add(Math.abs(expectedValue-memoryValue));
        }
        return ListUtils.findAverage(errors).orElseThrow();
    }

    @NotNull
    public double[] getInputVec(SSV v) {
        StateNormalizerEnergyTrader.VariablesEnergyTradingDouble vNorm=normalizer.normalize(v);
        return new double[]{vNorm.time, vNorm.SoE};
    }




}
