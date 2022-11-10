package black_jack.models_memory;

import black_jack.models_cards.StateObserved;

public interface MemoryInterface {

    void clear();
    int nofItems();
    double read(StateObserved state);
    void write(StateObserved state, double Value);
    double average();
}
