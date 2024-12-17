package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.environment.EnvironmentLunar;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.lunar_lander.domain.trainer.TrainerDependencies;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hellgren.plotters.plotting_2d.SwingHeatMapPlotter;
import org.hellgren.utilities.list_arrays.List2ArrayConverter;

import java.util.function.Function;

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

    public static PlotterAgent ofAgentParameters(TrainerDependencies trainerDependencies) {
        return new PlotterAgent(trainerDependencies,
                Settings.fromAgentProperties(trainerDependencies.agent().getAgentParameters()));
    }

    public static PlotterAgent of(TrainerDependencies trainerDependencies, Settings settings) {
        return new PlotterAgent(trainerDependencies, settings);
    }

    public void plotAll() {
        plotExpectedForce();
        plotExpectedAcceleration();
        plotValue();
    }

    public void plotExpectedForce() {
        Function<StateLunar,Double> func = s -> dependencies.agent().readActor(s).mean();
        showData(settings.titleForce(), getData(func));
    }

    public void plotExpectedAcceleration() {
        var env=(EnvironmentLunar) dependencies.environment();
        Function<StateLunar,Double> func = s ->
                env.acceleration(dependencies.agent().readActor(s).mean());
        showData(settings.titleAcc, getData(func));
    }

    public void plotValue() {
        Function<StateLunar,Double> func = s -> dependencies.agent().readCritic(s);
        showData(settings.titleValue, getData(func));
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

    private void showData(String title, double[][] data) {
        var yList = dependencies.environment().getProperties().ySpace(settings.nY());
        var spdList = dependencies.environment().getProperties().spdSpace(settings.nSpd());
        var shower = SwingHeatMapPlotter.builder()
                .plotWidth(settings.plotWidth).plotHeight(settings.plotHeight)
                .xAxisLabels(List2ArrayConverter.convertListToStringArr(spdList, settings.nDigitsAxisLabels()))
                .yAxisLabels(List2ArrayConverter.convertListToStringArr(yList, settings.nDigitsAxisLabels()))
                .valueFormat(settings.valueFormat)
                .showLabels(true)
                .build();
        shower.showHeatMap(data, title);
    }


}
