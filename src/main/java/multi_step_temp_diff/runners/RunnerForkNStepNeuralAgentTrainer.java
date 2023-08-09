package multi_step_temp_diff.runners;

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

public class RunnerForkNStepNeuralAgentTrainer {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 5;
    private static final int BATCH_SIZE = 10;
    private static final int NOF_EPIS = 1000;
    private static final int START_STATE = 0;
    private static final int LENGTH_WINDOW = 500;
    private static final int DISCOUNT_FACTOR = 1;
    static NStepNeuralAgentTrainer<ForkVariables> trainer;
    static AgentNeuralInterface<ForkVariables> agent;
    static AgentForkNeural agentCasted;
    static ForkEnvironment environment;

    public static void main(String[] args) {
        environment=new ForkEnvironment();
        agent = AgentForkNeural.newWithDiscountFactor(environment, DISCOUNT_FACTOR);
        buildTrainer(NOF_EPIS, START_STATE, NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED);
        trainer.train();
        AgentInfo<ForkVariables> agentInfo=new AgentInfo<>(agent);
        List<List<Double>> listOfTrajectories=new ArrayList<>();
        List<Double> filtered1 = agentInfo.getFilteredTemporalDifferenceList(LENGTH_WINDOW);
        listOfTrajectories.add(filtered1);
        PlotterMultiplePanelsTrajectory plotter=new PlotterMultiplePanelsTrajectory(Collections.singletonList("Error"), "Step");
        plotter.plot(listOfTrajectories);
        DecimalFormat formatter = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US)); //US <=> only dots
        agentInfo.getStateValues(agentCasted.getMemory(), environment.stateSet())
                .forEach((s,v) -> System.out.println("s="+s+", v="+formatter.format(v)));
        ;

    }

    public static void buildTrainer(int nofEpis, int startPos, int nofSteps) {
        agentCasted=(AgentForkNeural) agent;
        NStepNeuralAgentTrainerSettings settings= NStepNeuralAgentTrainerSettings.builder()
                .probStart(0.25).probEnd(1e-3).nofIterations(1)
                .batchSize(BATCH_SIZE)
                .nofEpis(nofEpis)
                .nofStepsBetweenUpdatedAndBackuped(nofSteps)
                .build();

        trainer= NStepNeuralAgentTrainer.<ForkVariables>builder()
                .settings(settings)
                .startStateSupplier(() -> ForkState.newFromPos(startPos))
                .agentNeural(agent)
                .environment(environment)
                .build();

        System.out.println("buildTrainer");
    }



}
