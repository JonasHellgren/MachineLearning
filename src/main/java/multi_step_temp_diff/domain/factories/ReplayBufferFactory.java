package multi_step_temp_diff.domain.factories;

import multi_step_temp_diff.domain.agent_abstract.ReplayBufferInterface;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStepPrioritized;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStepUniform;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;

public class ReplayBufferFactory<S> {

    String bufferType;
    int bufferSize;

    public ReplayBufferFactory(String bufferType, int bufferSize) {
        this.bufferType = bufferType;
        this.bufferSize = bufferSize;
    }

    public ReplayBufferInterface<S> create() {

        switch (bufferType) {

            case "Uniform" -> {
                return ReplayBufferNStepUniform.newFromMaxSize(bufferSize);
            }
            case "Prioritized" -> {
                return ReplayBufferNStepPrioritized.newFromMaxSize(bufferSize);
            }
            default -> throw new IllegalArgumentException("Not known buffer type");
        }

    }

}
