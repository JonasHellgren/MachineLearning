package maze_domain_design.persistance;

import maze_domain_design.domain.trainer.entities.Experience;

import java.util.*;

public class ExperienceDataBase implements DataBaseI<Experience> {
    Map<Integer,Experience> map;

    public ExperienceDataBase() {  //todo singleton
        this.map = new HashMap<>();
    }

    public static ExperienceDataBase empty() {
        return new ExperienceDataBase();
    }

    @Override
    public void create(Experience booking) {
        this.map.put(booking.getId(),booking);
    }

    @Override
    public Experience read(Integer id) {
        return map.get(id);
    }

    @Override
    public void delete(Integer id) {
        map.remove(id);
    }

    @Override
    public List<Experience> getAll() {
        return new ArrayList<>(map.values());
    }

    @Override
    public Integer largestId() {
        return getAll().isEmpty()
                ? 0
                : Collections.max(getAll().stream().map(Experience::getId).toList());
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
