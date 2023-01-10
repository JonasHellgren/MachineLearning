package monte_carlo_tree_search.generic_interfaces;

import org.bytedeco.opencv.presets.opencv_core;

public interface MemoryInterface<SSV> {

    void write(StateInterface<SSV> state, double value);
    double read(StateInterface<SSV> state);

}
