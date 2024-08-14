package safe_rl.domain.trainer.value_objects;


import lombok.NonNull;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.environment.EnvironmentI;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.safety_layer.SafetyLayer;

import java.util.function.Supplier;

public record TrainerExternal<V>(
        @NonNull EnvironmentI<V> environment,
        @NonNull AgentACDiscoI<V> agent,
        @NonNull SafetyLayer<V> safetyLayer,
        Supplier<StateI<V>> startStateSupplier
) {
}
