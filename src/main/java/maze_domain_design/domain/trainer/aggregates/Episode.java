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

    public int nextId() {
        return size()==0
        ? 0
        :experiences.largestId()+1;
    }

    public int size() {
        return experiences.size();
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append(System.lineSeparator());
        experiences.getAll().forEach( e ->
            sb.append(e).append(System.lineSeparator()));
        return sb.toString();
    }

}
