package monte_carlo_tree_search.interfaces;

public interface MemoryInterface<SSV> {
    double read(StateInterface<SSV> state);
    //void write(StateInterface<SSV> state, double value);
}
