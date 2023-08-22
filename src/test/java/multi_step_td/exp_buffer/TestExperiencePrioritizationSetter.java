package multi_step_td.exp_buffer;

import multi_step_temp_diff.domain.agent_parts.replay_buffer.utils.ExperiencePrioritizationSetter;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.NstepExperience;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.prio_strategy.PrioritizationProportional;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestExperiencePrioritizationSetter {


    @ParameterizedTest
    @CsvSource({"0.0, 0.0, 0", "0.1,0.1, 0.2", "-0.2,0.0, 0.2"
    })
    void givenTdError_thenCorrectPrio(ArgumentsAccessor arguments) {
        Double tdError = arguments.getDouble(0);
        Double epsilon = arguments.getDouble(1);
        Double expPrio = arguments.getDouble(2);

        List<NstepExperience<ChargeVariables>> buffer = new ArrayList<>();
        buffer.add(NstepExperience.<ChargeVariables>builder()
                .stateToUpdate(ChargeState.newDummy()).
                tdError(tdError).build());

        PrioritizationProportional<ChargeVariables> strategy = new PrioritizationProportional<>(epsilon);
        ExperiencePrioritizationSetter<ChargeVariables> prioritizationSetter =
                new ExperiencePrioritizationSetter<>(buffer, strategy);
        prioritizationSetter.setPrios();


        assertEquals(expPrio, buffer.get(0).prioritization);
    }


}
