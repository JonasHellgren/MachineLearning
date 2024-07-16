package maze_domain_design.domain.trainer.aggregates;

import maze_domain_design.domain.trainer.entities.Recording;
import maze_domain_design.persistance.RecordingDataBase;

import java.util.List;

public class Recorder {
    RecordingDataBase recordings;

    public Recorder() {
        this.recordings = new RecordingDataBase();
    }

    public void addRecording(Recording r) {
        recordings.create(r);
    }

    public Recording getExp(Integer id) {
        return recordings.read(id);
    }
    public List<Integer> getIds() {
        return recordings.getAll().stream().map(e -> e.getId()).toList();
    }

    public void clear() {
        recordings.clear();
    }

    public int nextId() {
        return size()==0
                ? 0
                :recordings.largestId()+1;
    }

    public int size() {
        return recordings.size();
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append(System.lineSeparator());
        recordings.getAll().forEach( r ->
                sb.append(r).append(System.lineSeparator()));
        return sb.toString();
    }
}
