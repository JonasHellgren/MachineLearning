package black_jack.models;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@ToString
public class Episode {

    List<EpisodeItem> episode;

    public Episode() {
        episode=new ArrayList<>();
    }

    public Integer nofItems() {
        return episode.size();
    }

    public void clear() {
        episode.clear();
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

    public EpisodeItem getEndItem() {
        if (nofItems()<1) {
            throw new NoSuchElementException("Empty episode");
        }

        return episode.get(nofItems()-1);
    }

}
