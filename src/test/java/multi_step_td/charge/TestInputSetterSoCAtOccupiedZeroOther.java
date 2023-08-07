package multi_step_td.charge;

import lombok.Builder;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.HotEncodingSoCAtOccupiedElseZero;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.InputVectorSetterChargeInterface;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.normalizer.NormalizeMinMax;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestInputSetterSoCAtOccupiedZeroOther {

    public static final int TIME = 0;
    InputVectorSetterChargeInterface inputSetter;
    ChargeEnvironmentSettings environmentSettings;

    @BeforeEach
    public void init() {
        environmentSettings = ChargeEnvironmentSettings.newDefault();
        inputSetter=new HotEncodingSoCAtOccupiedElseZero(
                AgentChargeNeuralSettings.newDefault(),
                environmentSettings,
                new NormalizeMinMax(0,1));
    }

    @Builder
    record ArgumentReader(int posA, int posB, double socA, double socB,
                          double socSum) {

        public static ArgumentReader of(ArgumentsAccessor args) {
            return  ArgumentReader.builder()
                    .posA(args.getInteger(0)).posB(args.getInteger(1))
                    .socA(args.getDouble(2)).socB(args.getDouble(3))
                    .socSum(args.getDouble(4))
                    .build();
        }

        @NotNull
        ChargeState createState() {
            return new ChargeState(ChargeVariables.builder()
                    .posA(posA).posB(posB)
                    .socA(socA).socB(socB)
                    .time(TIME)
                    .build());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "0,1,0.5,0.5, 1.0","0,10,0.99,1.0, 1.99", "20,10,0.09,0.09, 0.18",  //both in site
            "0,29,0.5,0.5, 0.5", "29,0,0.5,0.5, 0.5", "10,29,0.28,0.8, 0.28",              //one in site
            "39,29,0.5,0.5, 0.0"              //none in site
    })
    public void whenDecoding_thenOneHotEncodingEqualToSoC(ArgumentsAccessor arguments) {
        ArgumentReader reader= ArgumentReader.of(arguments);
        ChargeState state = reader.createState();
        double[] inArr = inputSetter.defineInArray(state);
        System.out.println("inArr = " + Arrays.toString(inArr));

        assertEquals(reader.socSum,Arrays.stream(inArr).sum());
    }

}
