package policy_gradient_problems.runners;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.markers.SeriesMarkers;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.short_corridor.*;
import java.util.ArrayList;
import java.util.List;

public class RunnerShortCorridor {

    public static final int PLOTTED_ACTION = 0;
    static List<List<Double>> probA0S0Lists=new ArrayList<>();
    static List<String> probA0S0TitlesLists=new ArrayList<>();
    static List<List<Double>> probA0S1Lists=new ArrayList<>();
    static List<String> probA0S1TitlesLists=new ArrayList<>();
    static List<List<Double>> probA0S2Lists=new ArrayList<>();
    static List<String> probA0S2TitlesLists=new ArrayList<>();

    public static void main(String[] args) {

        var trainer = createTrainerVanilla(AgentParamActorSC.newRandomStartStateDefaultThetas());
        trainer.train();
        addDataToPlotLists(trainer, "Vanilla");

        var trainerActorCritic = createTrainerNeuralAC(AgentNeuralActorNeuralCriticSC.newDefault());
        trainerActorCritic.train();
        addDataToPlotLists(trainerActorCritic, "NeuralAC");

        doPlotting();
    }

    private static void doPlotting() {
        plotsprobXForTrainerVersusEpisode("S0A0", probA0S0Lists,probA0S0TitlesLists);
        plotsprobXForTrainerVersusEpisode("S1A0", probA0S1Lists,probA0S1TitlesLists);
        plotsprobXForTrainerVersusEpisode("S2A0", probA0S2Lists,probA0S2TitlesLists);
    }

    private static void addDataToPlotLists(TrainerAbstractSC trainer, String seriesName) {
        probA0S0Lists.add(trainer.getTracker().getMeasureTrajectoriesForState(0).get(PLOTTED_ACTION));
        probA0S0TitlesLists.add(seriesName);
        probA0S1Lists.add(trainer.getTracker().getMeasureTrajectoriesForState(1).get(PLOTTED_ACTION));
        probA0S1TitlesLists.add(seriesName);
        probA0S2Lists.add(trainer.getTracker().getMeasureTrajectoriesForState(2).get(PLOTTED_ACTION));
        probA0S2TitlesLists.add(seriesName);
    }


    private static void plotsprobXForTrainerVersusEpisode(String yTtitle, List<List<Double>> listList, List<String> titles) {
        var chart = new XYChartBuilder().xAxisTitle("Episode").yAxisTitle(yTtitle).width(250).height(200).build();
        var titlesIterator = titles.iterator();
        for (List<Double> doubles : listList) {
            var series = chart.addSeries(titlesIterator.next(), null, doubles);
            series.setMarker(SeriesMarkers.NONE);
        }
        new SwingWrapper<>(chart).displayChart();
    }

    private static TrainerVanillaSC createTrainerVanilla(AgentParamActorSC agent) {
        return TrainerVanillaSC.builder()
                .environment(EnvironmentSC.create()).agent(agent)
                .parameters(getTrainerParameters())
                .build();
    }

    private static TrainerNeuralActorNeuralCriticSC createTrainerNeuralAC(AgentNeuralActorNeuralCriticSC agent) {
        return TrainerNeuralActorNeuralCriticSC.builder()
                .environment(EnvironmentSC.create())
                .agent(agent)
                .parameters(getTrainerParameters())
                .build();
    }

    private static TrainerParameters getTrainerParameters() {
        return TrainerParameters.builder()
                .nofEpisodes(2000).nofStepsMax(100)
                .gamma(0.9)  //.relativeNofFitsPerEpoch(1.0)
                .build();
    }


}
