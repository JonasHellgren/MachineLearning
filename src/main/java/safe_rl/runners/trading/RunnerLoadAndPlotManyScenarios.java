package safe_rl.runners.trading;

import common.other.RandUtils;
import common.plotters.parallel_coordinates.LineData;
import common.plotters.parallel_coordinates.NoiseToValuesInput;
import common.plotters.parallel_coordinates.NormalizeLineData;
import common.plotters.parallel_coordinates.ParallelCoordinatesChartCreator;
import org.knowm.xchart.SwingWrapper;
import safe_rl.other.scenario_creator.ScenarioExcelToMapConverter;
import safe_rl.other.scenario_creator.ScenarioParameters;
import safe_rl.persistance.trade_environment.PathAndFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static safe_rl.persistance.ElDataFinals.FILE_MANY_SCEN;
import static safe_rl.persistance.ElDataFinals.RES_PATH;

public class RunnerLoadAndPlotManyScenarios {

    public static void main(String[] args) {
        var scenMap=ScenarioExcelToMapConverter.readExcelToMap(PathAndFile.xlsxOf(RES_PATH, FILE_MANY_SCEN));
        scenMap.entrySet().forEach(e -> System.out.println(e));
        List<LineData> data = prepareData(scenMap);
        String[] inputNames = ScenarioParameters.names().toArray(new String[0]);
        String outputName = "Output";

        var dataNorm= NormalizeLineData.normalize(data);
        var dataNormAndNoisy= NoiseToValuesInput.addNoiseToValuesInput(dataNorm,0.1);
        var creator= ParallelCoordinatesChartCreator.builder()
                .inputNames(inputNames).outputName(outputName)
                .data(dataNormAndNoisy).build();
        new SwingWrapper<>(creator.create()).displayChart();

    }

    static Function<Double, Integer> categorize = (Double d) -> {
        if (d < 0.5) {
            return 1;
        } else if (d>10) {
            return 3;
        } else {
            return 2;
        }
    };

    private static List<LineData> prepareData(Map<ScenarioParameters, Double>  scenMap) {
        List<LineData> data = new ArrayList<>();
        scenMap.entrySet().forEach(e ->
        {
            ScenarioParameters parameters = e.getKey();
            data.add(LineData.ofCatFcn(convertToDoubleList(parameters.asListOfNumbers()),
                    e.getValue(),
                    categorize));
        });


        return data;
    }

    public static List<Double> convertToDoubleList(List<Number> numberList) {
        return numberList.stream()
                .map(Number::doubleValue)  // Convert each Number to Double
                .toList();
    }

}
