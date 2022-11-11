package black_jack.models_returns;

import black_jack.enums.CardAction;
import black_jack.models_cards.StateObservedObserved;
import black_jack.models_episode.Episode;
import black_jack.models_episode.EpisodeItem;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
public class ReturnsForEpisode {

    public static final double DISCOUNT_FACTOR_DEFAULT = 1d;

    private double discountFactor = DISCOUNT_FACTOR_DEFAULT;

    List<ReturnItem> returns;

    //Map<StateObserved,Double> stateReturnMap;


    public ReturnsForEpisode() {
        returns = new ArrayList<>();
    }

    public ReturnsForEpisode(double discountFactor) {
        this();
        this.discountFactor = discountFactor;
    }

    public int nofItems() {
        return returns.size();
    }

    public void clear() {
        returns.clear();
    }

    public void add(StateObservedObserved state, CardAction cardAction, Double returnValue) {
        returns.add(new ReturnItem(state, cardAction, returnValue));
    }

    public ReturnItem getItem(Integer index) {
        if (index < 0 || index >= nofItems()) {
            throw new IllegalArgumentException();
        }
        return returns.get(index);
    }

    public void appendReturns(Episode episode) {
        double G=0;
        for (int i = episode.nofItems()-1; i >=0 ; i--) {
            EpisodeItem ei= episode.getItem(i);
            G=G+ discountFactor *ei.reward;
            if (!episode.isStatePresentBeforeTimeStep(ei.state,i)) {
                this.add(ei.state,ei.action,G);  //todo, first visit flag
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ReturnItem ri : returns) {
            sb.append(ri.toString());
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

}
