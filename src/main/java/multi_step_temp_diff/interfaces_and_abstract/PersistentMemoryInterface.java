package multi_step_temp_diff.interfaces_and_abstract;

public interface PersistentMemoryInterface {
    void save(String fileName);
    void load(String fileName);
}
