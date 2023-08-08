package multi_step_temp_diff.domain.test_helpers;

import common.CpuTimer;
import common.MovingAverage;
import common.MultiplePanelsPlotter;
import lombok.Builder;
import lombok.SneakyThrows;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStep;
import multi_step_temp_diff.domain.agent_parts.ValueTracker;
import multi_step_temp_diff.domain.agents.charge.AgentChargeNeural;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.helpers.MockedReplayBufferCreatorCharge;

import java.util.List;

@Builder
public class AgentNeuralChargeTestHelper {

    public static final String PICS_FOLDER = "pics/";
    public static final int SLEEP_TIME = 1000;

    AgentNeuralInterface<ChargeVariables> agent;
    int nofIterations, iterationsBetweenPrints, batchLength, filterWindowLength;

    public void trainAgent(ReplayBufferNStep<ChargeVariables> buffer) {
        for (int i = 0; i < nofIterations; i++) {
            if(i % iterationsBetweenPrints ==0)
                System.out.println("i = " + i);
            agent.learn(buffer.getMiniBatch(batchLength));
        }
    }

    public void trainAgentTimeBudget(ReplayBufferNStep<ChargeVariables> buffer, long timeBudget) {
        CpuTimer timer=CpuTimer.newTimer(timeBudget);
        while (!timer.isTimeExceeded()) {
            agent.learn(buffer.getMiniBatch(batchLength));
        }
    }


    @SneakyThrows
    public void plotAndSaveErrorHistory(String fileName) {
        AgentChargeNeural agentCasted = (AgentChargeNeural) agent;
        ValueTracker errorTracker=agentCasted.getErrorHistory();
        MultiplePanelsPlotter plotter=new MultiplePanelsPlotter(List.of("Error "+fileName),"iter");
        MovingAverage movingAverage=new MovingAverage(
                filterWindowLength,errorTracker.getValueHistoryAbsoluteValues());
        plotter.plot(List.of(movingAverage.getFiltered()));
        Thread.sleep(SLEEP_TIME);
        plotter.saveImage(PICS_FOLDER +fileName);
    }

    public void reset(ChargeEnvironmentSettings settings, int bufferSize,  long timeBudget) {
        int dummy = 0;
        ChargeEnvironmentLambdas lambdas=new ChargeEnvironmentLambdas(settings);
        StateToValueFunctionContainerCharge container= new StateToValueFunctionContainerCharge(lambdas,settings, dummy);
        MockedReplayBufferCreatorCharge bufferCreator= MockedReplayBufferCreatorCharge.builder()
                .bufferSize(bufferSize).settings(settings).stateToValueFunction(container.fixedAtZero)
                .build();

        ReplayBufferNStep<ChargeVariables> buffer=bufferCreator.createExpReplayBuffer();
        trainAgentTimeBudget(buffer,timeBudget);

    }

}
