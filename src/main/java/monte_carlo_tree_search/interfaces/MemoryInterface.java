package monte_carlo_tree_search.interfaces;

public interface MemoryInterface<SSV> {

    void write(StateInterface<SSV> state, double value);
    double read(StateInterface<SSV> state);

}
