package multi_step_td.charge;

import multi_step_td.test_helpers.ArgumentReaderInputSetter;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.HotEncodingOneAtOccupiedSoCsSeparate;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.InputVectorSetterChargeInterface;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.normalizer.NormalizeMinMax;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestHotEncodingOneAtOccupiedSoCsSeparate {
    InputVectorSetterChargeInterface inputSetter;
    ChargeEnvironmentSettings environmentSettings;

    @BeforeEach
    public void init() {
        environmentSettings = ChargeEnvironmentSettings.newDefault();
        inputSetter=new HotEncodingOneAtOccupiedSoCsSeparate(
                AgentChargeNeuralSettings.newDefault(),
                environmentSettings,
                new NormalizeMinMax(0,1));
    }


    @ParameterizedTest
    @CsvSource({
            "0,1,0.5,0.5, 3.0","0,10,0.99,1.0, 3.99", "20,10,0.09,0.09, 2.18",  //both in site
            "0,29,0.5,0.5, 1.5", "29,0,0.5,0.5, 1.5", "10,29,0.28,0.8, 1.28",   //one in site
            "39,29,0.5,0.5, 0.0"              //none in site
    })
    public void whenDecoding_thenOneHotEncodingEqualToSoC(ArgumentsAccessor arguments) {
        ArgumentReaderInputSetter reader= ArgumentReaderInputSetter.of(arguments);
        ChargeState state = reader.createState();
        double[] inArr = inputSetter.defineInArray(state);
        System.out.println("inArr = " + Arrays.toString(inArr));

        assertEquals(reader.checkSum(),Arrays.stream(inArr).sum());  //checksum = sum of socs and two ones
    }

}
