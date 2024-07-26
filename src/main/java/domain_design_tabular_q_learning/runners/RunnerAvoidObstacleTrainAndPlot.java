package domain_design_tabular_q_learning.runners;

import domain_design_tabular_q_learning.domain.plotting.FileDirName;
import domain_design_tabular_q_learning.environments.avoid_obstacle.PropertiesRoad;
import domain_design_tabular_q_learning.environments.avoid_obstacle.RoadActionProperties;
import domain_design_tabular_q_learning.environments.shared.GridVariables;
import lombok.SneakyThrows;
import domain_design_tabular_q_learning.services.PlottingSettings;
import domain_design_tabular_q_learning.services.RoadPlottingService;
import domain_design_tabular_q_learning.services.TrainingService;
import domain_design_tabular_q_learning.services.TrainingServiceFactory;

public class RunnerAvoidObstacleTrainAndPlot {

    static final String DIR=
            "src/main/java/domain_design_tabular_q_learning/documentation/environments/avoid_obstacle/pics";
    public static final String PNG = ".png";

    static TrainingService<GridVariables, RoadActionProperties, PropertiesRoad> training;
    static RoadPlottingService<GridVariables, RoadActionProperties,PropertiesRoad> plotting;

    @SneakyThrows
    public static void main(String[] args) {
        training = TrainingServiceFactory.createRoadMaze();
        training.getEnvironment().setProperties(
                training.getEnvironment().getProperties().withRewardFailTerminalStd(30d));
        training.train();

        PlottingSettings settings= PlottingSettings.newRunnerRoad();
        plotting = RoadPlottingService.ofTrainingService(training,settings);
        plotting.plotEnvironment();
        plotting.plotTrainer();
        plotting.plotAgent();

        plotting.saveEnvironmentChart(FileDirName.of(DIR,"roadMazeEnv", PNG));
        plotting.saveTrainingCharts(FileDirName.of(DIR,"training", PNG));
        plotting.saveAgentCharts(FileDirName.of(DIR,"agent", PNG));
    }
}
