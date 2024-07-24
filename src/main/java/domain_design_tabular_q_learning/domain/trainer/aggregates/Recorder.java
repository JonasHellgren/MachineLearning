package domain_design_tabular_q_learning.domain.trainer.aggregates;

import domain_design_tabular_q_learning.domain.trainer.entities.Recording;
import domain_design_tabular_q_learning.persistance.RecordingDataBase;

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
