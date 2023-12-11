package policy_gradient_problems.runners;

import common.MovingAverage;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;
import policy_gradient_problems.common.TrainingTracker;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.cart_pole.*;
import java.util.List;

public class RunnerTrainersPole {


    public static final int LENGTH_WINDOW = 1000;
    public static final TrainerParameters PARAMETERS_TRAINER = TrainerParameters.builder()
            .nofEpisodes(10_000).nofStepsMax(100).gamma(0.99).beta(1e-3).stepHorizon(10).learningRate(1e-3)
            .build();

    public static void main(String[] args) {
        var agent = AgentPole.newAllZeroStateDefaultThetas();
        var environment = new EnvironmentPole(ParametersPole.newDefault());

        var nofStepsListVanilla = getNofStepsListVanilla(agent, environment);
        var nofStepsListBaseline = getNofStepsListBaseline(agent, environment);
        var nofStepsListAC = getNofStepsListAC(agent, environment);

        plotNofStepsVersusEpisode(
                List.of(nofStepsListVanilla, nofStepsListBaseline,nofStepsListAC),
                List.of("vanilla", "baseline", "actor critic"));
    }

    private static List<Double> getNofStepsListVanilla(AgentPole agent, EnvironmentPole environment) {
        var trainerVanilla = TrainerVanillaPole.builder()
                .environment(environment).agent(agent.copy()).parameters(PARAMETERS_TRAINER).build();
        trainerVanilla.train();
        return getFilteredNofSteps(trainerVanilla.getTracker());
    }

    private static List<Double> getNofStepsListBaseline(AgentPole agent, EnvironmentPole environment) {
        var trainerBaseline = TrainerBaselinePole.builder()
                .environment(environment).agent(agent.copy()).parameters(PARAMETERS_TRAINER).build();
        trainerBaseline.train();
        return getFilteredNofSteps(trainerBaseline.getTracker());
    }

    private static List<Double> getNofStepsListAC(AgentPole agent, EnvironmentPole environment) {
        var trainerAC = TrainerActorCriticPole.builder()
                .environment(environment).agent(agent.copy()).parameters(PARAMETERS_TRAINER).build();
        trainerAC.train();
        return getFilteredNofSteps(trainerAC.getTracker());
    }


    private static void plotNofStepsVersusEpisode(List<List<Double>> listList, List<String> titles) {
        var chart = new XYChartBuilder().xAxisTitle("Episode").yAxisTitle("Nof steps").width(500).height(300).build();
        var titlesIterator = titles.iterator();
        for (List<Double> doubles : listList) {
            var series = chart.addSeries(titlesIterator.next(), null, doubles);
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
