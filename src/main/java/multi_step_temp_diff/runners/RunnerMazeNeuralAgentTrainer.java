package multi_step_temp_diff.runners;

import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_valueobj.AgentMazeNeuralSettings;
import multi_step_temp_diff.domain.agents.maze.AgentMazeNeural;
import multi_step_temp_diff.domain.environments.maze.MazeEnvironment;
import multi_step_temp_diff.domain.environments.maze.MazeState;
import multi_step_temp_diff.domain.environments.maze.MazeVariables;
import multi_step_temp_diff.domain.helpers_common.TdErrorPlotter;
import multi_step_temp_diff.domain.helpers_specific.MazeStateValuePrinter;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import multi_step_temp_diff.domain.trainer_valueobj.NStepNeuralAgentTrainerSettings;

public class RunnerMazeNeuralAgentTrainer {

    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 3;
    private static final int BATCH_SIZE = 30, BUFFER_SIZE_MAX = 1_000;
    private static final int NOF_EPIS = 5000;
    public static final double LEARNING_RATE =1e-2;
    public static final double PROB_START = 0.1, PROB_END = 1e-5;
    public static final double DISCOUNT_FACTOR = 1.0;

    static  MazeEnvironment environment;

    public static void main(String[] args) {

        environment=new MazeEnvironment();
        AgentNeuralInterface<MazeVariables> agent=createAgent();
        NStepNeuralAgentTrainer<MazeVariables> trainer=createTrainer(agent);
        trainer.train();

        MazeStateValuePrinter<MazeVariables> printer=new MazeStateValuePrinter<>(agent,environment);
        printer.printMazeNeuralAgent();

        TdErrorPlotter<MazeVariables> tdErrorPlotter=TdErrorPlotter.<MazeVariables>builder()
                .maxValueInPlot(100d)
                .agent(agent).build();
        tdErrorPlotter.plotTdError();

    }


    static AgentNeuralInterface<MazeVariables> createAgent() {

        AgentMazeNeuralSettings agentSettings = AgentMazeNeuralSettings.builder()
                .discountFactor(DISCOUNT_FACTOR).learningRate(LEARNING_RATE)
                .build();
        return new AgentMazeNeural(environment, agentSettings);
    }

    static NStepNeuralAgentTrainer<MazeVariables> createTrainer(AgentNeuralInterface<MazeVariables> agent) {
        var settings= NStepNeuralAgentTrainerSettings.builder()
                .probStart(PROB_START).probEnd(PROB_END).nofIterations(1)
                .batchSize(BATCH_SIZE)
                .nofEpis(NOF_EPIS).batchSize(BATCH_SIZE).maxBufferSize(BUFFER_SIZE_MAX)
                .nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                .nofEpisodesBetweenLogs(100)
                .build();

        return NStepNeuralAgentTrainer.<MazeVariables>builder()
                .settings(settings)
                .startStateSupplier(MazeState::newFromRandomPos)
                .agentNeural(agent)
                .environment(environment)
                .build();

    }

}
