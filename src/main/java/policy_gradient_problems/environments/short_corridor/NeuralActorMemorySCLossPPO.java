package policy_gradient_problems.environments.short_corridor;

import common_dl4j.LossPPO;
import common_dl4j.NetSettings;
import org.nd4j.linalg.activations.Activation;
import policy_gradient_problems.environments.cart_pole.NeuralActorMemorySC;
import java.util.List;

/**
 Wrapping NeuralActorMemorySC due to facts in link below
 https://www.geeksforgeeks.org/favoring-composition-over-inheritance-in-java-with-examples/
 */

public class NeuralActorMemorySCLossPPO {

    NeuralActorMemorySC memory;

    public static NeuralActorMemorySCLossPPO newDefault() {
        return new NeuralActorMemorySCLossPPO(getDefaultNetSettings());
    }

    public NeuralActorMemorySCLossPPO(NetSettings netSettings) {
        this.memory =new NeuralActorMemorySC(netSettings);
    }

    private static NetSettings getDefaultNetSettings() {
        return NetSettings.builder()
                .nInput(NeuralActorMemorySC.NOF_INPUTS).nHiddenLayers(1).nHidden(5)
                .nOutput(NeuralActorMemorySC.NOF_OUTPUTS)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.SOFTMAX)
                .learningRate(1e-4).momentum(0.9).seed(1234)
                .lossFunction(LossPPO.newWithEpsilonPPO(0.1))
                .sizeBatch(10).isNofFitsAbsolute(false).relativeNofFitsPerBatch(3.0)  //4 10
                .build();
    }

    public double[] getOutValue(double[] doubles) {
        return memory.getOutValue(doubles);
    }

    public double getError() {
        return memory.getError();
    }

    public void fit(List<List<Double>> inList, List<List<Double>> outList) {
        memory.fit(inList,outList);
    }

}
