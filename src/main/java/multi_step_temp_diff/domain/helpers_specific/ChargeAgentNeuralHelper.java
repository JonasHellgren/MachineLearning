package multi_step_temp_diff.domain.helpers_specific;

import common.CpuTimer;
import lombok.NonNull;
import lombok.Builder;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.ReplayBufferNStepUniform;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;

import java.util.function.Function;

@Builder
public class ChargeAgentNeuralHelper {

    @NonNull AgentNeuralInterface<ChargeVariables> agent;
    Integer nofIterations;

    @Builder.Default
    Integer iterationsBetweenPrints=100;
    @Builder.Default
    Integer batchLength=100;
    @Builder.Default
    Integer filterWindowLength=100;

    public void trainAgent(ReplayBufferNStepUniform<ChargeVariables> buffer) {
        for (int i = 0; i < nofIterations; i++) {
            if(i % iterationsBetweenPrints ==0)
                System.out.println("i = " + i);
            agent.learn(buffer.getMiniBatch(batchLength));
        }
    }

    public void trainAgentTimeBudget(ReplayBufferNStepUniform<ChargeVariables> buffer, long timeBudget) {
        CpuTimer timer=CpuTimer.newWithTimeBudgetInMilliSec(timeBudget);
        while (!timer.isTimeExceeded()) {
            agent.learn(buffer.getMiniBatch(batchLength));
        }
    }

    public void resetAgentMemory(ChargeEnvironmentSettings settings, int bufferSize, long timeBudget) {
        ChargeEnvironmentLambdas lambdas=new ChargeEnvironmentLambdas(settings);
        ChargeStateToValueFunctionContainer container= new ChargeStateToValueFunctionContainer(lambdas,settings, 0);
        setAgentMemory(settings,bufferSize,timeBudget,container.fixedAtZero);
    }

    public void setAgentMemoryToOne(ChargeEnvironmentSettings settings, int bufferSize, long timeBudget) {
        ChargeEnvironmentLambdas lambdas=new ChargeEnvironmentLambdas(settings);
        ChargeStateToValueFunctionContainer container= new ChargeStateToValueFunctionContainer(lambdas,settings, 0);
        setAgentMemory(settings,bufferSize,timeBudget,container.fixedAtMinusTen);
    }

    public void setAgentMemory(ChargeEnvironmentSettings settings,
                               int bufferSize,
                               long timeBudget,
                               Function<ChargeState, Double> function) {
        ChargeMockedReplayBufferCreator bufferCreator= ChargeMockedReplayBufferCreator.builder()
                .bufferSize(bufferSize).envSettings(settings).stateToValueFunction(function)
                .build();
        ReplayBufferNStepUniform<ChargeVariables> buffer=bufferCreator.createExpReplayBuffer();
        trainAgentTimeBudget(buffer,timeBudget);
    }



}
