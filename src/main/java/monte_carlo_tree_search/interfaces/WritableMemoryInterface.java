package monte_carlo_tree_search.interfaces;

public interface WritableMemoryInterface<SSV> {
        void write(StateInterface<SSV> state, double value);
}
