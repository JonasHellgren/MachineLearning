package black_jack.models_episode;

import black_jack.enums.CardAction;
import black_jack.models_cards.StateObservedActionObserved;
import black_jack.models_cards.StateObservedObserved;
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

    public void add(StateObservedObserved s, CardAction a, Double r) {
        episode.add(new EpisodeItem(s,a,r));
    }

    public void add(StateObservedActionObserved sa, Double r) {
        episode.add(new EpisodeItem(sa.getStateObserved(),sa.getCardAction(),r));
    }

    public EpisodeItem getItem(Integer timeStep) {
        throwExceptionIfNonValidTimeStep(timeStep);
        return episode.get(timeStep);
    }


    public EpisodeItem getEndItem() {
        if (nofItems()<1) {
            throw new NoSuchElementException("Empty episode");
        }

        return episode.get(nofItems()-1);
    }

    public boolean isStatePresentBeforeTimeStep(StateObservedObserved state, Integer timeStep) {
        throwExceptionIfNonValidTimeStep(timeStep);

        //todo replace with streams
        for (int i = 0; i < timeStep ; i++) {
            EpisodeItem ei=episode.get(i);
            if (state.equals(ei.state)) {
                return true;
            }
        }
        return false;
    }


    private void throwExceptionIfNonValidTimeStep(Integer timeStep) {
        if (timeStep <0 || timeStep >=nofItems()) {
            throw new IllegalArgumentException();
        }
    }

}
