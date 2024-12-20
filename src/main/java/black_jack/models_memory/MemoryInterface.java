package black_jack.models_memory;

import black_jack.models_cards.StateInterface;
import black_jack.models_cards.StateObserved;

import java.util.Set;
import java.util.function.Predicate;

public interface MemoryInterface<T extends StateInterface> {

    void clear();
    int nofItems();
    double read(T state);
    double readBestValue(StateObserved state);
    void write(T state, double Value);
    double average();
    Set<Double> valuesOf(Predicate<StateObserved> p);
}
