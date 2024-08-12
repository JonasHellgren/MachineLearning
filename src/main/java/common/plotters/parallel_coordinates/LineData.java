package common.plotters.parallel_coordinates;

import lombok.Builder;

import java.util.List;
import java.util.function.Function;

@Builder
public  record LineData(
        List<Double> valuesInput,
        Double valueOutput,
        Integer category
) {
    public static LineData of(List<Double> valuesInput,
                              Double valueOutput,
                              Integer category)  {
        return LineData.builder()
                .valuesInput(valuesInput).valueOutput(valueOutput).category(category)
                .build();
    }

    public static LineData ofCatFcn(List<Double> valuesInput,
                                    Double valueOutput,
                                    Function<Double, Integer> fcn)  {
        return LineData.builder()
                .valuesInput(valuesInput).valueOutput(valueOutput)
                .category(fcn.apply(valueOutput))
                .build();
    }

}