package common_records;

import common.MyFunctions;
import lombok.Builder;


public record NetSettings(
        Double learningRate,
        Double momentum,
        Integer nofFitsPerEpoch,
        Integer nHidden,
        Integer seed) {

    public static final int N_HIDDEN = 10;

    @Builder
    public NetSettings(Double learningRate,
                       Double momentum,
                       Integer nofFitsPerEpoch,
                       Integer nHidden,
                       Integer seed) {
        this.learningRate = MyFunctions.defaultIfNullDouble.apply(learningRate,1e-1);
        this.momentum = MyFunctions.defaultIfNullDouble.apply(momentum,0.9);
        this.nofFitsPerEpoch = MyFunctions.defaultIfNullInteger.apply(nofFitsPerEpoch,10);
        this.nHidden = MyFunctions.defaultIfNullInteger.apply(nHidden, N_HIDDEN);
        this.seed = MyFunctions.defaultIfNullInteger.apply(seed,12345);
    }

    public static NetSettings newDefault() {
      return NetSettings.builder().build();
  }
}