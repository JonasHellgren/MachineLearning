package monte_carlo_tree_search.generic_interfaces;

public interface NodeValueMemoryInterface<SSV> {

    void write(StateInterface<SSV> state, double value);
    double read(StateInterface<SSV> state);

}
