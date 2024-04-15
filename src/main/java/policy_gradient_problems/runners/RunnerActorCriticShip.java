package policy_gradient_problems.runners;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.extern.java.Log;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.markers.SeriesMarkers;
import policy_gradient_problems.domain.common_episode_trainers.ActorCriticEpisodeTrainerPPOCont;
import policy_gradient_problems.domain.common_episode_trainers.ParamActorTabCriticEpisodeTrainer;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.sink_the_ship.*;

import java.util.List;

@Log
public class RunnerActorCriticShip {

    public static final double VALUE_TERMINAL_STATE = 0;
    public static final int NOF_EPISODES = 2000;
    static final int NOF_STEPS_MAX = 100;
    public static final double LEARNING_RATE_PARAM = 1e-3;
    static final double GAMMA = 1.0;

    static final List<String> MEASURES_RECORDED = List.of("mean", "std", "value");

    public static void main(String[] args) {
        Table<String, Integer, List<List<Double>>> tableData= HashBasedTable.create();  //type, pos -> trajectories

        var trainerParam = createTrainerParam(
                new EnvironmentShip(ShipSettings.newDefault()), AgentShipParam.newRandomStartStateDefaultThetas());
        trainerParam.train();
        putTrajInTable(tableData, "param", trainerParam);
        log.info("Training finished param");
/*

        var trainerPPO = createTrainerPPO(EnvironmentShip.newDefault(), AgentShipPPO.newDefault());
        trainerPPO.train();
        putTrajInTable(tableData, "ppo", trainerPPO);
        log.info("Training finished ppo");
*/

        var trainerSafe = createTrainerSafe(EnvironmentShip.newDefault(), AgentACShipSafe.newRandomStartStateDefaultThetas());
        trainerSafe.train();
        putTrajInTable(tableData, "safe", trainerSafe);
        log.info("Training finished safe");

        for (Integer pos : EnvironmentShip.POSITIONS) {
            plotTable(tableData, pos);
        }
    }

    private static void putTrajInTable(Table<String, Integer, List<List<Double>>> plotTable,
                                       String type,
                                       TrainerAbstractShip trainerParam) {
        for (Integer pos : EnvironmentShip.POSITIONS) {
        List<List<Double>> listOfTrajectories = getTrajectoriesInState(trainerParam, pos);
        plotTable.put(type,pos,listOfTrajectories);
        }
    }



    private static List<List<Double>> getTrajectoriesInState(TrainerAbstractShip trainer, int s) {
        return trainer.getRecorderStateValues().valuesTrajectoryForEachAction(s);
    }

    private static TrainerActorCriticShipParam createTrainerParam(EnvironmentShip environment, AgentShipParam agent) {
        return TrainerActorCriticShipParam.builder()
                .environment(environment).agent(agent)
                .parameters(getTrainerParameters())
                .episodeTrainer(ParamActorTabCriticEpisodeTrainer.<VariablesShip>builder()
                        .agent(agent)
                        .parameters(getTrainerParameters())
                        .valueTermState(VALUE_TERMINAL_STATE)
                        .tabularCoder((v) -> v.pos())
                        .isTerminal((s) -> false)
                        .build())
                .build();
    }


    private static TrainerActorCriticShipParam createTrainerSafe(EnvironmentShip environment, AgentACShipSafe agent) {
        return TrainerActorCriticShipParam.builder()
                .environment(environment).agent(agent)
                .parameters(getTrainerParameters())
                .episodeTrainer(ParamActorTabCriticEpisodeTrainer.<VariablesShip>builder()
                        .agent(agent)
                        .parameters(getTrainerParameters())
                        .valueTermState(VALUE_TERMINAL_STATE)
                        .tabularCoder((v) -> v.pos())
                        .isTerminal((s) -> false)
                        .build())
                .build();
    }

    private static TrainerActorCriticShipPPO createTrainerPPO(EnvironmentShip environment, AgentShipPPO agent) {
        return TrainerActorCriticShipPPO.builder()
                .environment(environment).agent(agent)
                .shipSettings(ShipSettings.newDefault())
                .parameters(getTrainerParameters())
                .episodeTrainer(ActorCriticEpisodeTrainerPPOCont.<VariablesShip>builder()
                        .valueTermState(0d).agent(agent).parameters(getTrainerParameters()).build())
                .build();
    }



    private static TrainerParameters getTrainerParameters() {
        return TrainerParameters.builder()
                .nofEpisodes(NOF_EPISODES).nofStepsMax(NOF_STEPS_MAX)
                .gamma(GAMMA).learningRateNonNeuralActor(LEARNING_RATE_PARAM)
                .build();
    }


    private static void plotTable(Table<String, Integer, List<List<Double>>> table,int s) {
        List<String> types = List.of("param", "safe");

        for (String measure : MEASURES_RECORDED) {
            var chart = new XYChartBuilder().xAxisTitle("Episode").yAxisTitle(measure).width(350).height(200).build();
            chart.getStyler().setYAxisDecimalPattern("0.00");  // Set Y-axis to show 2 decimal places
            chart.setTitle("state="+s);
            for (String type:types) {
                List<List<Double>> trajs = table.get(type, s);
                var series = chart.addSeries(type, null, trajs.get(MEASURES_RECORDED.indexOf(measure)));
                series.setMarker(SeriesMarkers.NONE);
            }
            new SwingWrapper<>(chart).displayChart();
        }
    }

}
