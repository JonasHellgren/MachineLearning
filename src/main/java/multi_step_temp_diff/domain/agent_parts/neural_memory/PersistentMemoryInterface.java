package multi_step_temp_diff.domain.agent_parts.neural_memory;

public interface PersistentMemoryInterface {
    void save(String fileName);
    void load(String fileName);
}
