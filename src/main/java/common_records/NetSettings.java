package common_records;

import common.MyFunctions;
import lombok.Builder;


public record NetSettings(
        Double learningRate,
        Double momentum,
        Integer nofFitsPerEpoch,
        Integer nHidden0,
        Integer nHidden1,
        Integer nHidden2,
        Integer seed) {

    public static final int N_HIDDEN = 10;

    @Builder
    public NetSettings(Double learningRate,
                       Double momentum,
                       Integer nofFitsPerEpoch,
                       Integer nHidden0,
                       Integer nHidden1,
                       Integer nHidden2,
                       Integer seed) {
        this.learningRate = MyFunctions.defaultIfNullDouble.apply(learningRate,1e-1);
        this.momentum = MyFunctions.defaultIfNullDouble.apply(momentum,0.9);
        this.nofFitsPerEpoch = MyFunctions.defaultIfNullInteger.apply(nofFitsPerEpoch,10);
        this.nHidden0 = MyFunctions.defaultIfNullInteger.apply(nHidden0, N_HIDDEN);
        this.nHidden1 = MyFunctions.defaultIfNullInteger.apply(nHidden1,N_HIDDEN);
        this.nHidden2 = MyFunctions.defaultIfNullInteger.apply(nHidden2,N_HIDDEN);
        this.seed = MyFunctions.defaultIfNullInteger.apply(seed,12345);
    }

    public static NetSettings newDefault() {
      return NetSettings.builder().build();
  }
}