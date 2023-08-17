package multi_step_temp_diff.runners;

import multi_step_temp_diff.domain.agent_abstract.ReplayBufferInterface;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStepPrioritized;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStepUniform;
import plotters.PlotterMultiplePanelsTrajectory;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironment;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.helpers_common.AgentInfo;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agents.fork.AgentForkNeural;
import multi_step_temp_diff.domain.trainer_valueobj.NStepNeuralAgentTrainerSettings;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class RunnerForkNStepNeuralAgentTrainer_UniformVersusPrioritizedBuffer {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 5;
    private static final int BATCH_SIZE = 10;
    private static final int NOF_EPIS = 500;
    private static final int START_STATE = 0;
    private static final int LENGTH_WINDOW = 1000;
    private static final int DISCOUNT_FACTOR = 1;
    static NStepNeuralAgentTrainer<ForkVariables> trainer;
    static AgentNeuralInterface<ForkVariables> agent;
    static AgentForkNeural agentCasted;
    static ForkEnvironment environment;
    static AgentInfo<ForkVariables> agentInfo;

    public static void main(String[] args) {
        environment=new ForkEnvironment();
        agent = AgentForkNeural.newWithDiscountFactor(environment, DISCOUNT_FACTOR);
        agentInfo= new AgentInfo<>(agent);

        buildTrainer(ReplayBufferNStepUniform.newDefault());
        trainer.train();
        plotTdError("Error uniform");
        printStateValues();

        agent.clear();
        buildTrainer(ReplayBufferNStepPrioritized.<ForkVariables>builder()
                .beta(0.5)
                .build());
        trainer.train();
        plotTdError("Error prioritized");
        printStateValues();

    }

    private static void printStateValues() {
        DecimalFormat formatter = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US)); //US <=> only dots
        agentInfo.getStateValues(agentCasted.getMemory(), environment.stateSet())
                .forEach((s,v) -> System.out.println("s="+s+", v="+formatter.format(v)));
    }

    static void  plotTdError(String yTitle) {
        AgentInfo<ForkVariables> agentInfo=new AgentInfo<>(agent);
        List<List<Double>> listOfTrajectories=new ArrayList<>();
        List<Double> filtered1 = agentInfo.getFilteredTemporalDifferenceList(LENGTH_WINDOW);
        listOfTrajectories.add(filtered1);
        PlotterMultiplePanelsTrajectory plotter=new PlotterMultiplePanelsTrajectory(Collections.singletonList(yTitle), "Step");
        plotter.plot(listOfTrajectories);
    }

    public static void buildTrainer(ReplayBufferInterface<ForkVariables> buffer) {
        agentCasted=(AgentForkNeural) agent;
        NStepNeuralAgentTrainerSettings settings= NStepNeuralAgentTrainerSettings.builder()
                .probStart(0.25).probEnd(1e-3).nofIterations(1)
                .batchSize(BATCH_SIZE)
                .nofEpis(NOF_EPIS)
                .nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                .build();

        trainer= NStepNeuralAgentTrainer.<ForkVariables>builder()
                .settings(settings)
                .startStateSupplier(() -> ForkState.newFromPos(START_STATE))
                .agentNeural(agent)
                .environment(environment)
                .buffer(buffer)
                .build();

        System.out.println("buildTrainer");
    }



}
