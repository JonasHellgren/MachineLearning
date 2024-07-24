package domain_design_tabular_q_learning.runners;

import domain_design_tabular_q_learning.environments.obstacle_on_road.GridActionProperties;
import domain_design_tabular_q_learning.environments.obstacle_on_road.GridVariables;
import lombok.SneakyThrows;
import domain_design_tabular_q_learning.services.PlottingSettings;
import domain_design_tabular_q_learning.services.RoadPlottingService;
import domain_design_tabular_q_learning.services.TrainingService;
import domain_design_tabular_q_learning.services.TrainingServiceFactory;

public class RunnerRoadMazeTrainAndPlot {

    static final String DIR="src/main/java/domain_design_tabular_q_learning/documentation/pics/";
    public static final String PNG = ".png";

    static TrainingService training;
    static RoadPlottingService<GridVariables, GridActionProperties> plotting;

    @SneakyThrows
    public static void main(String[] args) {
        training = TrainingServiceFactory.createRoadMaze();
        training.train();

        PlottingSettings settings= PlottingSettings.newRunnerRoad();
        plotting = RoadPlottingService.ofTrainingService(training,settings);
        plotting.plotEnvironment();
        plotting.plotTrainer();
        plotting.plotAgent();

        plotting.saveEnvironmentChart(DIR,"roadMazeEnv", PNG);
        plotting.saveTrainingCharts(DIR,"training", PNG);
        plotting.saveAgentCharts(DIR,"agent", PNG);
    }
}
