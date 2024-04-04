package policygradient.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.value_classes.ProgressMeasures;
import policy_gradient_problems.helpers.RecorderTrainingProgress;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestRecorderTrainingProgress {

    RecorderTrainingProgress recorder;

    @BeforeEach
    public void init() {
        recorder =new RecorderTrainingProgress();
    }

    @Test
    void whenEmpty_thenOk() {
        assertTrue(recorder.isEmpty());
        assertEquals(0, recorder.size());
    }

    @Test
    void whenAddOne_thenOk() {
        recorder.add(ProgressMeasures.ofAllZero());
        assertEquals(1, recorder.size());
        assertEquals(List.of(0), recorder.nStepsTraj());
    }

    @Test
    void whenAddingTwo_thenOk() {
        recorder.add(ProgressMeasures.ofAllZero().withNSteps(5));
        recorder.add(ProgressMeasures.ofAllZero().withNSteps(5));
        assertEquals(2, recorder.size());
        assertEquals(List.of(5,5), recorder.nStepsTraj());
    }



}
