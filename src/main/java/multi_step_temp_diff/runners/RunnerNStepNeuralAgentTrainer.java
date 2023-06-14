package multi_step_temp_diff.runners;

import common.MultiplePanelsPlotter;
import multi_step_temp_diff.environments.ForkEnvironment;
import multi_step_temp_diff.helpers.AgentInfo;
import multi_step_temp_diff.helpers.NStepNeuralAgentTrainer;
import multi_step_temp_diff.interfaces_and_abstract.AgentNeuralInterface;
import multi_step_temp_diff.models.AgentForkNeural;

import java.util.*;

public class RunnerNStepNeuralAgentTrainer {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 5;
    private static final int BATCH_SIZE = 10;
    private static final int NOF_EPIS = 300;
    private static final int START_STATE = 0;
    private static final int LENGTH_WINDOW = 500;
    private static final int DISCOUNT_FACTOR = 1;
    static NStepNeuralAgentTrainer trainer;
    static AgentNeuralInterface agent;
    static AgentForkNeural agentCasted;
    static ForkEnvironment environment;

    public static void main(String[] args) {
        environment=new ForkEnvironment();
        agent = AgentForkNeural.newWithDiscountFactor(environment, DISCOUNT_FACTOR);
        buildTrainer(NOF_EPIS, START_STATE, NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED);
        trainer.train();
        AgentInfo agentInfo=new AgentInfo(agent);
        List<List<Double>> listOfTrajectories=new ArrayList<>();
        List<Double> filtered1 = agentInfo.getFilteredTemporalDifferenceList(LENGTH_WINDOW);
        listOfTrajectories.add(filtered1);
        MultiplePanelsPlotter plotter=new MultiplePanelsPlotter(Collections.singletonList("Error"), "Step");
        plotter.plot(listOfTrajectories);
        AgentInfo.getStateValues(agentCasted.getMemory(), environment.stateSet())
                .forEach((s,v) -> System.out.println("s="+s+", v="+v));
        ;

    }

    public static void buildTrainer(int nofEpis, int startState, int nofSteps) {
        agentCasted=(AgentForkNeural) agent;
        trainer= NStepNeuralAgentTrainer.builder()
                .nofStepsBetweenUpdatedAndBackuped(nofSteps)
                .startState(startState)
                .alpha(0.1)
                .nofEpisodes(nofEpis).batchSize(BATCH_SIZE).agentNeural(agent)
                .probStart(0.25).probEnd(1e-5).nofTrainingIterations(1)
                .environment(environment)
                .agentNeural(agent)
                .build();

        System.out.println("buildTrainer");
    }



}
