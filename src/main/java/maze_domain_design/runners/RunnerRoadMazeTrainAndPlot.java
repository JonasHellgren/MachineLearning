package maze_domain_design.runners;

import maze_domain_design.domain.shared.TrainerPlotter;
import maze_domain_design.services.TrainingService;
import maze_domain_design.services.TrainingServiceFactory;

public class RunnerRoadMazeTrainAndPlot {

    static TrainingService training;
    static TrainerPlotter trainerPlotter;

    public static void main(String[] args) {

        training=TrainingServiceFactory.createRoadMaze();
        training.train();
        trainerPlotter =TrainerPlotter.ofTrainingService(training);
        System.out.println("mem = " + training.getAgent().getMemory());
        trainerPlotter.plot();

    }
}
