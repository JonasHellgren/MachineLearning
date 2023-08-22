package multi_step_temp_diff.runners;

import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.remove_strategy.RemoveStrategyOldest;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.ReplayBufferNStepUniform;
import multi_step_temp_diff.domain.agent_valueobj.AgentMazeNeuralSettings;
import multi_step_temp_diff.domain.agents.maze.AgentMazeNeural;
import multi_step_temp_diff.domain.environments.maze.MazeEnvironment;
import multi_step_temp_diff.domain.environments.maze.MazeState;
import multi_step_temp_diff.domain.environments.maze.MazeVariables;
import multi_step_temp_diff.domain.helpers_common.TdErrorPlotter;
import multi_step_temp_diff.domain.helpers_specific.MazeStateValuePrinter;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import multi_step_temp_diff.domain.trainer_valueobj.NStepNeuralAgentTrainerSettings;

/***
 * For this environment a smaller buffer is better for convergence
 * - old bad epsiodes are forgotten
 *
 * Remove strategy not critical is small buffer (1000 items)
 * For larger buffer (5k items) it is critical/better to have OldestFirst
 *
 * Sätta NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED som stort (typ 100) ger divergens. Typ 10 isf 1 ger stabilare convergence.
 */

public class RunnerMazeNeuralAgentTrainer {

    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 10;
    private static final int BATCH_SIZE = 30, BUFFER_SIZE_MAX = 1_000;
    private static final int NOF_EPIS = 300;
    public static final double LEARNING_RATE =1e-1;
    public static final double PROB_START = 0.5, PROB_END = 1e-2;
    public static final double DISCOUNT_FACTOR = 1.0;

    static  MazeEnvironment environment;

    public static void main(String[] args) {

        environment=new MazeEnvironment();
        var agent=createAgent();
        var trainer=createTrainer(agent);
        trainer.train();

        var printer=new MazeStateValuePrinter<>(agent,environment);
        printer.printMazeNeuralAgent();

        var tdErrorPlotter=TdErrorPlotter.<MazeVariables>builder().maxValueInPlot(10d).agent(agent).build();
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
                .probStart(PROB_START).probEnd(PROB_END)
                .batchSize(BATCH_SIZE)
                .nofEpis(NOF_EPIS).batchSize(BATCH_SIZE)
                .nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                .nofEpisodesBetweenLogs(10)
                .build();

        return NStepNeuralAgentTrainer.<MazeVariables>builder()
                .settings(settings)
                .startStateSupplier(() -> new MazeState(MazeVariables.newFromXY(0,0)) )
                .agentNeural(agent)
                .environment(environment)
                .buffer(ReplayBufferNStepUniform.<MazeVariables>builder()
                        .maxSize(BUFFER_SIZE_MAX)
                        .removeStrategy(new RemoveStrategyOldest<>())
                        .build())
                .build();

    }

}
