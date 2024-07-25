package domain_design_tabular_q_learning.runners;

import domain_design_tabular_q_learning.domain.plotting.FileDirName;
import domain_design_tabular_q_learning.environments.avoid_obstacle.GridActionProperties;
import domain_design_tabular_q_learning.environments.avoid_obstacle.GridVariables;
import lombok.SneakyThrows;
import domain_design_tabular_q_learning.services.PlottingSettings;
import domain_design_tabular_q_learning.services.RoadPlottingService;
import domain_design_tabular_q_learning.services.TrainingService;
import domain_design_tabular_q_learning.services.TrainingServiceFactory;

public class RunnerRoadMazeTrainAndPlot {

    static final String DIR="src/main/java/domain_design_tabular_q_learning/documentation/environments/avoid_obstacle/pics";
    public static final String PNG = ".png";

    static TrainingService<GridVariables,GridActionProperties> training;
    static RoadPlottingService<GridVariables, GridActionProperties> plotting;

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
