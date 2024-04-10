package policy_gradient_problems.runners;

import common.Counter;
import policy_gradient_problems.domain.value_classes.StepReturn;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.maze.*;
import policy_gradient_problems.helpers.NeuralActorUpdaterPPOLoss;

import java.awt.geom.Point2D;

public class RunnerMazeAgentMultiStepPPO {

    //static EnvironmentMaze environment= EnvironmentMaze.newDefault();
    static EnvironmentMaze environment= EnvironmentMaze.newOneRow();
    static MazeAgentPPO agent=MazeAgentPPO.newDefaultAtX0Y0();
    static TrainerMazeAgentMultiStepPPO trainer=createTrainer();

    public static void main(String[] args) {
        trainer.train();

        var runner=AgentRunner.builder().environment(environment).agent(agent).build();
        int nofSteps=runner.runTrainedAgent(StateMaze.newFromPoint(new Point2D.Double(0,0)));
        System.out.println("nofSteps = " + nofSteps);
        var plotter=new MazeAgentPlotter(agent,environment.getSettings());
        plotter.plotValues();
        plotter.plotBestAction();
        trainer.getRecorderTrainingProgress().plot("trainer");
    }

    private static TrainerMazeAgentMultiStepPPO createTrainer() {
        return  TrainerMazeAgentMultiStepPPO.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(50).nofStepsMax(10).gamma(1.00)  //0.95
                        .stepHorizon(3)
                        .build())
                .mazeSettings(environment.getSettings())
                .actorUpdater(new NeuralActorUpdaterPPOLoss<>())
                .build();
    }




}
