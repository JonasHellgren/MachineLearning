package common.plotters.parallel_coordinates;

import common.other.RandUtils;

import java.util.List;
import java.util.stream.Collectors;

public class NoiseToValuesInput {

    private NoiseToValuesInput() {
    }

    public static List<LineData> addNoiseToValuesInput(List<LineData> dataList, double noiseLevel) {
        return dataList.stream()
                .map(lineData -> {
                    List<Double> noisyValues = lineData.valuesInput().stream()
                            .map(value -> value + generateNoise(noiseLevel))
                            .collect(Collectors.toList());
                    return new LineData(noisyValues, lineData.valueOutput(), lineData.category());
                })
                .collect(Collectors.toList());
    }

    private static double generateNoise(double noiseLevel) {
        return RandUtils.getRandomDouble(-noiseLevel,noiseLevel);  // Generate noise in range [-noiseLevel, noiseLevel]
    }
}
