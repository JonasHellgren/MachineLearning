package policy_gradient_problems.runners;

import common.MovingAverage;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;
import policy_gradient_problems.common.TrainingTracker;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.cart_pole.*;
import java.util.List;

public class RunnerTrainersPole {


    public static final int LENGTH_WINDOW = 100;

    public static void main(String[] args) {

        AgentPole agent = AgentPole.newAllZeroStateDefaultThetas();
        EnvironmentPole environment = new EnvironmentPole(ParametersPole.newDefault());

        TrainerParameters parametersTrainer = TrainerParameters.builder()
                .nofEpisodes(15000).nofStepsMax(100).gamma(0.99).beta(1e-3).learningRate(1e-3)
                .build();

        var trainerVanilla = TrainerVanillaPole.builder()
                .environment(environment).agent(agent.copy()).parameters(parametersTrainer).build();
        trainerVanilla.train();
        List<Double> nofStepsListVanilla = getFilteredNofSteps(trainerVanilla.getTracker());

        var trainerBaseline = TrainerBaselinePole.builder()
                .environment(environment).agent(agent.copy()).parameters(parametersTrainer).build();
        trainerBaseline.train();
        List<Double> nofStepsListBaseline = getFilteredNofSteps(trainerBaseline.getTracker());

        plotNofStepsVersusEpisode(
                List.of(nofStepsListVanilla, nofStepsListBaseline),
                List.of("vanilla", "baseline"));

    }

    private static void plotNofStepsVersusEpisode(List<List<Double>> listList, List<String> titles) {
        XYChart chart = new XYChartBuilder().xAxisTitle("Episode").yAxisTitle("Nof steps").width(500).height(300).build();
        var titlesIterator = titles.iterator();
        for (List<Double> doubles : listList) {
            XYSeries series = chart.addSeries(titlesIterator.next(), null, doubles);
            series.setMarker(SeriesMarkers.NONE);
        }
        new SwingWrapper<>(chart).displayChart();

    }

    private static List<Double> getFilteredNofSteps(TrainingTracker tracker) {
        var nofStepsList = tracker.getMeasureTrajectoriesForState(0).get(0);
        var movingAverage = new MovingAverage(LENGTH_WINDOW, nofStepsList);
        return movingAverage.getFiltered();
    }

}
