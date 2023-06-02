package multi_step_temp_diff.models;

import lombok.Builder;

@Builder
public class NetSettings {

    @Builder.Default
    public int outPutSize = 1;
    public int inputSize,  nofNeuronsHidden;
    public double minOut, maxOut;
    @Builder.Default
    public double netOutMin = 0d;
    @Builder.Default
    public double netOutMax = 1d;
    @Builder.Default
    public double learningRate=0.05;
}
