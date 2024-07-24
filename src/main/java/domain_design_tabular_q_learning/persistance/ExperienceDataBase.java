package domain_design_tabular_q_learning.persistance;

import domain_design_tabular_q_learning.domain.trainer.entities.Experience;

import java.util.*;

public class ExperienceDataBase<V> implements DataBaseI<Experience<V>> {
    Map<Integer,Experience<V>> map;

    public  ExperienceDataBase() {  //todo singleton
        this.map = new HashMap<>();
    }

    public static <V> ExperienceDataBase<V> empty() {
        return new ExperienceDataBase<>();
    }

    @Override
    public void create(Experience<V> booking) {
        this.map.put(booking.getId(),booking);
    }

    @Override
    public Experience<V> read(Integer id) {
        return map.get(id);
    }

    @Override
    public void delete(Integer id) {
        map.remove(id);
    }

    @Override
    public List<Experience<V>> getAll() {
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
