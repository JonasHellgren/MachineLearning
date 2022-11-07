package black_jack.models;

public interface MemoryInterface {

    void clear();
    int nofItems();
    double read(StateObserved state);
    void write(StateObserved state, double Value);
    double average();
}
