package policy_gradient_problems.runners;

import lombok.extern.java.Log;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.markers.SeriesMarkers;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.short_corridor.*;
import policy_gradient_problems.helpers.RecorderActionProbabilities;

import java.util.ArrayList;
import java.util.List;

@Log
public class RunnerShortCorridor {

    public static final int PLOTTED_ACTION = 0;
    public static final int NOF_EPISODES = 500;  //5000 for convergence
    static List<List<Double>> probA0S0Lists=new ArrayList<>();
    static List<String> probA0S0TitlesLists=new ArrayList<>();
    static List<List<Double>> probA0S1Lists=new ArrayList<>();
    static List<String> probA0S1TitlesLists=new ArrayList<>();
    static List<List<Double>> probA0S2Lists=new ArrayList<>();
    static List<String> probA0S2TitlesLists=new ArrayList<>();

    public static void main(String[] args) {
        var trainer = createTrainerParam(AgentParamActorSC.newRandomStartStateDefaultThetas());
        trainer.train();
        log.info("Parameter trained");
        addDataToPlotLists(trainer, "Param");


        var trainerCEM = createTrainerCEM(AgentActorICriticSCLossCEM.newDefault());
        trainerCEM.train();
        log.info("CEM trained");
        addDataToPlotLists(trainerCEM, "CEM");
        trainerCEM.getRecorderTrainingProgress().plot("CEM");

        var trainerPPO = createTrainerPPO(AgentActorCriticSCLossPPO.newDefault());
        trainerPPO.train();
        log.info("PPO trained");
        addDataToPlotLists(trainerPPO, "PPO");
        trainerPPO.getRecorderTrainingProgress().plot("PPO");

        doPlottingAllTrainers();
    }

    private static void doPlottingAllTrainers() {
        plotsprobXForTrainerVersusEpisode("S0A0", probA0S0Lists,probA0S0TitlesLists);
        plotsprobXForTrainerVersusEpisode("S1A0", probA0S1Lists,probA0S1TitlesLists);
        plotsprobXForTrainerVersusEpisode("S2A0", probA0S2Lists,probA0S2TitlesLists);
    }

    private static void addDataToPlotLists(TrainerAbstractSC trainer, String seriesName) {
        RecorderActionProbabilities recorder = trainer.getRecorderActionProbabilities();
        probA0S0Lists.add(recorder.probabilityTrajectoryForStateAndAction(0,PLOTTED_ACTION));
        probA0S0TitlesLists.add(seriesName);
        probA0S1Lists.add(recorder.probabilityTrajectoryForStateAndAction(1,PLOTTED_ACTION));
        probA0S1TitlesLists.add(seriesName);
        probA0S2Lists.add(recorder.probabilityTrajectoryForStateAndAction(2,PLOTTED_ACTION));
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

    private static TrainerParamActorSC createTrainerParam(AgentParamActorSC agent) {
        return TrainerParamActorSC.builder()
                .environment(EnvironmentSC.create()).agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(NOF_EPISODES).gamma(0.5).build())
                .build();
    }

    private static TrainerActorCriticSCLossCEM createTrainerCEM(AgentActorICriticSCLossCEM agent) {
        return TrainerActorCriticSCLossCEM.builder()
                .environment(EnvironmentSC.create())
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(NOF_EPISODES).gamma(0.5).nofStepsMax(5).build())
                .build();
    }


    private static TrainerActorCriticSCLossPPO createTrainerPPO(AgentActorCriticSCLossPPO agent) {
        return TrainerActorCriticSCLossPPO.builder()
                .environment(EnvironmentSC.create())
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(NOF_EPISODES).gamma(0.5).nofStepsMax(5).build())
                .build();
    }

}
