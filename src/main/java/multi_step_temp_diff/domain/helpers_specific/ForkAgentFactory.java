package multi_step_temp_diff.domain.helpers_specific;

import lombok.Builder;
import lombok.NonNull;
import multi_step_temp_diff.domain.agent_abstract.ReplayBufferInterface;
import multi_step_temp_diff.domain.agent_abstract.normalizer.NormalizerMeanStd;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import multi_step_temp_diff.domain.agents.fork.AgentForkNeural;
import multi_step_temp_diff.domain.environment_valueobj.ForkEnvironmentSettings;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironment;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import multi_step_temp_diff.domain.trainer_valueobj.NStepNeuralAgentTrainerSettings;
import org.neuroph.util.TransferFunctionType;

import java.util.List;

@Builder
public class ForkAgentFactory {

    @NonNull  ForkEnvironment environment;
    @NonNull Double minOut;
    @NonNull Double maxOut;
    @NonNull Double learningRate;
    @NonNull Integer nofHiddenLayers = 1;
    @NonNull Double discountFactor;

    public AgentForkNeural buildAgent() {

        ForkEnvironmentSettings envSettings=ForkEnvironmentSettings.getDefault();


        NetSettings netSettings = NetSettings.builder()
                .learningRate(learningRate)
                .inputSize(envSettings.nofStates()).nofHiddenLayers(nofHiddenLayers).nofNeuronsHidden(envSettings.nofStates())
                .transferFunctionType(TransferFunctionType.TANH)
                .minOut(minOut).maxOut(maxOut)
                .normalizer(new NormalizerMeanStd(List.of(10 * minOut, 10 * maxOut, 0d, 0d, 0d))).build();
        //        .normalizer(new NormalizeMinMax(minOut*2,maxOut*2)).build();  //also works

        return AgentForkNeural.newWithDiscountFactorAndMemorySettings(
                environment, discountFactor, netSettings);
    }


}
