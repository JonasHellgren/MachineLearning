package policygradient.maze;

import common.other.Counter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.value_classes.StepReturn;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.maze.*;
import policy_gradient_problems.helpers.NeuralActorUpdaterPPOLoss;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTrainerMazeAgentMultiStepPPO {

    TrainerMazeAgentMultiStepPPO trainer;
    MazeAgentPPO agent;
    EnvironmentMaze environment;
    MazeSettings settings;

    @BeforeEach
    void init() {
        environment= EnvironmentMaze.newDefault();
        agent=MazeAgentPPO.newDefaultAtX0Y0();
        settings=MazeSettings.newDefault();
        trainer=createTrainer();
    }


    private TrainerMazeAgentMultiStepPPO createTrainer() {
        return  TrainerMazeAgentMultiStepPPO.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(1000).nofStepsMax(30).gamma(0.99)  //0.95
                        .stepHorizon(3)
                        .build())
                .mazeSettings(settings)
                .actorUpdater(new NeuralActorUpdaterPPOLoss<>())
                .build();
    }

    @SneakyThrows
    @Test
    @Disabled("Long time")
    void whenTrained_thenFewSteps() {
        trainer.train();

        int nofSteps=runTrainedAgent(StateMaze.newFromPoint(new Point2D.Double(0,0)));
        System.out.println("nofSteps = " + nofSteps);

        var plotter=new MazeAgentPlotter(agent,settings);
        plotter.plotValues();
        plotter.plotBestAction();
        Thread.sleep(10000);

        assertTrue(nofSteps < 10);


    }

    public int runTrainedAgent(StateMaze stateStart) {
        agent.setState(stateStart);
        Counter counter=new Counter();
        StepReturn<VariablesMaze> stepReturn;
        do {
            System.out.println("agent.getState() = " + agent.getState());
            System.out.println("agent.actionProbabilitiesInPresentState() = " + agent.actionProbabilitiesInPresentState());
            System.out.println("agent.criticOut(agent.getState()) = " + agent.criticOut(agent.getState()));
            stepReturn = environment.step(agent.getState(),agent.chooseAction());
            agent.setState(stepReturn.state().copy());
            counter.increase();
        } while (!stepReturn.isTerminal() );
        return counter.getCount();
    }



}
