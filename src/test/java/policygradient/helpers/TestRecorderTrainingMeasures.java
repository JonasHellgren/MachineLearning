package policygradient.helpers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.value_classes.TrainingMeasures;
import policy_gradient_problems.helpers.RecorderTrainingMeasures;

import java.util.List;

class TestRecorderTrainingMeasures {

    RecorderTrainingMeasures recorderTrainingMeasures;

    @BeforeEach
    public void init() {
        recorderTrainingMeasures=new RecorderTrainingMeasures();
    }

    @Test
    void whenEmpty_thenOk() {
        Assertions.assertTrue(recorderTrainingMeasures.isEmpty());
        Assertions.assertEquals(0,recorderTrainingMeasures.size());
    }

    @Test
    void whenAddOne_thenOk() {
        recorderTrainingMeasures.add(TrainingMeasures.ofAllZero());
        Assertions.assertEquals(1,recorderTrainingMeasures.size());
        Assertions.assertEquals(List.of(0),recorderTrainingMeasures.nStepsTraj());
    }

    @Test
    void whenAddingTwo_thenOk() {
        recorderTrainingMeasures.add(TrainingMeasures.ofAllZero().withNSteps(5));
        recorderTrainingMeasures.add(TrainingMeasures.ofAllZero().withNSteps(5));
        Assertions.assertEquals(2,recorderTrainingMeasures.size());
        Assertions.assertEquals(List.of(5,5),recorderTrainingMeasures.nStepsTraj());
    }



}
