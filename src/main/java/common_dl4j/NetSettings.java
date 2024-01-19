package common_dl4j;

import common.MyFunctions;
import lombok.Builder;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import static common.MyFunctions.defaultIfNullDouble;
import static common.MyFunctions.defaultIfNullInteger;


public record NetSettings(
        Double learningRate,
        Double momentum,
        Double l2Value,
        Integer nofFitsPerEpoch,  //todo ta bort
        WeightInit weightInit,
        Integer nHiddenLayers,
        Integer nInput,
        Integer nHidden,
        Integer nOutput,
        Activation activInLayer,
        Activation activHiddenLayer,
        Activation activOutLayer,
        ILossFunction lossFunction,
        Integer seed,
        Double relativeNofFitsPerBatch,
        Integer absNoFit,
        Boolean isNofFitsAbsolute,
        Integer sizeBatch) {

    public static final int N_HIDDEN = 10;
    public static final ILossFunction LOSS_FCN = LossFunctions.LossFunction.MSE.getILossFunction();
    public static final double RELATIVE_NOF_FITS_PER_EPOCH = 0.5;
    public static final int SIZE_BATCH = 16;
    public static final boolean IS_NFITS_ABS = false;

    @Builder
    public NetSettings(Double learningRate,
                       Double momentum,
                       Double l2Value,
                       Integer nofFitsPerEpoch,  //todo veck
                       WeightInit weightInit,
                       Integer nHiddenLayers,
                       Integer nInput,
                       Integer nHidden,
                       Integer nOutput,
                       Activation activInLayer,
                       Activation activHiddenLayer,
                       Activation activOutLayer,
                       ILossFunction lossFunction,
                       Integer seed,
                       Double relativeNofFitsPerBatch,
                       Integer absNoFit,
                       Boolean isNofFitsAbsolute,
                       Integer sizeBatch) {
        this.learningRate = MyFunctions.defaultIfNullDouble.apply(learningRate,1e-1);
        this.momentum = MyFunctions.defaultIfNullDouble.apply(momentum,0.9);
        this.l2Value = MyFunctions.defaultIfNullDouble.apply(l2Value,0d);
        this.nofFitsPerEpoch = MyFunctions.defaultIfNullInteger.apply(nofFitsPerEpoch, 1);
        this.weightInit = (WeightInit) MyFunctions.defaultIfNullObject.apply(weightInit,WeightInit.XAVIER);
        this.nHiddenLayers = MyFunctions.defaultIfNullInteger.apply(nHiddenLayers, 1);
        this.nInput = MyFunctions.defaultIfNullInteger.apply(nInput, 1);
        this.nHidden = MyFunctions.defaultIfNullInteger.apply(nHidden, N_HIDDEN);
        this.nOutput = MyFunctions.defaultIfNullInteger.apply(nOutput, 1);
        this.activInLayer = (Activation) MyFunctions.defaultIfNullObject.apply(activInLayer, Activation.RELU);
        this.activHiddenLayer = (Activation) MyFunctions.defaultIfNullObject.apply(activHiddenLayer, Activation.RELU);
        this.activOutLayer = (Activation) MyFunctions.defaultIfNullObject.apply(activOutLayer, Activation.IDENTITY);
        this.lossFunction = (ILossFunction) MyFunctions.defaultIfNullObject.apply(lossFunction,LOSS_FCN);
        this.seed = MyFunctions.defaultIfNullInteger.apply(seed,12345);
        this.relativeNofFitsPerBatch = defaultIfNullDouble.apply(relativeNofFitsPerBatch, RELATIVE_NOF_FITS_PER_EPOCH);
        this.isNofFitsAbsolute = (Boolean) MyFunctions.defaultIfNullObject.apply(isNofFitsAbsolute, IS_NFITS_ABS);
        this.absNoFit=MyFunctions.defaultIfNullInteger.apply(absNoFit,1);
        this.sizeBatch = defaultIfNullInteger.apply(sizeBatch, SIZE_BATCH);
    }

    public static NetSettings newDefault() {
      return NetSettings.builder().build();
  }

     public int nofFits(int nofPoints) {
        return  isNofFitsAbsolute
                ? absNoFit
                : (int) Math.max(1, (relativeNofFitsPerBatch() * nofPoints));
    }



}