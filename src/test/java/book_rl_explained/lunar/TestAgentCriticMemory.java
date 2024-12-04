package book_rl_explained.lunar;

import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.agent.CriticMemoryLunar;
import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import org.hellgren.utilities.random.RandUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import super_vised.radial_basis.RadialBasis;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestAgentCriticMemory {


    public static final double TOL = 0.9;
    public static final int N_FITS = 100;
    public static final double LEARNING_RATE = 0.5;
    LunarProperties lunarProperties;
    CriticMemoryLunar memory;
    AgentParameters agentParameters;

    @BeforeEach
    void init() {
        lunarProperties = LunarProperties.defaultProps();
        agentParameters = AgentParameters.defaultProps(lunarProperties).withLearningRate(LEARNING_RATE);
        memory = CriticMemoryLunar.zeroWeights(agentParameters, lunarProperties);
    }

    @Test
    void testCreateMemory() {
        var criticMemoryLunar = CriticMemoryLunar.zeroWeights(agentParameters, lunarProperties);
        RadialBasis rb = criticMemoryLunar.getMemory();
        assertEquals(agentParameters.nKernelsY() * agentParameters.nKernelsSpeed(), rb.getWeights().length);
    }

    @Test
    void testReadAnyState() {
        for (int i = 0; i < 10; i++) {
            StateLunar state = StateLunar.randomPosAndZeroSpeed(lunarProperties);
            assertEquals(0.0, memory.read(state));
        }
    }


    @Test
    void testFit_oneState() {
        StateLunar state = StateLunar.of(0.5, 0.5);
        int vTarget = 10;
        fitMemory(memory, state, vTarget);
        assertEquals(vTarget, memory.read(state), TOL);
    }

    @Test
    void testFit_twoDifferentYpos() {
        var states = List.of(StateLunar.of(5, 0), StateLunar.of(1, 0));
        var vTargets = List.of(10d, 1d);
        fitStatesToTargets(states, vTargets, memory);
        assertStatesAndTargets(states, vTargets, memory);
    }

    @Test
    void testFit_twoDifferentSpeeds() {
        var states = List.of(StateLunar.of(5, 0), StateLunar.of(5, lunarProperties.spdMax()));
        var vTargets = List.of(10d, 1d);
        fitStatesToTargets(states, vTargets, memory);
        assertStatesAndTargets(states, vTargets, memory);
    }

    @Test
    void testFit_AllStatesToTarget5() {
        double vTarget = 10;
        for (int i = 0; i < 100; i++) {
            StateLunar state = StateLunar.randomPosAndSpeed(lunarProperties);
            fitMemory(memory, state, vTarget);
        }

        var states = List.of(StateLunar.of(5, 0), StateLunar.of(1, 0));
        var vTargets = List.of(vTarget,vTarget);
        assertStatesAndTargets(states, vTargets, memory);
    }


    private static void assertStatesAndTargets(List<StateLunar> states, List<Double> vTargets, CriticMemoryLunar criticMemoryLunar) {
        for (StateLunar state : states) {
            double vTarget = vTargets.get(states.indexOf(state));
            assertEquals(vTarget, criticMemoryLunar.read(state), TOL);
        }
    }

    private static void fitStatesToTargets(List<StateLunar> states, List<Double> vTargets, CriticMemoryLunar criticMemoryLunar) {
        for (StateLunar state : states) {
            double vTarget = vTargets.get(states.indexOf(state));
            fitMemory(criticMemoryLunar, state, vTarget);
        }
    }


    private static void fitMemory(CriticMemoryLunar criticMemoryLunar, StateLunar state, double vTarget) {
        for (int i = 0; i < N_FITS; i++) {
            double error = vTarget - criticMemoryLunar.read(state);
            criticMemoryLunar.fit(state, error);
        }
    }


}
