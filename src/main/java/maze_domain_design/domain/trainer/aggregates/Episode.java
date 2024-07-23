package maze_domain_design.domain.trainer.aggregates;

import lombok.Getter;
import lombok.Setter;
import maze_domain_design.domain.trainer.entities.Experience;
import maze_domain_design.domain.trainer.value_objects.EpisodeInfoForRecording;
import maze_domain_design.persistance.ExperienceDataBase;

import java.util.List;

public class Episode {
    ExperienceDataBase experiences;
    @Setter @Getter
    EpisodeInfoForRecording infoForRecording;

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

    public double sumRewards() {
        return experiences.getAll().stream().mapToDouble(e -> e.getSars().r()).sum();
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
