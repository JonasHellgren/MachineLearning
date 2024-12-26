package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.environment.EnvironmentLunar;
import book_rl_explained.lunar_lander.domain.trainer.ExperienceLunar;
import book_rl_explained.lunar_lander.domain.trainer.TrainerDependencies;
import com.beust.jcommander.internal.Lists;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.hellgren.utilities.conditionals.Conditionals;
import org.hellgren.utilities.conditionals.Counter;
import org.hellgren.utilities.list_arrays.MyListUtils;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.util.List;

@AllArgsConstructor
public class AgentEvaluator {

    public static final int WIDTH = 600;
    public static final int HEIGHT = 300;
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
        var creator = EpisodeCreator.of(dependencies);
        var experiencesNotExploring = creator.getExperiencesNotExploring();
        var ep = dependencies.environment().getProperties();
        var speeds = ExperiencesInfo.of(experiencesNotExploring).speeds();
        var forces = ExperiencesInfo.of(experiencesNotExploring).forces(ep);
        var accelerations = ExperiencesInfo.of(experiencesNotExploring)
                .accelerations((EnvironmentLunar) dependencies.environment());
        var positions = ExperiencesInfo.of(experiencesNotExploring).positions();
        var times = ExperiencesInfo.of(experiencesNotExploring).times(ep.dt());
        XYChart chart = new XYChartBuilder()
                .xAxisTitle("Time(s)").yAxisTitle("").width(WIDTH).height(HEIGHT).build();
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideE);

        var minMax = getMinMax(speeds, forces, accelerations, positions);
        addSeries(chart,times, forces, minMax, "Force(kN)");
        addSeries(chart,times, accelerations, minMax,"Acc. (m/s^2)");
        addSeries(chart,times, speeds, minMax,"Speed(m/s)");
        addSeries(chart,times, positions, minMax,"Pos(m)");
        new SwingWrapper<>(chart).displayChart();
}

     static Pair<Double, Double> getMinMax(List<Double> speeds,
                                                  List<Double> forces,
                                                  List<Double> accelerations,
                                                  List<Double> positions) {
        List<Double> mergedList= Lists.newArrayList();
        mergedList.addAll(speeds);
        mergedList.addAll(forces);
        mergedList.addAll(accelerations);
        mergedList.addAll(positions);
        return Pair.of(MyListUtils.findMin(mergedList).orElseThrow(),MyListUtils.findMax(mergedList).orElseThrow());
    }

    private void addSeries(XYChart chart, List<Double> times, List<Double> values, Pair<Double,Double> minMax, String measure) {
        chart.getStyler().setYAxisMin(minMax.getLeft());
        chart.getStyler().setYAxisMax(minMax.getRight());
        XYSeries series = chart.addSeries(measure, times, values);
        series.setMarker(SeriesMarkers.NONE);
    }


}
