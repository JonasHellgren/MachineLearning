package monte_carlo_tree_search.domains.energy_trading;

import lombok.SneakyThrows;
import monte_carlo_tree_search.models_and_support_classes.ValueMemoryNetworkAbstract;
import monte_carlo_tree_search.interfaces.StateInterface;
import org.jetbrains.annotations.NotNull;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

public class EnergyTraderValueMemoryNetwork<SSV,AV> extends ValueMemoryNetworkAbstract<SSV,AV> {

    private static final int INPUT_SIZE = 2;
    private static final int OUTPUT_SIZE = 1;
    private static final int NOF_NEURONS_HIDDEN = INPUT_SIZE*2;  //2
    private static final double LEARNING_RATE = 0.01;

    StateNormalizerEnergyTrader<SSV> normalizer;

    public EnergyTraderValueMemoryNetwork() {
        this(-EnvironmentEnergyTrading.RETURN_MAX_ESTIMATE*1, EnvironmentEnergyTrading.RETURN_MAX_ESTIMATE*1); //2
    }
    public EnergyTraderValueMemoryNetwork(double minOut, double maxOut) {
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

    @NotNull
    public double[] getInputVec(SSV v) {
        StateNormalizerEnergyTrader.VariablesEnergyTradingDouble vNorm=normalizer.normalize(v);
        return new double[]{vNorm.time, vNorm.SoE};
    }

}
