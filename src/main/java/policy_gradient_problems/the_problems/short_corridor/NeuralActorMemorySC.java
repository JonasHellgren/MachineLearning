package policy_gradient_problems.the_problems.short_corridor;

import common.ListUtils;
import common_dl4j.*;
import org.apache.commons.math3.util.Pair;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import java.util.List;
import static common.ListUtils.findMax;
import static common.ListUtils.findMin;

public class NeuralActorMemorySC {

    static int NOF_INPUTS = 3, NOF_OUTPUTS = EnvironmentSC.NOF_ACTIONS;

    MultiLayerNetwork net;
    NormalizerMinMaxScaler normalizerIn;

    public static NeuralActorMemorySC newDefault() {
        return new NeuralActorMemorySC(getDefaultNetSettings());
    }

    public NeuralActorMemorySC(NetSettings netSettings) {
        this.net= MultiLayerNetworkCreator.create(netSettings);
        net.init();
        this.normalizerIn = createNormalizerIn();
    }


    public void fit(List<Double> in, List<Double> out, int  nofFitsPerEpoch) {
        INDArray indArray = transformToIndArray(in);
        net.fit(indArray, Nd4j.create(out));
    }

    public double[] getOutValue(double[] inData) {
        INDArray indArray = transformToIndArray(ListUtils.arrayPrimitiveDoublesToList(inData));
       return net.output(indArray).toDoubleVector();
    }

    public double getError() {
        return net.gradientAndScore().getSecond();
    }


    private INDArray transformToIndArray(List<Double> in) {
        List<Double> onHot = ListUtils.createListWithEqualElementValues(NOF_INPUTS, 0d);
        int pos = in.get(0).intValue();
        onHot.set(pos, 1d);
        INDArray indArray = Nd4j.create(onHot);
        normalizerIn.transform(indArray);
        indArray= indArray.reshape(1,indArray.length());  // reshape it to a row matrix of size 1×n
        return indArray;
    }

    private INDArray transformToIndArrayOld(List<Double> in) {
        INDArray indArray = Nd4j.create(in);
        normalizerIn.transform(indArray);
        indArray= indArray.reshape(1,indArray.length());  // reshape it to a row matrix of size 1×n
        return indArray;
    }

   private static NetSettings getDefaultNetSettings() {
        return NetSettings.builder()
                .nInput(NOF_INPUTS).nHiddenLayers(1).nHidden(20).nOutput(NOF_OUTPUTS)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.SOFTMAX)
                .nofFitsPerEpoch(1).learningRate(1e-3).momentum(0.95).seed(1234)
                .lossFunction(CustomPolicyGradientLoss.newDefault())
                .build();
    }


    private static NormalizerMinMaxScaler createNormalizerIn() {
        List<Double> os = EnvironmentSC.SET_OBSERVABLE_STATES.stream().map(n -> n.doubleValue()).toList();
        var inMinMax = List.of(Pair.create(findMin(os).orElseThrow(),findMax(os).orElseThrow()));
        return Dl4JUtil.createNormalizer(inMinMax, Pair.create(-1d,1d));  //0,1 gives worse performance
    }


}
