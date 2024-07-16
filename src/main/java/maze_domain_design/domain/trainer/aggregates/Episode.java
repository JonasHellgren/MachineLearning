package maze_domain_design.domain.trainer.aggregates;

import maze_domain_design.domain.trainer.entities.Experience;
import maze_domain_design.persistance.ExperienceDataBase;

import java.util.List;

public class Episode {
    ExperienceDataBase experiences;

    public Episode() {
        this.experiences = new ExperienceDataBase();
    }

    public void addExp(Experience e) {
        experiences.create(e);
    }

    public Experience getExp(Integer id) {
        return experiences.read(id);
    }
    public List<Integer> getIds() {
        return experiences.getAll().stream().map(e -> e.getId()).toList();
    }

    public void clear() {
        experiences.clear();
    }

    public int idMax() {
        return experiences.largestId();
    }

    public int size() {
        return experiences.size();
    }

}
