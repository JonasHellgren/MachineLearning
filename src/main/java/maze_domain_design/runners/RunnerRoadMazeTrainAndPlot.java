package maze_domain_design.runners;

import lombok.SneakyThrows;
import maze_domain_design.services.PlottingSettings;
import maze_domain_design.services.PlottingService;
import maze_domain_design.services.TrainingService;
import maze_domain_design.services.TrainingServiceFactory;

public class RunnerRoadMazeTrainAndPlot {

    static final String DIR="src/main/java/maze_domain_design/documentation/pics/";
    public static final String PNG = ".png";

    static TrainingService training;
    static PlottingService plotting;

    @SneakyThrows
    public static void main(String[] args) {
        training = TrainingServiceFactory.createRoadMaze();
        training.train();

        PlottingSettings settings= PlottingSettings.newRunnerRoad();
        plotting = PlottingService.ofTrainingService(training,settings);
        plotting.plotEnvironment();
        plotting.plotTrainer();
        plotting.plotAgent();

        plotting.saveEnvironmentChart(DIR,"roadMazeEnv", PNG);
        plotting.saveTrainingCharts(DIR,"training", PNG);
        plotting.saveAgentCharts(DIR,"agent", PNG);
    }
}
