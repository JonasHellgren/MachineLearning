package multi_step_td.common;

import multi_step_temp_diff.domain.agent_parts.replay_buffer.utils.RunningSum;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class TestRunningSum {
    public static final List<Double> EXPECTED_RUN_SUM = List.of(5d, 15d, 30d, 50d);

    @Test
    public void whenGivenIntegers_thenCorrectRunningSum() {
        List<Integer> numList=List.of(5,10,15,20);
        RunningSum<Integer> runningSum=new RunningSum<>(numList);
        Assertions.assertTrue(runningSum.calculate().containsAll(EXPECTED_RUN_SUM));
    }

    @Test
    public void whenGivenDoubles_thenCorrectRunningSum() {
        List<Double> numList=List.of(5d,10d,15d,20d);
        RunningSum<Double> runningSum=new RunningSum<>(numList);
        System.out.println("runningSum.calculate() = " + runningSum.calculate());
        Assertions.assertTrue(runningSum.calculate().containsAll(EXPECTED_RUN_SUM));
    }

}
