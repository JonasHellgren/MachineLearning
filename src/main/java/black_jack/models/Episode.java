package black_jack.models;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class Episode {

    List<EpisodeItem> episode;

    public Episode() {
        episode=new ArrayList<>();
    }

    public Integer nofItems() {
        return episode.size();
    }

    public void add(StateObserved s, CardAction a, Double r) {
        episode.add(new EpisodeItem(s,a,r));
    }

    public EpisodeItem getItem(Integer index) {
        if (index<0 || index>=nofItems()) {
            throw new IllegalArgumentException();
        }
        return episode.get(index);
    }

}
