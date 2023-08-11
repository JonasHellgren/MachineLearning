package multi_step_temp_diff.domain.helpers_specific;

import common.CpuTimer;
import lombok.NonNull;
import lombok.Builder;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_abstract.normalizer.NormalizerMeanStd;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStep;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import multi_step_temp_diff.domain.trainer_valueobj.NStepNeuralAgentTrainerSettings;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Supplier;

@Builder
public class ChargeAgentNeuralHelper {

    @NonNull  AgentNeuralInterface<ChargeVariables> agent;
    Integer nofIterations;

    @Builder.Default
    Integer iterationsBetweenPrints=100;
    @Builder.Default
    Integer batchLength=100;
    @Builder.Default
    Integer filterWindowLength=100;

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

    public void resetAgentMemory(ChargeEnvironmentSettings settings, int bufferSize, long timeBudget) {
        int dummy = 0;
        ChargeEnvironmentLambdas lambdas=new ChargeEnvironmentLambdas(settings);
        ChargeStateToValueFunctionContainer container= new ChargeStateToValueFunctionContainer(lambdas,settings, dummy);
        ChargeMockedReplayBufferCreator bufferCreator= ChargeMockedReplayBufferCreator.builder()
                .bufferSize(bufferSize).envSettings(settings).stateToValueFunction(container.fixedAtZero)
                .build();

        ReplayBufferNStep<ChargeVariables> buffer=bufferCreator.createExpReplayBuffer();
        trainAgentTimeBudget(buffer,timeBudget);
    }




}
