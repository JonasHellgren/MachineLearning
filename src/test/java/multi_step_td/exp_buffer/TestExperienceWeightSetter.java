package multi_step_td.exp_buffer;

import multi_step_temp_diff.domain.agent_parts.ExperiencePrioritizationSetter;
import multi_step_temp_diff.domain.agent_parts.ExperienceWeightSetter;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;
import multi_step_temp_diff.domain.agent_parts.PrioritizationProportional;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Formattable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 wVeci = 1*1/pi  wi = wVeci * 1/max(wVec)
 */

public class TestExperienceWeightSetter {

    @ParameterizedTest
    @CsvSource({"0.0, 1",   // prob ->  expWeight
            "0.2, 1", //prob = 0 => DUMMY_WEIGHT
    })
    void givenBetaZeroAndProbability_thenCorrectWeight(ArgumentsAccessor arguments) {
        double beta = 0d;
        Double prob = arguments.getDouble(0);
        Double expWeight = arguments.getDouble(1);

        List<NstepExperience<ChargeVariables>> buffer = new ArrayList<>();
        buffer.add(getExperienceWithProb(prob));

        ExperienceWeightSetter<ChargeVariables> experienceWeightSetter=new ExperienceWeightSetter<>(buffer, beta);
        experienceWeightSetter.setWeights();

        assertEquals(expWeight, buffer.get(0).weight);
    }

    @ParameterizedTest
    @CsvSource({"0.0,0.0, 1d,1d",   // prob0,prob1 ->  expWeight0, expWeight1
            "0.8, 0.2, 0.25,1.0",   //exp0 has 0.8 probability, 4 times higher than exp1, hence weight of exp0 is 4 times smaller
    })
    void givenBetaOneAndProbability_thenCorrectWeight(ArgumentsAccessor arguments) {
        double beta = 1d;
        Double prob0 = arguments.getDouble(0);
        Double prob1 = arguments.getDouble(1);
        Double expWeight0 = arguments.getDouble(2);
        Double expWeight1 = arguments.getDouble(3);

        List<NstepExperience<ChargeVariables>> buffer = new ArrayList<>();
        buffer.add(getExperienceWithProb(prob0));
        buffer.add(getExperienceWithProb(prob1));

        ExperienceWeightSetter<ChargeVariables> experienceWeightSetter=new ExperienceWeightSetter<>(buffer, beta);
        experienceWeightSetter.setWeights();

        System.out.println("buffer.get(0).weight = " + buffer.get(0).weight);
        System.out.println("buffer.get(1).weight = " + buffer.get(1).weight);

        assertEquals(expWeight0, buffer.get(0).weight);
        assertEquals(expWeight1, buffer.get(1).weight);

    }

    private static NstepExperience<ChargeVariables> getExperienceWithProb(Double prob0) {
        return NstepExperience.<ChargeVariables>builder()
                .stateToUpdate(ChargeState.newDummy()).
                probability(prob0).build();
    }


}
