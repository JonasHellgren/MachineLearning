package maze_domain_design.runners;

import maze_domain_design.services.PlottingSettings;
import maze_domain_design.services.PlottingService;
import maze_domain_design.services.TrainingService;
import maze_domain_design.services.TrainingServiceFactory;

public class RunnerRoadMazeTrainAndPlot {
    static TrainingService training;
    static PlottingService plotting;

    public static void main(String[] args) {
        training = TrainingServiceFactory.createRoadMaze();
        training.train();

        PlottingSettings settings= PlottingSettings.newRunnerRoad();
        plotting = PlottingService.ofTrainingService(training,settings);
        plotting.plotEnvironment();
        plotting.plotTrainer();
        plotting.plotAgent();
    }
}
