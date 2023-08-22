package multi_step_temp_diff.domain.agents.fork;

import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import multi_step_temp_diff.domain.agent_abstract.*;
import multi_step_temp_diff.domain.agent_parts.neural_memory.NetworkMemoryInterface;
import multi_step_temp_diff.domain.agent_valueobj.AgentForkNeuralSettings;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.NstepExperience;

import java.util.*;

/**
 * http://www.incompleteideas.net/papers/fernando-sutton-2019.pdf
 * https://arxiv.org/abs/1810.09967
 * https://openreview.net/pdf?id=X6YPReSv5CX
 * https://arxiv.org/pdf/1602.04621.pdf
 * <p>
 * https://www.baeldung.com/lombok-builder-inheritance
 */

@Getter
public class AgentForkNeural extends AgentAbstract<ForkVariables> implements AgentNeuralInterface<ForkVariables> {

    AgentForkNeuralSettings settings;
    NetworkMemoryInterface<ForkVariables> memory;

    @Builder
    public AgentForkNeural(EnvironmentInterface<ForkVariables> environment,
                           StateInterface<ForkVariables> state,
                           AgentForkNeuralSettings settings) {
        super(environment, state, settings);
        this.settings = settings;
        this.memory = getSettings().memory();
    }

    public static AgentForkNeural newWithSettings(EnvironmentInterface<ForkVariables> environment,
                                                  AgentForkNeuralSettings settings) {
        return new AgentForkNeural(environment, new ForkState(ForkVariables.newFromPos(settings.startState())), settings);
    }

    public static AgentForkNeural newDefault(EnvironmentInterface<ForkVariables> environment) {
        var settings = AgentForkNeuralSettings.getDefault();
        return newWithSettings(environment, settings);
    }

    public static AgentForkNeural newWithDiscountFactor(EnvironmentInterface<ForkVariables> environment,
                                                        double discountFactor) {
        var settings = AgentForkNeuralSettings.getWithDiscountFactor(discountFactor);
        return newWithSettings(environment, settings);
    }

    public static AgentForkNeural newWithDiscountFactorAndMemorySettings(EnvironmentInterface<ForkVariables> environment,
                                                                         double discountFactor,
                                                                         NetSettings netSettings) {
        var settings = AgentForkNeuralSettings.getWithDiscountFactorAndMemorySettings(discountFactor, netSettings);
        return newWithSettings(environment, settings);
    }


    @Override
    public double readValue(StateInterface<ForkVariables> state) {
        return memory.read(state);
    }

    @Override
    public void learn(List<NstepExperience<ForkVariables>> miniBatch) {
        memory.learn(miniBatch);
    }

    @SneakyThrows
    @Override
    public void saveMemory(String fileName) {
        throw new NoSuchMethodException("Fork");
    }

    @SneakyThrows
    @Override
    public void loadMemory(String fileName) {
        throw new NoSuchMethodException("Fork");
    }


}
