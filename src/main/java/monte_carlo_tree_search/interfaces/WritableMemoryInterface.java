package monte_carlo_tree_search.interfaces;

public interface WritableMemoryInterface<S> {
        void write(StateInterface<S> state, double value);
}
