package black_jack.models_memory;

public interface MemoryInterface<T> {

    void clear();
    int nofItems();
    double read(T state);
    void write(T state, double Value);
    double average();
}
