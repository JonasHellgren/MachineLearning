package maze_domain_design.persistance;

import maze_domain_design.domain.trainer.entities.Experience;
import maze_domain_design.domain.trainer.entities.Recording;

import java.util.*;

public class RecordingDataBase implements DataBaseI<Recording> {
    Map<Integer, Recording> map;

    public RecordingDataBase() {  //todo singleton
        this.map = new HashMap<>();
    }

    public static ExperienceDataBase empty() {
        return new ExperienceDataBase();
    }

    @Override
    public void create(Recording r) {
        this.map.put(r.getId(),r);
    }

    @Override
    public Recording read(Integer id) {
        return map.get(id);
    }

    @Override
    public void delete(Integer id) {
        map.remove(id);
    }

    @Override
    public List<Recording> getAll() {
        return new ArrayList<>(map.values());
    }

    @Override
    public Integer largestId() {
        return getAll().isEmpty()
                ? 0
                : Collections.max(getAll().stream().map(Recording::getId).toList());
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public int size() {
        return map.size();
    }
}
