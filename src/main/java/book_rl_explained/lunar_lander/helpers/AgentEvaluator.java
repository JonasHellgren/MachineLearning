package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.trainer.ExperienceLunar;
import book_rl_explained.lunar_lander.domain.trainer.TrainerDependencies;
import lombok.AllArgsConstructor;
import org.hellgren.utilities.conditionals.Conditionals;
import org.hellgren.utilities.conditionals.Counter;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;
import java.util.List;

@AllArgsConstructor
public class AgentEvaluator {

    TrainerDependencies dependencies;

    public static AgentEvaluator of(TrainerDependencies dependencies) {
        return new AgentEvaluator(dependencies);
    }


    public double fractionFails(int nEvals) {
        var creator = new EpisodeCreator(dependencies);
        var failCounter = new Counter();
        var evalCounter = new Counter(nEvals);
        while (!evalCounter.isExceeded()) {
            System.out.println("evalCounter = " + evalCounter);
            var experiencesNotExploring = creator.getExperiencesNotExploring();
            ExperienceLunar endExperience = ExperiencesInfo.of(experiencesNotExploring).endExperience();
            Conditionals.executeIfTrue(endExperience.isTransitionToFail(), () -> failCounter.increase());
            Conditionals.executeIfTrue(true, () -> experiencesNotExploring.forEach(System.out::println));
            evalCounter.increase();
        }
        return (double) failCounter.getCount() / nEvals;
    }

    public void plotSimulation() {
        var creator = new EpisodeCreator(dependencies);
        var experiencesNotExploring = creator.getExperiencesNotExploring();
        var ep = dependencies.environment().getProperties();
        var speeds = ExperiencesInfo.of(experiencesNotExploring).speeds();
        var forces = ExperiencesInfo.of(experiencesNotExploring).forces();
        var positions = ExperiencesInfo.of(experiencesNotExploring).positions();
        var times = ExperiencesInfo.of(experiencesNotExploring).times(ep.dt());
        XYChart chart = new XYChartBuilder().xAxisTitle("Time(s)").yAxisTitle("").width(600).height(400).build();
        addSeries(chart,times, forces, "Force(kN)");
        addSeries(chart,times, speeds, "Speed(m/s)");
        addSeries(chart,times, positions, "Pos(m)");
        new SwingWrapper<>(chart).displayChart();
}

    private void addSeries(XYChart chart, List<Double> times, List<Double> values, String measure) {
        chart.getStyler().setYAxisMin(-2d);
        chart.getStyler().setYAxisMax(6d);
        XYSeries series = chart.addSeries(measure, times, values);
        series.setMarker(SeriesMarkers.NONE);
    }


}
