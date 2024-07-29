package multi_agent_rl.domain.value_classes;

import lombok.Builder;
import lombok.NonNull;
import lombok.With;
import multi_agent_rl.domain.abstract_classes.*;

import java.util.Map;

/**
 * The consequence of taking action in stateNew, value is/can be defined later from list of experiences
 */

@Builder
public record Experience<V,O>(
        @NonNull StateI<V,O> state,
        @NonNull ActionJoint action,
        @With double reward,
        @NonNull StateI<V,O> stateNew,
        AgentActions<V,O> agentActions,
        boolean isTerminal,
        @With double value) {

    public static final int VALUE_DUMMY = 0;
    public static final double REWARD_DUMMY = 0d;



}
