package policy_gradient_problems.runners;

import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.maze.*;
import java.awt.geom.Point2D;

public class RunnerMazeAgentSingleStepPPO {

    //static EnvironmentMaze environment= EnvironmentMaze.newDefault();
    static EnvironmentMaze environment= EnvironmentMaze.newOneRow();

    static MazeAgentPPO agent=MazeAgentPPO.newDefaultAtX0Y0();

    static TrainerMazeAgentSingleStepPPO trainer=createTrainer();

    public static void main(String[] args) {
        trainer.train();

        var runner= AgentRunner.builder().environment(environment).agent(agent).build();
        int nofSteps=runner.runTrainedAgent(StateMaze.newFromPoint(new Point2D.Double(0,0)));
        System.out.println("nofSteps = " + nofSteps);
        var plotter=new MazeAgentPlotter(agent,environment.getSettings());
        plotter.plotValues();
        plotter.plotBestAction();
        trainer.getRecorderTrainingProgress().plot("trainer");
    }

    private static TrainerMazeAgentSingleStepPPO createTrainer() {
        return  TrainerMazeAgentSingleStepPPO.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(200).nofStepsMax(10).gamma(1.00)  //0.95
                        .build())
                .mazeSettings(environment.getSettings())
                .build();
    }


}
