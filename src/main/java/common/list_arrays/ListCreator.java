package common.list_arrays;

import common.math.MathUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * A utility class for creating lists of doubles with various trainerParameters.
 */
public class ListCreator {

    private ListCreator() {
    }

    /**
     * Creates a list of doubles with the specified length, all initialized to 0.
     *
     * @param len the length of the list to create
     * @return a list of doubles with the specified length, all initialized to 0
     */
    public static List<Double> createListWithZeroElements(int len) {
        // Delegate to createListWithEqualElementValues with value 0
        return createListWithEqualElementValues(len, 0);
    }

    /**
     * Creates a list of doubles with the specified length, all initialized to the specified value.
     *
     * @param len   the length of the list to create
     * @param value the value to initialize all elements to
     * @return a list of doubles with the specified length, all initialized to the specified value
     */
    public static List<Double> createListWithEqualElementValues(int len, double value) {
        // Use Collections.nCopies to create a list with the specified length and value
        return new ArrayList<>(Collections.nCopies(len, value));
    }

    /**
     * Creates a list of doubles starting from the specified start value, incrementing by the specified step,
     * and ending at or below the specified end value.
     *
     * @param start the starting value of the list
     * @param end   the ending value of the list (inclusive)
     * @param step  the increment between each element
     * @return a list of doubles starting from the specified start value, incrementing by the specified step,
     * and ending at or below the specified end value
     */
    public static List<Double> createFromStartToEndWithStep(double start, double end, double step) {
        // Use DoubleStream.iterate to generate the sequence of values
        return DoubleStream.iterate(start, d -> step > 0 ? d <= end : d >= end, d -> d + step)
                .boxed()
                .collect(Collectors.toList());
    }

    /**
     * Creates a list of doubles starting from the specified start value, incrementing by the specified step,
     * and containing the specified number of elements.
     *
     * @param start    the starting value of the list
     * @param step     the increment between each element
     * @param nItems   the number of elements to generate
     * @return a list of doubles starting from the specified start value, incrementing by the specified step,
     * and containing the specified number of elements
     */
    public static List<Double> createFromStartWithStepWithNofItems(double start, double step, int nItems) {
        // Use Stream.iterate to generate the sequence of values
        return Stream.iterate(start, value -> value + step) // Start with 'start' and add 'step' for each subsequent element
                .limit(nItems)                        // Limit the sequence to 'nItems' elements
                .toList();
    }

    /**
     * Creates a list of doubles starting from the specified start value,
     * ending at the specified end value, and containing the specified number of elements.
     *
     * The list is generated by dividing the range between start and end into nItems - 1 equal parts.
     *
     * @param start    the starting value of the list
     * @param end      the ending value of the list (inclusive)
     * @param nItems   the number of elements to generate
     * @return a list of doubles starting from the specified start value,
     *         ending at the specified end value, and containing the specified number of elements
     */
    public static List<Double> createFromStartToEndWithNofItems(double start, double end, int nItems) {
        if (nItems == 0) {
            return Collections.emptyList();
        }
        if (nItems == 1) {
            return List.of(start);
        }

        double step = (end - start) / (nItems - 1);

        if (MathUtils.isZero(step)) {
            return createListWithEqualElementValues(nItems,start);
        }
        return createFromStartToEndWithStep(start, end, step);
    }

    /**
     * Creates a list of integers starting from the specified start value, incrementing by the specified step,
     * and containing the specified number of elements.
     *
     * @param start    the starting value of the list
     * @param step     the increment between each element
     * @param nItems   the number of elements to generate
     * @return a list of integers starting from the specified start value, incrementing by the specified step,
     *         and containing the specified number of elements
     */
    public static List<Integer> createFromStartWithStepWithNofItems(int start, int step, int nItems) {
        if (nItems == 0) {
            return Collections.emptyList();
        }
        if (nItems == 1) {
            return List.of(start);
        }
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < nItems; i++) {
            result.add(start + (i * step));
        }
        return result;
    }

    /**
     * Creates a mutable list containing 'n' copies of the specified element.
     *
     * @param <T> the type of the element to be copied
     * @param x   the element to be copied
     * @param n   the number of copies to create
     * @return a mutable list containing 'n' copies of the specified element
     */

    public static <T> List<T> nCopiesMutable(T x, int n) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            result.add(x);
        }
        return result;
    }
}