package black_jack.models_memory;

import black_jack.models_cards.StateObserved;

import java.util.Set;
import java.util.function.Predicate;

public interface MemoryInterface<T> {

    void clear();
    int nofItems();
    double read(T state);
    void write(T state, double Value);
    double average();
    Set<Double> valuesOf(Predicate<StateObserved> p);
}
