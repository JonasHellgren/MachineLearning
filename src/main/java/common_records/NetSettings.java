package common_records;

import lombok.Builder;

@Builder
public record NetSettings(
        double learningRate,
        double momentum,
        int nofFitsPerEpoch,
        int nHidden,
        int seed) {

  public static NetSettings newDefault() {
      return NetSettings.builder()
              .learningRate(1e-1).momentum(0.9)
              .nofFitsPerEpoch(10).nHidden(2)
              .seed(12345)
              .build();
  }
}