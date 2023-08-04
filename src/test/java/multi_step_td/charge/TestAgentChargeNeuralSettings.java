package multi_step_td.charge;

import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAgentChargeNeuralSettings {

    AgentChargeNeuralSettings agentChargeNeuralSettings;

    @Test
    public void whenDefault_thenCorrectValues() {
        agentChargeNeuralSettings = AgentChargeNeuralSettings.newDefault();
        out.println("agentChargeNeuralSettings = " + agentChargeNeuralSettings);

        assertEquals(29, agentChargeNeuralSettings.nofStates());
        assertEquals(0, agentChargeNeuralSettings.maxValue());

    }


    @Test
    public void whenSpecific_thenCorrectValues() {
        int startState = 5;
        double learningRate = 0.01;
        double discountFactor = 0.5;
        agentChargeNeuralSettings = AgentChargeNeuralSettings.builder()
                .discountFactor(discountFactor).learningRate(learningRate).startState(startState)
                .build();
        out.println("agentChargeNeuralSettings = " + agentChargeNeuralSettings);

        assertAll(
                () -> assertEquals(29, agentChargeNeuralSettings.nofStates()),
                () -> assertEquals(0, agentChargeNeuralSettings.maxValue()),
                () -> assertEquals(startState, agentChargeNeuralSettings.startState()),
                () -> assertEquals(learningRate, agentChargeNeuralSettings.learningRate()),
                () -> assertEquals(discountFactor, agentChargeNeuralSettings.discountFactor())
        );

    }

}
