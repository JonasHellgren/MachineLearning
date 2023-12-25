package common_dl4j;

import common.MyFunctions;
import lombok.Builder;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import org.nd4j.linalg.lossfunctions.LossFunctions;


public record NetSettings(
        Double learningRate,
        Double momentum,
        Integer nofFitsPerEpoch,
        Integer nHiddenLayers,
        Integer nInput,
        Integer nHidden,
        Integer nOutput,
        Activation activInLayer,
        Activation activHiddenLayer,
        Activation activOutLayer,
        ILossFunction lossFunction,
        Integer seed) {

    public static final int N_HIDDEN = 10;
    public static final ILossFunction LOSS_FCN = LossFunctions.LossFunction.MSE.getILossFunction();

    @Builder
    public NetSettings(Double learningRate,
                       Double momentum,
                       Integer nofFitsPerEpoch,
                       Integer nHiddenLayers,
                       Integer nInput,
                       Integer nHidden,
                       Integer nOutput,
                       Activation activInLayer,
                       Activation activHiddenLayer,
                       Activation activOutLayer,
                       ILossFunction lossFunction,
                       Integer seed) {
        this.learningRate = MyFunctions.defaultIfNullDouble.apply(learningRate,1e-1);
        this.momentum = MyFunctions.defaultIfNullDouble.apply(momentum,0.9);
        this.nofFitsPerEpoch = MyFunctions.defaultIfNullInteger.apply(nofFitsPerEpoch,10);
        this.nHiddenLayers = MyFunctions.defaultIfNullInteger.apply(nHiddenLayers, 1);
        this.nInput = MyFunctions.defaultIfNullInteger.apply(nInput, 1);
        this.nHidden = MyFunctions.defaultIfNullInteger.apply(nHidden, N_HIDDEN);
        this.nOutput = MyFunctions.defaultIfNullInteger.apply(nOutput, 1);
        this.activInLayer = (Activation) MyFunctions.defaultIfNullObject.apply(activInLayer, Activation.RELU);
        this.activHiddenLayer = (Activation) MyFunctions.defaultIfNullObject.apply(activHiddenLayer, Activation.RELU);
        this.activOutLayer = (Activation) MyFunctions.defaultIfNullObject.apply(activOutLayer, Activation.IDENTITY);
        this.lossFunction = (ILossFunction) MyFunctions.defaultIfNullObject.apply(lossFunction,LOSS_FCN);
        this.seed = MyFunctions.defaultIfNullInteger.apply(seed,12345);
    }

    public static NetSettings newDefault() {
      return NetSettings.builder().build();
  }
}