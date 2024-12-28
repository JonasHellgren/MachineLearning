package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.environment.EnvironmentLunar;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.lunar_lander.domain.trainer.TrainerDependencies;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import org.hellgren.plotters.plotting_2d.HeatMapChartCreator;
import org.hellgren.utilities.file_io.PathAndFile;
import org.hellgren.utilities.list_arrays.List2ArrayConverter;
import org.knowm.xchart.HeatMapChart;
import org.knowm.xchart.SwingWrapper;

import java.util.function.Function;

import static lunar.RunnerHelper.CHART_DIR;
import static org.hellgren.plotters.chart_saver.ChartSaver.saveHeatMapChart;

@AllArgsConstructor
public class PlotterAgent {

    @Builder
    public record Settings(
            int nY,
            int nSpd,
            int nDigitsAxisLabels,
            String valueFormat,
            int plotWidth,
            int plotHeight,
            String titleForce,
            String titleAcc,
            String titleValue
    ) {

        //default values
        public static SettingsBuilder builder() {
            return new SettingsBuilder().
                    nDigitsAxisLabels(1).valueFormat("%.1f").
                    plotWidth(500).plotHeight(300)
                    .titleForce("Force").titleAcc("Acc").titleValue("Value");
        }

        public static Settings fromAgentProperties(AgentParameters ap) {
            return Settings.builder()
                    .nY(ap.nKernelsY()).nSpd(ap.nKernelsSpeed())
                    .build();
        }
    }

    TrainerDependencies dependencies;
    Settings settings;

    public static PlotterAgent of(TrainerDependencies trainerDependencies, Settings settings) {
        return new PlotterAgent(trainerDependencies, settings);
    }

    public void plotAndSaveAll() {
        plotAndSavExpectedForce();
        plotAndSavExpectedAcceleration();
        plotAndSavValue();
    }


    public void plotAndSavExpectedForce() {
        Function<StateLunar,Double> func = s -> dependencies.agent().readActor(s).mean();
        var chart= showData(settings.titleForce(), getData(func));
        saveHeatMapChart(chart, PathAndFile.ofPng(CHART_DIR, settings.titleForce()));
    }

    public void plotAndSavExpectedAcceleration() {
        var env=(EnvironmentLunar) dependencies.environment();
        Function<StateLunar,Double> func = s ->
                env.acceleration(dependencies.agent().readActor(s).mean());
        var chart=showData(settings.titleAcc, getData(func));
        saveHeatMapChart(chart, PathAndFile.ofPng(CHART_DIR, settings.titleAcc()));
    }

    public void plotAndSavValue() {
        Function<StateLunar,Double> func = s -> dependencies.agent().readCritic(s);
        var chart=showData(settings.titleValue, getData(func));
        saveHeatMapChart(chart, PathAndFile.ofPng(CHART_DIR, settings.titleValue()));
    }

    private double[][] getData(Function<StateLunar,Double> func) {
        var yList = dependencies.environment().getProperties().ySpace(settings.nY());
        var spdList = dependencies.environment().getProperties().spdSpace(settings.nSpd());
        System.out.println("spdList = " + spdList);

        double[][] data = new double[yList.size()][spdList.size()];
        for (double y : yList) {
            for (double spd : spdList) {
                int yi = yList.indexOf(y);
                int spdi = spdList.indexOf(spd);
                data[yi][spdi] = func.apply(StateLunar.of(y, spd));
            }
        }
        return data;
    }


    @SneakyThrows
    private HeatMapChart showData(String title, double[][] data) {
        var settingsMap = HeatMapChartCreator.Settings.defaultBuilder()
                .xAxisLabel("Speed").yAxisLabel("Height")
                .title(title).showDataValues(false).build();
        var yList = dependencies.environment().getProperties().ySpace(settings.nY());
        var spdList = dependencies.environment().getProperties().spdSpace(settings.nSpd());
        var creator = HeatMapChartCreator.of(
                settingsMap,
                data,
                List2ArrayConverter.convertListToDoubleArr(spdList),
                List2ArrayConverter.convertListToDoubleArr(yList));
        HeatMapChart chart = creator.create();
        new SwingWrapper<>(chart).displayChart();
        return chart;
    }



}
