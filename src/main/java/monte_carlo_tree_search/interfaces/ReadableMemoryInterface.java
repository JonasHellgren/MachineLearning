package monte_carlo_tree_search.interfaces;

public interface ReadableMemoryInterface<S> {
    double read(StateInterface<S> state);
}
