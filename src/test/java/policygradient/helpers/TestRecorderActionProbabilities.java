package policygradient.helpers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.helpers.RecorderActionProbabilities;

import java.util.List;
import java.util.Map;

class TestRecorderActionProbabilities {

    RecorderActionProbabilities recorder;

    @BeforeEach
    void init() {
        recorder = new RecorderActionProbabilities();
    }

    @Test
    void givenEmpty_thenCorrect() {
        Assertions.assertTrue(recorder.isEmpty());
        Assertions.assertEquals(0, recorder.nActions());
        Assertions.assertEquals(0, recorder.nStates());
    }

    @Test
    void givenFilledOneState_thenCorrect() {
        List<Double> t0 = List.of(0.1, 0.9);
        recorder.addStateProbabilitiesMap(Map.of(0, t0));
        List<Double> t1 = List.of(0.2, 0.8);
        recorder.addStateProbabilitiesMap(Map.of(0, t1));

        Assertions.assertEquals(1, recorder.nStates());
        Assertions.assertEquals(2, recorder.nActions());
        Assertions.assertEquals(List.of(t0, t1), recorder.probabilitiesForState(0));
    }

    @Test
    void givenFilledTwoStates_thenCorrect() {
        List<Double> t00 = List.of(0.1, 0.9);
        List<Double> t01 = List.of(0.4, 0.6);  //state 1 time 0
        List<Double> t10 = List.of(0.2, 0.8);  //state 0 time 1
        List<Double> t11 = List.of(0.5, 0.5);

        recorder.addStateProbabilitiesMap(Map.of(0, t00,1, t01));
        recorder.addStateProbabilitiesMap(Map.of(0, t10,1, t11));

        Assertions.assertEquals(2, recorder.nStates());
        Assertions.assertEquals(2, recorder.nActions());
        Assertions.assertEquals(List.of(t00, t10), recorder.probabilitiesForState(0));
    }


}
