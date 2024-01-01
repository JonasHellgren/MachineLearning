package policy_gradient_problems.runners;

import common.MovingAverage;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;
import policy_gradient_problems.agent_interfaces.AgentParamActorNeuralCriticI;
import policy_gradient_problems.common_helpers.TrainingTracker;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.cart_pole.*;
import java.util.List;

public class RunnerTrainersPole {

    public static final int LENGTH_WINDOW = 100, NOF_STEPS_MAX = 300;
    public static final TrainerParameters PARAMETERS_TRAINER = TrainerParameters.builder()
            .nofEpisodes(2_000).nofStepsMax(NOF_STEPS_MAX).gamma(0.99).learningRateActor(1e-3)
            .learningRateCritic(1e-3)  //not relevant for vanilla  1e-3
            .stepHorizon(5).relativeNofFitsPerEpoch(0.5)   //only relevant for AC  10
            .build();

    public static void main(String[] args) {
        var agent = AgentParamActorPole.newAllZeroStateDefaultThetas();
        var agentAC = AgentParamActorNeuralCriticPole.newDefaultCritic(StatePole.newUprightAndStill());

        var environment = new EnvironmentPole(ParametersPole.newWithMaxNofSteps(NOF_STEPS_MAX));

        var nofStepsListVanilla = getNofStepsListVanilla(agent, environment);
        var nofStepsListBaseline = getNofStepsListBaseline(agent, environment);
        var nofStepsListAC = getNofStepsListAC(agentAC, environment);

        plotNofStepsVersusEpisode(
                List.of(nofStepsListVanilla, nofStepsListBaseline,nofStepsListAC),
                List.of("vanilla", "baseline", "actor critic"));
    }

    private static List<Double> getNofStepsListVanilla(AgentParamActorPole agent, EnvironmentPole environment) {
        var trainerVanilla = TrainerVanillaPole.builder()
                .environment(environment).agent(agent.copy()).parameters(PARAMETERS_TRAINER).build();
        trainerVanilla.train();
        return getFilteredNofSteps(trainerVanilla.getTracker());
    }

    private static List<Double> getNofStepsListBaseline(AgentParamActorPole agent, EnvironmentPole environment) {
        var trainerBaseline = TrainerBaselinePole.builder()
                .environment(environment).agent(agent).parameters(PARAMETERS_TRAINER).build();
        trainerBaseline.train();
        return getFilteredNofSteps(trainerBaseline.getTracker());
    }

    private static List<Double> getNofStepsListAC(AgentParamActorNeuralCriticI<VariablesPole> agent, EnvironmentPole environment) {
        var trainerAC = TrainerParamActorNeuralCriticPole.builder()
                .environment(environment).agent(agent).parameters(PARAMETERS_TRAINER).build();
        trainerAC.train();
        trainerAC.getAgent().setState(StatePole.newUprightAndStill());
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
