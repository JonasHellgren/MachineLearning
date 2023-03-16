package monte_carlo_tree_search.domains.energy_trading;

import lombok.SneakyThrows;
import monte_carlo_tree_search.classes.StateValueMemoryAbstract;
import monte_carlo_tree_search.interfaces.StateInterface;
import org.jetbrains.annotations.NotNull;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

public class EnergyTraderValueMemory <SSV,AV> extends StateValueMemoryAbstract<SSV,AV> {

    private static final int INPUT_SIZE = 2;
    private static final int OUTPUT_SIZE = 1;
    private static final int NOF_NEURONS_HIDDEN = INPUT_SIZE*2;
    private static final double LEARNING_RATE = 0.01;

    StateNormalizerEnergyTrader<SSV> normalizer;

    public EnergyTraderValueMemory() {
        this(-EnvironmentEnergyTrading.RETURN_MAX_ESTIMATE*2, EnvironmentEnergyTrading.RETURN_MAX_ESTIMATE*2);
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


    @NotNull
    public double[] getInputVec(SSV v) {
        StateNormalizerEnergyTrader.VariablesEnergyTradingDouble vNorm=normalizer.normalize(v);
        return new double[]{vNorm.time, vNorm.SoE};
    }

}
