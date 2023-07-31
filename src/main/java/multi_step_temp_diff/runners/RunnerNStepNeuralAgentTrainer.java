package multi_step_temp_diff.runners;

import common.MultiplePanelsPlotter;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironment;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.helpers.AgentInfo;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agents.fork.AgentForkNeural;

import java.util.*;

public class RunnerNStepNeuralAgentTrainer {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 5;
    private static final int BATCH_SIZE = 10;
    private static final int NOF_EPIS = 300;
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
        MultiplePanelsPlotter plotter=new MultiplePanelsPlotter(Collections.singletonList("Error"), "Step");
        plotter.plot(listOfTrajectories);
        agentInfo.getStateValues(agentCasted.getMemory(), environment.stateSet())
                .forEach((s,v) -> System.out.println("s="+s+", v="+v));
        ;

    }

    public static void buildTrainer(int nofEpis, int startPos, int nofSteps) {
        agentCasted=(AgentForkNeural) agent;
        trainer= NStepNeuralAgentTrainer.<ForkVariables>builder()
                .nofStepsBetweenUpdatedAndBackuped(nofSteps)
                .startStateSupplier(() -> ForkState.newFromPos(startPos))
                .alpha(0.1)
                .nofEpisodes(nofEpis).batchSize(BATCH_SIZE).agentNeural(agent)
                .probStart(0.25).probEnd(1e-5).nofTrainingIterations(1)
                .environment(environment)
                .agentNeural(agent)
                .build();

        System.out.println("buildTrainer");
    }



}