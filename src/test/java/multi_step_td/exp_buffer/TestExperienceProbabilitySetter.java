package multi_step_td.exp_buffer;

import multi_step_temp_diff.domain.agent_parts.replay_buffer.ExperienceProbabilitySetter;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.NstepExperience;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestExperienceProbabilitySetter {

    List<NstepExperience<ChargeVariables>> buffer;
    List<Double> PRIO_LIST = List.of(2d, 1d, 1d);

    @BeforeEach
    public void init() {
        buffer = new ArrayList<>();
        for (Double prio : PRIO_LIST) {
            buffer.add(NstepExperience.<ChargeVariables>builder()
                    .stateToUpdate(ChargeState.newDummy()).
                    prioritization(prio).build());
        }
    }


    @Test
    public void whenBufferWithDifferentProsAndAlphaIsOne_thenCorrectProbability() {
        double alpha = 1d;
        ExperienceProbabilitySetter<ChargeVariables> probabilitySetter=new ExperienceProbabilitySetter<>(buffer, alpha);
        probabilitySetter.setProbabilities();
        List<Double> probabilities=buffer.stream().map(e -> e.probability).toList();
        System.out.println("probabilities = " + probabilities);
        double probSum = 2 + 1 + 1;
        assertTrue(probabilities.containsAll(List.of(2d/ probSum,1d/probSum)));
    }

    @Test
    public void whenBufferWithDifferentProsAndAlphaIsZero_thenSametProbability() {
        double alpha = 0d;
        ExperienceProbabilitySetter<ChargeVariables> probabilitySetter=new ExperienceProbabilitySetter<>(buffer, alpha);
        probabilitySetter.setProbabilities();
        List<Double> probabilities=buffer.stream().map(e -> e.probability).toList();
        assertTrue(probabilities.contains(1d/PRIO_LIST.size()));
    }

}
