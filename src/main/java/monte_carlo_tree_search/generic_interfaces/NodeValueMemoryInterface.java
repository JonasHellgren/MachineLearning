package monte_carlo_tree_search.generic_interfaces;

import org.bytedeco.opencv.presets.opencv_core;

public interface NodeValueMemoryInterface<SSV> {

    void write(StateInterface<SSV> state, double value);
    double read(StateInterface<SSV> state);
    void save(String fileName);
    void load(String fileName);


}
