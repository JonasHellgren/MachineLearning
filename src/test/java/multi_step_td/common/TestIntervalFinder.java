package multi_step_td.common;

import multi_step_temp_diff.domain.helpers_common.IntervalFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestIntervalFinder {

    public static final List<Double> ACCUMULATED_PROBABILITIES = List.of(0d, 0.2, 0.4, 0.8, 1d);
    public static final List<Double> ACCUMULATED_PROBABILITIES_TWO_EQUAL = List.of(0d, 0.2, 0.4, 0.4, 0.8, 1d);

    @ParameterizedTest
    @CsvSource({"0.0, 0", "0.1, 0", "0.2, 1", "0.22, 1", "0.44, 2", "0.85, 3", "0.99, 3", "1.0, 3"
    })
    void givenNumber_thenCorrectInterval(ArgumentsAccessor arguments) {
        IntervalFinder intervalFinder = IntervalFinder.newNoArgumentCheck(ACCUMULATED_PROBABILITIES);
        Double x = arguments.getDouble(0);
        Integer expectedInterval = arguments.getInteger(1);

        assertEquals(expectedInterval, intervalFinder.find(x));
        System.out.println("Nof iterations = " + intervalFinder.getNofIterations());
    }

    @Test
    void givenNumberOutside_thenThrows() {
        IntervalFinder intervalFinder = IntervalFinder.newArgumentCheck(ACCUMULATED_PROBABILITIES);
        assertThrows(IllegalArgumentException.class, () -> intervalFinder.find(-1d));
    }

    @Test
    void givenNotAscending_thenThrows() {
        assertThrows(IllegalArgumentException.class,() -> IntervalFinder.newArgumentCheck(List.of(1d,2d,1.5)));
    }

    @ParameterizedTest
    @CsvSource({"0.399, 2", "0.4, 2", "0.4001, 2", })
    void givenTwoEqualAccumProb_thenCorrespondingIntervalNeverSelected(ArgumentsAccessor arguments) {
        IntervalFinder intervalFinder = IntervalFinder.newNoArgumentCheck(ACCUMULATED_PROBABILITIES_TWO_EQUAL);
        Double x = arguments.getDouble(0);
        Integer nonExpectedInterval = arguments.getInteger(1);
        System.out.println("x = "+x+", intervalFinder.find(x) = " + intervalFinder.find(x));

        assertNotEquals(nonExpectedInterval, intervalFinder.find(x));
    }


}
